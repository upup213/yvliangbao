package com.yvliangbao.common.service.impl.merchant;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yvliangbao.common.exception.BusinessException;
import com.yvliangbao.common.mapper.merchant.MerchantDailyStatsMapper;
import com.yvliangbao.common.mapper.merchant.MerchantInfoMapper;
import com.yvliangbao.common.mapper.merchant.StoreInfoMapper;
import com.yvliangbao.common.mapper.order.OrderInfoMapper;
import com.yvliangbao.common.pojo.dto.merchant.MerchantCompleteInfoDTO;
import com.yvliangbao.common.pojo.dto.merchant.MerchantLoginDTO;
import com.yvliangbao.common.pojo.dto.merchant.MerchantRegisterDTO;
import com.yvliangbao.common.pojo.dto.merchant.MerchantSimpleRegisterDTO;
import com.yvliangbao.common.pojo.entity.merchant.MerchantDailyStats;
import com.yvliangbao.common.pojo.entity.merchant.MerchantInfo;
import com.yvliangbao.common.pojo.enums.MerchantStatus;
import com.yvliangbao.common.pojo.vo.merchant.*;
import com.yvliangbao.common.service.common.CacheService;
import com.yvliangbao.common.service.merchant.MerchantInfoService;
import com.yvliangbao.common.service.merchant.ProductInfoService;
import com.yvliangbao.common.util.AESUtil;
import com.yvliangbao.common.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 商户信息 Service 实现
 *
 * @author 余量宝
 */
@Slf4j
@Service
public class MerchantInfoServiceImpl extends ServiceImpl<MerchantInfoMapper, MerchantInfo> implements MerchantInfoService {

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private MerchantDailyStatsMapper merchantDailyStatsMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private StoreInfoMapper storeInfoMapper;

    @Autowired
    private ProductInfoService productInfoService;

    @Autowired
    private AESUtil aesUtil;

    @Override
    public MerchantInfo getByMerchantNo(String merchantNo) {
        return this.lambdaQuery()
                .eq(MerchantInfo::getMerchantNo, merchantNo)
                .one();
    }

    @Override
    public MerchantInfo getByContactPhone(String phone) {
        return this.lambdaQuery()
                .eq(MerchantInfo::getContactPhone, phone)
                .one();
    }

    @Override
    public String generateMerchantNo() {
        // 生成商户编号：YL + 时间戳 + 4位随机数
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.format("%04d", ThreadLocalRandom.current().nextInt(10000));
        return "YL" + timestamp + random;
    }

    @Override
    public MerchantInfoVO register(MerchantRegisterDTO dto) {
        log.info("商户入驻开始: contactPhone={}", dto.getContactPhone());
        
        try {
            // 检查手机号是否已注册
            MerchantInfo existMerchant = this.getByContactPhone(dto.getContactPhone());
            if (existMerchant != null) {
                log.warn("商户入驻失败: 手机号已注册, contactPhone={}", dto.getContactPhone());
                throw new BusinessException("该手机号已注册");
            }

            // 创建商户
            MerchantInfo merchant = new MerchantInfo();
            BeanUtils.copyProperties(dto, merchant);
            merchant.setMerchantNo(this.generateMerchantNo());
            merchant.setStatus(MerchantStatus.PENDING_AUDIT.getCode()); // 待审核
            this.save(merchant);

            log.info("商户入驻成功: merchantId={}, merchantNo={}", merchant.getId(), merchant.getMerchantNo());

            // 生成 token
            String token = JwtUtil.generateToken(merchant.getId(), merchant.getMerchantNo());

            // 构造响应
            MerchantInfoVO vo = new MerchantInfoVO();
            BeanUtils.copyProperties(merchant, vo);
            vo.setToken(token);

            return vo;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("商户入驻异常: contactPhone={}, error={}", dto.getContactPhone(), e.getMessage(), e);
            throw new BusinessException("商户入驻失败：" + e.getMessage());
        }
    }

    @Override
    public MerchantInfoVO simpleRegister(MerchantSimpleRegisterDTO dto) {
        log.info("商户简单注册开始: contactPhone={}", dto.getContactPhone());

        try {
            // 检查手机号是否已注册
            MerchantInfo existMerchant = this.getByContactPhone(dto.getContactPhone());
            if (existMerchant != null) {
                log.warn("商户注册失败: 手机号已注册, contactPhone={}", dto.getContactPhone());
                throw new BusinessException("该手机号已注册");
            }

            // 创建商户
            MerchantInfo merchant = new MerchantInfo();
            merchant.setMerchantNo(this.generateMerchantNo());
            merchant.setMerchantName(dto.getMerchantName());
            merchant.setContactName(dto.getContactName());
            merchant.setContactPhone(dto.getContactPhone());
            merchant.setPassword(passwordEncoder.encode(dto.getPassword()));
            merchant.setStatus(MerchantStatus.PENDING_AUDIT.getCode()); // 待审核
            this.save(merchant);

            log.info("商户注册成功: merchantId={}, merchantNo={}", merchant.getId(), merchant.getMerchantNo());

            // 生成 token
            String token = JwtUtil.generateToken(merchant.getId(), merchant.getMerchantNo());

            // 构造响应
            MerchantInfoVO vo = new MerchantInfoVO();
            BeanUtils.copyProperties(merchant, vo);
            vo.setToken(token);

            return vo;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("商户注册异常: contactPhone={}, error={}", dto.getContactPhone(), e.getMessage(), e);
            throw new BusinessException("注册失败：" + e.getMessage());
        }
    }

    @Override
    public MerchantInfoVO login(MerchantLoginDTO dto) {
        log.info("商户登录开始: contactPhone={}", dto.getContactPhone());

        try {
            // 查询商户
            MerchantInfo merchant = this.getByContactPhone(dto.getContactPhone());
            if (merchant == null) {
                log.warn("商户登录失败: 商户不存在, contactPhone={}", dto.getContactPhone());
                throw new BusinessException("商户不存在");
            }

            // 验证密码
            if (merchant.getPassword() == null || !passwordEncoder.matches(dto.getPassword(), merchant.getPassword())) {
                log.warn("商户登录失败: 密码错误, contactPhone={}", dto.getContactPhone());
                throw new BusinessException("密码错误");
            }

            // 检查状态
            if (MerchantStatus.DISABLED.getCode().equals(merchant.getStatus())) {
                log.warn("商户登录失败: 商户已被禁用, merchantId={}", merchant.getId());
                throw new BusinessException("商户已被禁用");
            }

            // 生成 token
            String token = JwtUtil.generateToken(merchant.getId(), merchant.getMerchantNo());

            // 构造响应
            MerchantInfoVO vo = new MerchantInfoVO();
            BeanUtils.copyProperties(merchant, vo);
            vo.setToken(token);

            log.info("商户登录成功: merchantId={}, merchantNo={}", merchant.getId(), merchant.getMerchantNo());

            return vo;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("商户登录异常: contactPhone={}, error={}", dto.getContactPhone(), e.getMessage(), e);
            throw new BusinessException("登录失败：" + e.getMessage());
        }
    }

    @Override
    public MerchantInfoVO getMerchantInfo(Long merchantId) {
        log.debug("获取商户信息: merchantId={}", merchantId);

        // 查询商户信息
        MerchantInfo merchant = this.getById(merchantId);
        if (merchant == null) {
            log.warn("获取商户信息失败: 商户不存在, merchantId={}", merchantId);
            throw new BusinessException("商户不存在");
        }

        // 构造响应
        MerchantInfoVO vo = new MerchantInfoVO();
        BeanUtils.copyProperties(merchant, vo);

        // 敏感字段脱敏返回
        if (vo.getLegalPersonName() != null) {
            try {
                vo.setLegalPersonName(aesUtil.decrypt(vo.getLegalPersonName()));
            } catch (Exception e) {
                log.warn("法人姓名解密失败，使用密文: {}", e.getMessage());
            }
        }
        if (vo.getLegalPersonIdCard() != null) {
            vo.setLegalPersonIdCard(AESUtil.maskIdCard(vo.getLegalPersonIdCard()));
        }
        if (vo.getBankAccount() != null) {
            vo.setBankAccount(AESUtil.maskBankAccount(vo.getBankAccount()));
        }

        log.debug("获取商户信息成功: merchantId={}", merchantId);

        return vo;
    }

    @Override
    public MerchantInfoVO completeInfo(Long merchantId, MerchantCompleteInfoDTO dto) {
        log.info("完善商户信息: merchantId={}", merchantId);

        MerchantInfo merchant = this.getById(merchantId);
        if (merchant == null) {
            throw new BusinessException("商户不存在");
        }

        // 更新商户信息
        if (dto.getMerchantName() != null) {
            merchant.setMerchantName(dto.getMerchantName());
        }
        if (dto.getMerchantType() != null) {
            merchant.setMerchantType(dto.getMerchantType());
        }
        if (dto.getContactName() != null) {
            merchant.setContactName(dto.getContactName());
        }
        if (dto.getBusinessLicenseNo() != null) {
            merchant.setBusinessLicenseNo(dto.getBusinessLicenseNo());
        }
        if (dto.getBusinessLicenseImg() != null) {
            merchant.setBusinessLicenseImg(dto.getBusinessLicenseImg());
        }
        if (dto.getFoodLicenseNo() != null) {
            merchant.setFoodLicenseNo(dto.getFoodLicenseNo());
        }
        if (dto.getFoodLicenseImg() != null) {
            merchant.setFoodLicenseImg(dto.getFoodLicenseImg());
        }
        if (dto.getLegalPersonName() != null) {
            merchant.setLegalPersonName(aesUtil.encrypt(dto.getLegalPersonName()));
        }
        if (dto.getLegalPersonIdCard() != null) {
            merchant.setLegalPersonIdCard(aesUtil.encrypt(dto.getLegalPersonIdCard()));
        }
        if (dto.getLegalPersonIdCardImgFront() != null) {
            merchant.setLegalPersonIdCardImgFront(dto.getLegalPersonIdCardImgFront());
        }
        if (dto.getLegalPersonIdCardImgBack() != null) {
            merchant.setLegalPersonIdCardImgBack(dto.getLegalPersonIdCardImgBack());
        }
        if (dto.getBankName() != null) {
            merchant.setBankName(dto.getBankName());
        }
        if (dto.getBankAccount() != null) {
            merchant.setBankAccount(aesUtil.encrypt(dto.getBankAccount()));
        }
        if (dto.getAlipayAccount() != null) {
            merchant.setAlipayAccount(dto.getAlipayAccount());
        }

        // 如果是驳回状态，重新提交后将状态改为待审核
        if (MerchantStatus.REJECTED.getCode().equals(merchant.getStatus())) {
            merchant.setStatus(MerchantStatus.PENDING_AUDIT.getCode());
            merchant.setRejectReason(null); // 清除驳回原因
            log.info("商户重新提交资料，状态改为待审核: merchantId={}", merchantId);
        }

        this.updateById(merchant);
        
        MerchantInfoVO vo = new MerchantInfoVO();
        BeanUtils.copyProperties(merchant, vo);
        return vo;
    }

    @Override
    public MerchantStatsVO getMerchantStats(Long merchantId) {
        log.debug("获取商户统计: merchantId={}", merchantId);

        // 尝试从Redis缓存获取
        Map<String, Object> cachedStats = cacheService.getMerchantStatsCache(merchantId);
        if (cachedStats != null) {
            log.debug("从缓存获取商户统计: merchantId={}", merchantId);
            MerchantStatsVO stats = new MerchantStatsVO();
            stats.setMerchantId(merchantId);
            stats.setTodayOrders(getInt(cachedStats, "todayOrders"));
            stats.setTodayRevenue(getLong(cachedStats, "todayRevenue"));
            stats.setTodayVerified(getInt(cachedStats, "todayVerified"));
            stats.setTodayRefund(getLong(cachedStats, "todayRefund"));
            stats.setYesterdayOrders(getInt(cachedStats, "yesterdayOrders"));
            stats.setYesterdayRevenue(getLong(cachedStats, "yesterdayRevenue"));
            stats.setYesterdayVerified(getInt(cachedStats, "yesterdayVerified"));
            stats.setMonthOrders(getInt(cachedStats, "monthOrders"));
            stats.setMonthRevenue(getLong(cachedStats, "monthRevenue"));
            stats.setMonthVerified(getInt(cachedStats, "monthVerified"));
            stats.setMonthRefund(getLong(cachedStats, "monthRefund"));
            stats.setTotalOrders(getInt(cachedStats, "totalOrders"));
            stats.setTotalRevenue(getLong(cachedStats, "totalRevenue"));
            stats.setTotalSaved(getLong(cachedStats, "totalSaved"));
            stats.setTotalRefund(getLong(cachedStats, "totalRefund"));
            stats.setPendingVerify(getInt(cachedStats, "pendingVerify"));
            stats.setRefunding(getInt(cachedStats, "refunding"));
            stats.setStoreCount(getInt(cachedStats, "storeCount"));
            stats.setProductCount(getInt(cachedStats, "productCount"));
            stats.setOrderGrowthRate(getBigDecimal(cachedStats, "orderGrowthRate"));
            stats.setRevenueGrowthRate(getBigDecimal(cachedStats, "revenueGrowthRate"));
            stats.setVerifiedGrowthRate(getBigDecimal(cachedStats, "verifiedGrowthRate"));
            return stats;
        }

        MerchantStatsVO stats = new MerchantStatsVO();
        stats.setMerchantId(merchantId);

        // 今日统计
        stats.setTodayOrders(orderInfoMapper.countMerchantTodayOrders(merchantId));
        stats.setTodayRevenue(orderInfoMapper.sumMerchantTodayRevenue(merchantId));
        stats.setTodayVerified(orderInfoMapper.countMerchantTodayVerified(merchantId));
        stats.setTodayRefund(orderInfoMapper.sumMerchantTodayRefund(merchantId));

        // 昨日统计
        stats.setYesterdayOrders(orderInfoMapper.countMerchantYesterdayOrders(merchantId));
        stats.setYesterdayRevenue(orderInfoMapper.sumMerchantYesterdayRevenue(merchantId));
        stats.setYesterdayVerified(orderInfoMapper.countMerchantYesterdayVerified(merchantId));

        // 本月统计
        stats.setMonthOrders(orderInfoMapper.countMerchantMonthOrders(merchantId));
        stats.setMonthRevenue(orderInfoMapper.sumMerchantMonthRevenue(merchantId));
        stats.setMonthVerified(orderInfoMapper.countMerchantMonthVerified(merchantId));
        stats.setMonthRefund(orderInfoMapper.sumMerchantMonthRefund(merchantId));

        // 累计统计
        stats.setTotalOrders(orderInfoMapper.countMerchantTotalOrders(merchantId));
        stats.setTotalRevenue(orderInfoMapper.sumMerchantTotalRevenue(merchantId));
        stats.setTotalSaved(orderInfoMapper.sumMerchantTotalSaved(merchantId));
        stats.setTotalRefund(orderInfoMapper.sumMerchantTotalRefund(merchantId));

        // 待处理
        stats.setPendingVerify(orderInfoMapper.countMerchantPendingVerify(merchantId));
        stats.setRefunding(orderInfoMapper.countMerchantRefunding(merchantId));

        // 门店统计
        Long storeCount = storeInfoMapper.selectCount(
                new LambdaQueryWrapper<com.yvliangbao.common.pojo.entity.merchant.StoreInfo>()
                        .eq(com.yvliangbao.common.pojo.entity.merchant.StoreInfo::getMerchantId, merchantId)
        );
        stats.setStoreCount(storeCount != null ? storeCount.intValue() : 0);

        // 在售商品统计
        int productCount = productInfoService.countByMerchantId(merchantId, 1);
        stats.setProductCount(productCount);

        // 计算环比增长率
        stats.setOrderGrowthRate(calculateGrowthRate(stats.getTodayOrders(), stats.getYesterdayOrders()));
        stats.setRevenueGrowthRate(calculateGrowthRate(stats.getTodayRevenue(), stats.getYesterdayRevenue()));
        stats.setVerifiedGrowthRate(calculateGrowthRate(stats.getTodayVerified(), stats.getYesterdayVerified()));

        // 存入Redis缓存
        Map<String, Object> statsMap = new HashMap<>();
        statsMap.put("todayOrders", stats.getTodayOrders());
        statsMap.put("todayRevenue", stats.getTodayRevenue());
        statsMap.put("todayVerified", stats.getTodayVerified());
        statsMap.put("todayRefund", stats.getTodayRefund());
        statsMap.put("yesterdayOrders", stats.getYesterdayOrders());
        statsMap.put("yesterdayRevenue", stats.getYesterdayRevenue());
        statsMap.put("yesterdayVerified", stats.getYesterdayVerified());
        statsMap.put("monthOrders", stats.getMonthOrders());
        statsMap.put("monthRevenue", stats.getMonthRevenue());
        statsMap.put("monthVerified", stats.getMonthVerified());
        statsMap.put("monthRefund", stats.getMonthRefund());
        statsMap.put("totalOrders", stats.getTotalOrders());
        statsMap.put("totalRevenue", stats.getTotalRevenue());
        statsMap.put("totalSaved", stats.getTotalSaved());
        statsMap.put("totalRefund", stats.getTotalRefund());
        statsMap.put("pendingVerify", stats.getPendingVerify());
        statsMap.put("refunding", stats.getRefunding());
        statsMap.put("storeCount", stats.getStoreCount());
        statsMap.put("productCount", stats.getProductCount());
        statsMap.put("orderGrowthRate", stats.getOrderGrowthRate());
        statsMap.put("revenueGrowthRate", stats.getRevenueGrowthRate());
        statsMap.put("verifiedGrowthRate", stats.getVerifiedGrowthRate());
        cacheService.setMerchantStatsCache(merchantId, statsMap);

        return stats;
    }

    @Override
    public RevenueTrendVO getRevenueTrend(Long merchantId, Integer days) {
        log.debug("获取营收趋势: merchantId={}, days={}", merchantId, days);

        if (days == null || days <= 0) {
            days = 7;
        }

        // 尝试从Redis缓存获取
        Map<String, Object> cachedTrend = cacheService.getMerchantRevenueTrendCache(merchantId, days);
        if (cachedTrend != null) {
            log.debug("从缓存获取营收趋势: merchantId={}, days={}", merchantId, days);
            RevenueTrendVO trend = new RevenueTrendVO();
            trend.setMerchantId(merchantId);
            trend.setDays(days);
            // 安全转换列表类型（Jackson反序列化后可能是Integer或LinkedHashMap）
            trend.setDates(convertToStringList(cachedTrend.get("dates")));
            trend.setRevenues(convertToLongList(cachedTrend.get("revenues")));
            trend.setOrders(convertToIntegerList(cachedTrend.get("orders")));
            trend.setRefunds(convertToLongList(cachedTrend.get("refunds")));
            return trend;
        }

        RevenueTrendVO trend = new RevenueTrendVO();
        trend.setMerchantId(merchantId);
        trend.setDays(days);

        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(days - 1);

        // 尝试从每日统计表获取历史数据（除今天外）
        LocalDate yesterday = today.minusDays(1);
        List<Map<String, Object>> statsData = merchantDailyStatsMapper.selectByDateRange(merchantId, startDate, yesterday);

        // 构建统计表数据映射
        Map<String, Map<String, Object>> statsMap = new LinkedHashMap<>();
        for (Map<String, Object> row : statsData) {
            String date = row.get("date").toString();
            statsMap.put(date, row);
        }

        // 构造日期列表和数据列表
        List<String> dates = new ArrayList<>();
        List<Long> revenues = new ArrayList<>();
        List<Integer> orders = new ArrayList<>();
        List<Long> refunds = new ArrayList<>();

        // 填充历史数据（从统计表）
        for (int i = days - 1; i >= 1; i--) {
            LocalDate date = today.minusDays(i);
            String dateStr = date.toString();
            dates.add(dateStr.substring(5)); // 只保留 MM-dd

            Map<String, Object> dayStats = statsMap.get(dateStr);
            if (dayStats != null) {
                orders.add(((Number) dayStats.get("order_count")).intValue());
                revenues.add(((Number) dayStats.get("revenue")).longValue());
                refunds.add(((Number) dayStats.get("refund_amount")).longValue());
            } else {
                orders.add(0);
                revenues.add(0L);
                refunds.add(0L);
            }
        }

        // 今天的数据实时查询
        dates.add(today.toString().substring(5));
        List<Map<String, Object>> todayRevenue = orderInfoMapper.selectRevenueTrend(merchantId, 1);
        List<Map<String, Object>> todayRefund = orderInfoMapper.selectRefundTrend(merchantId, 1);

        String todayStr = today.toString();
        boolean foundRevenue = false;
        for (Map<String, Object> row : todayRevenue) {
            if (todayStr.equals(row.get("date").toString())) {
                orders.add(((Number) row.get("order_count")).intValue());
                revenues.add(((Number) row.get("revenue")).longValue());
                foundRevenue = true;
                break;
            }
        }
        if (!foundRevenue) {
            orders.add(0);
            revenues.add(0L);
        }

        boolean foundRefund = false;
        for (Map<String, Object> row : todayRefund) {
            if (todayStr.equals(row.get("date").toString())) {
                refunds.add(((Number) row.get("refund_amount")).longValue());
                foundRefund = true;
                break;
            }
        }
        if (!foundRefund) {
            refunds.add(0L);
        }

        trend.setDates(dates);
        trend.setRevenues(revenues);
        trend.setOrders(orders);
        trend.setRefunds(refunds);

        // 存入Redis缓存
        Map<String, Object> trendMap = new HashMap<>();
        trendMap.put("dates", dates);
        trendMap.put("revenues", revenues);
        trendMap.put("orders", orders);
        trendMap.put("refunds", refunds);
        cacheService.setMerchantRevenueTrendCache(merchantId, days, trendMap);

        return trend;
    }

    @Override
    public RevenueHistoryVO getRevenueHistory(Long merchantId, String startDate, String endDate) {
        log.debug("获取历史营收: merchantId={}, startDate={}, endDate={}", merchantId, startDate, endDate);
        
        RevenueHistoryVO history = new RevenueHistoryVO();
        history.setMerchantId(merchantId);
        history.setStartDate(startDate);
        history.setEndDate(endDate);
        
        // 查询统计数据
        history.setTotalRevenue(orderInfoMapper.sumMerchantRevenueByRange(merchantId, startDate, endDate));
        history.setOrderCount(orderInfoMapper.countMerchantOrdersByRange(merchantId, startDate, endDate));
        history.setVerifiedCount(orderInfoMapper.countMerchantVerifiedByRange(merchantId, startDate, endDate));
        history.setVerifiedRevenue(orderInfoMapper.sumMerchantVerifiedByRange(merchantId, startDate, endDate));
        history.setRefundAmount(orderInfoMapper.sumMerchantRefundByRange(merchantId, startDate, endDate));

        return history;
    }

    @Override
    public Page<DailyStatsVO> getRevenueHistoryPage(Long merchantId, String startDate, String endDate, Integer pageNum, Integer pageSize) {
        log.debug("分页获取历史营收: merchantId={}, startDate={}, endDate={}, pageNum={}, pageSize={}", 
                merchantId, startDate, endDate, pageNum, pageSize);
        
        // 构建查询条件
        LambdaQueryWrapper<MerchantDailyStats> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MerchantDailyStats::getMerchantId, merchantId);
        
        if (startDate != null && !startDate.isEmpty()) {
            wrapper.ge(MerchantDailyStats::getStatDate, LocalDate.parse(startDate));
        }
        if (endDate != null && !endDate.isEmpty()) {
            wrapper.le(MerchantDailyStats::getStatDate, LocalDate.parse(endDate));
        }
        
        wrapper.orderByDesc(MerchantDailyStats::getStatDate);
        
        // 分页查询
        Page<MerchantDailyStats> page = new Page<>(pageNum, pageSize);
        Page<MerchantDailyStats> result = merchantDailyStatsMapper.selectPage(page, wrapper);
        
        // 转换为VO
        Page<DailyStatsVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        List<DailyStatsVO> voList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        for (MerchantDailyStats stats : result.getRecords()) {
            DailyStatsVO vo = new DailyStatsVO();
            vo.setDate(stats.getStatDate().format(formatter));
            vo.setRevenue(stats.getRevenue() != null ? stats.getRevenue() : 0L);
            vo.setOrderCount(stats.getOrderCount() != null ? stats.getOrderCount() : 0);
            vo.setRefundAmount(stats.getRefundAmount() != null ? stats.getRefundAmount() : 0L);
            vo.setVerifiedCount(stats.getVerifiedCount() != null ? stats.getVerifiedCount() : 0);
            vo.setVerifiedAmount(stats.getVerifiedAmount() != null ? stats.getVerifiedAmount() : 0L);
            voList.add(vo);
        }
        voPage.setRecords(voList);
        
        return voPage;
    }

    // ==================== 类型转换辅助方法 ====================

    /**
     * 安全转换为 String 列表
     */
    @SuppressWarnings("unchecked")
    private List<String> convertToStringList(Object obj) {
        if (obj == null) return new ArrayList<>();
        List<?> list = (List<?>) obj;
        List<String> result = new ArrayList<>();
        for (Object item : list) {
            result.add(item != null ? item.toString() : "");
        }
        return result;
    }

    /**
     * 安全转换为 Long 列表
     */
    @SuppressWarnings("unchecked")
    private List<Long> convertToLongList(Object obj) {
        if (obj == null) return new ArrayList<>();
        List<?> list = (List<?>) obj;
        List<Long> result = new ArrayList<>();
        for (Object item : list) {
            if (item instanceof Number) {
                result.add(((Number) item).longValue());
            } else if (item != null) {
                result.add(Long.parseLong(item.toString()));
            } else {
                result.add(0L);
            }
        }
        return result;
    }

    /**
     * 安全转换为 Integer 列表
     */
    @SuppressWarnings("unchecked")
    private List<Integer> convertToIntegerList(Object obj) {
        if (obj == null) return new ArrayList<>();
        List<?> list = (List<?>) obj;
        List<Integer> result = new ArrayList<>();
        for (Object item : list) {
            if (item instanceof Number) {
                result.add(((Number) item).intValue());
            } else if (item != null) {
                result.add(Integer.parseInt(item.toString()));
            } else {
                result.add(0);
            }
        }
        return result;
    }

    /**
     * 从 Map 中安全获取 Integer 值
     */
    private Integer getInt(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return 0;
    }

    /**
     * 从 Map 中安全获取 Long 值
     */
    private Long getLong(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return 0L;
    }

    /**
     * 从 Map 中安全获取 BigDecimal 值
     */
    private BigDecimal getBigDecimal(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return new BigDecimal(value.toString());
        }
        return BigDecimal.ZERO;
    }

    /**
     * 计算环比增长率
     * @param today 今日数据
     * @param yesterday 昨日数据
     * @return 增长率（百分比），如 15.5 表示增长15.5%
     */
    private BigDecimal calculateGrowthRate(Number today, Number yesterday) {
        double todayVal = today != null ? today.doubleValue() : 0;
        double yesterdayVal = yesterday != null ? yesterday.doubleValue() : 0;
        
        if (yesterdayVal == 0) {
            if (todayVal > 0) {
                return new BigDecimal("100"); // 从0增长视为100%增长
            }
            return BigDecimal.ZERO; // 0到0视为0%
        }
        
        double rate = (todayVal - yesterdayVal) / yesterdayVal * 100;
        return BigDecimal.valueOf(rate).setScale(1, java.math.RoundingMode.HALF_UP);
    }
}
