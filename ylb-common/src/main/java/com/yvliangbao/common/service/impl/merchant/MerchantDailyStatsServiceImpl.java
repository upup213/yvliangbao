package com.yvliangbao.common.service.impl.merchant;


import com.yvliangbao.common.mapper.merchant.MerchantDailyStatsMapper;
import com.yvliangbao.common.mapper.merchant.MerchantInfoMapper;
import com.yvliangbao.common.mapper.order.OrderInfoMapper;
import com.yvliangbao.common.pojo.entity.merchant.MerchantDailyStats;
import com.yvliangbao.common.pojo.entity.merchant.MerchantInfo;
import com.yvliangbao.common.service.common.CacheService;
import com.yvliangbao.common.service.merchant.MerchantDailyStatsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 商户每日统计服务实现
 *
 * @author 余量宝
 */
@Slf4j
@Service
public class MerchantDailyStatsServiceImpl implements MerchantDailyStatsService {

    private static final String LOCK_KEY = "lock:daily:stats";
    private static final long LOCK_EXPIRE_SECONDS = 3500;

    @Autowired
    private MerchantDailyStatsMapper merchantDailyStatsMapper;

    @Autowired
    private MerchantInfoMapper merchantInfoMapper;

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public MerchantDailyStats calculateDailyStats(Long merchantId, LocalDate date) {
        log.info("计算商户每日统计: merchantId={}, date={}", merchantId, date);

        String startDate = date.toString();
        String endDate = date.toString();

        // 从订单表查询统计数据
        Long revenue = orderInfoMapper.sumMerchantRevenueByRange(merchantId, startDate, endDate);
        Integer orderCount = orderInfoMapper.countMerchantOrdersByRange(merchantId, startDate, endDate);
        Long refundAmount = orderInfoMapper.sumMerchantRefundByRange(merchantId, startDate, endDate);

        // 查询核销数据（state=3 表示已核销）
        Integer verifiedCount = orderInfoMapper.countMerchantVerifiedByRange(merchantId, startDate, endDate);
        Long verifiedAmount = orderInfoMapper.sumMerchantVerifiedByRange(merchantId, startDate, endDate);

        MerchantDailyStats stats = new MerchantDailyStats();
        stats.setMerchantId(merchantId);
        stats.setStatDate(date);
        stats.setRevenue(revenue != null ? revenue : 0L);
        stats.setOrderCount(orderCount != null ? orderCount : 0);
        stats.setRefundAmount(refundAmount != null ? refundAmount : 0L);
        stats.setVerifiedCount(verifiedCount != null ? verifiedCount : 0);
        stats.setVerifiedAmount(verifiedAmount != null ? verifiedAmount : 0L);

        return stats;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void calculateAllMerchantsDailyStats(LocalDate date) {
        log.info("开始计算所有商户每日统计: date={}", date);

        // 查询所有商户
        List<MerchantInfo> merchants = merchantInfoMapper.selectList(null);
        if (merchants.isEmpty()) {
            log.info("没有商户需要统计");
            return;
        }

        List<MerchantDailyStats> statsList = new ArrayList<>();
        for (MerchantInfo merchant : merchants) {
            try {
                MerchantDailyStats stats = calculateDailyStats(merchant.getId(), date);
                statsList.add(stats);
            } catch (Exception e) {
                log.error("计算商户统计失败: merchantId={}, error={}", merchant.getId(), e.getMessage());
            }
        }

        // 批量插入或更新
        if (!statsList.isEmpty()) {
            try {
                merchantDailyStatsMapper.batchInsertOrUpdate(statsList);
                log.info("批量保存商户每日统计成功: count={}", statsList.size());
            } catch (Exception e) {
                log.error("批量保存商户每日统计失败: error={}", e.getMessage());
                // 逐条保存
                for (MerchantDailyStats stats : statsList) {
                    try {
                        merchantDailyStatsMapper.insert(stats);
                    } catch (Exception ex) {
                        log.warn("保存单条统计失败: merchantId={}, date={}", stats.getMerchantId(), stats.getStatDate());
                    }
                }
            }
        }
    }

    @Override
    public List<MerchantDailyStats> getMerchantStatsByDateRange(Long merchantId, LocalDate startDate, LocalDate endDate) {
        List<java.util.Map<String, Object>> data = merchantDailyStatsMapper.selectByDateRange(merchantId, startDate, endDate);
        List<MerchantDailyStats> result = new ArrayList<>();
        for (java.util.Map<String, Object> row : data) {
            MerchantDailyStats stats = new MerchantDailyStats();
            stats.setMerchantId(merchantId);
            stats.setStatDate(LocalDate.parse(row.get("date").toString()));
            stats.setRevenue(((Number) row.get("revenue")).longValue());
            stats.setOrderCount(((Number) row.get("order_count")).intValue());
            stats.setRefundAmount(((Number) row.get("refund_amount")).longValue());
            stats.setVerifiedCount(((Number) row.get("verified_count")).intValue());
            stats.setVerifiedAmount(((Number) row.get("verified_amount")).longValue());
            result.add(stats);
        }
        return result;
    }

    /**
     * 每天凌晨1点执行统计任务
     * 计算昨天的统计数据
     */
    @Override
    @Scheduled(cron = "0 0 1 * * ?")
    public void executeDailyStatsTask() {
        String lockValue = UUID.randomUUID().toString();
        
        try {
            Boolean acquired = stringRedisTemplate.opsForValue()
                    .setIfAbsent(LOCK_KEY, lockValue, LOCK_EXPIRE_SECONDS, TimeUnit.SECONDS);
            
            if (!Boolean.TRUE.equals(acquired)) {
                log.debug("获取分布式锁失败，另一实例正在执行");
                return;
            }
            
            log.info("========== 开始执行每日统计任务 ==========");
            long startTime = System.currentTimeMillis();

            LocalDate yesterday = LocalDate.now().minusDays(1);
            calculateAllMerchantsDailyStats(yesterday);

            // 清除所有商户的统计缓存
            clearAllMerchantStatsCache();

            long cost = System.currentTimeMillis() - startTime;
            log.info("========== 每日统计任务完成, 耗时: {}ms ==========", cost);
        } catch (Exception e) {
            log.error("每日统计任务执行失败: {}", e.getMessage(), e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void backfillHistoricalStats(Long merchantId, LocalDate startDate, LocalDate endDate) {
        log.info("开始补算历史数据: merchantId={}, startDate={}, endDate={}", merchantId, startDate, endDate);

        List<MerchantDailyStats> statsList = new ArrayList<>();
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            if (merchantId != null) {
                // 指定商户
                MerchantDailyStats stats = calculateDailyStats(merchantId, currentDate);
                statsList.add(stats);
            } else {
                // 所有商户
                List<MerchantInfo> merchants = merchantInfoMapper.selectList(null);
                for (MerchantInfo merchant : merchants) {
                    MerchantDailyStats stats = calculateDailyStats(merchant.getId(), currentDate);
                    statsList.add(stats);
                }
            }
            currentDate = currentDate.plusDays(1);

            // 每100条批量保存一次
            if (statsList.size() >= 100) {
                merchantDailyStatsMapper.batchInsertOrUpdate(statsList);
                statsList.clear();
            }
        }

        // 保存剩余数据
        if (!statsList.isEmpty()) {
            merchantDailyStatsMapper.batchInsertOrUpdate(statsList);
        }

        log.info("历史数据补算完成: 共{}条记录", statsList.size());
    }

    /**
     * 清除所有商户的统计缓存
     */
    private void clearAllMerchantStatsCache() {
        try {
            List<MerchantInfo> merchants = merchantInfoMapper.selectList(null);
            for (MerchantInfo merchant : merchants) {
                cacheService.deleteMerchantStatsCache(merchant.getId());
                cacheService.deleteMerchantRevenueTrendCache(merchant.getId());
                cacheService.deleteMerchantSettlementOverviewCache(merchant.getId());
            }
            log.info("清除商户统计缓存完成: count={}", merchants.size());
        } catch (Exception e) {
            log.warn("清除商户统计缓存失败: {}", e.getMessage());
        }
    }
}
