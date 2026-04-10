package com.yvliangbao.common.service.merchant;



import com.yvliangbao.common.pojo.entity.merchant.MerchantDailyStats;

import java.time.LocalDate;
import java.util.List;

/**
 * 商户每日统计服务接口
 *
 * @author 余量宝
 */
public interface MerchantDailyStatsService {

    /**
     * 计算指定商户指定日期的统计数据
     *
     * @param merchantId 商户ID
     * @param date       统计日期
     * @return 统计数据
     */
    MerchantDailyStats calculateDailyStats(Long merchantId, LocalDate date);

    /**
     * 批量计算所有商户指定日期的统计数据
     *
     * @param date 统计日期
     */
    void calculateAllMerchantsDailyStats(LocalDate date);

    /**
     * 获取商户指定日期范围的统计数据
     *
     * @param merchantId 商户ID
     * @param startDate  开始日期
     * @param endDate    结束日期
     * @return 统计数据列表
     */
    List<MerchantDailyStats> getMerchantStatsByDateRange(Long merchantId, LocalDate startDate, LocalDate endDate);

    /**
     * 执行每日统计任务（由定时任务调用）
     * 计算昨天的统计数据
     */
    void executeDailyStatsTask();

    /**
     * 补算历史数据
     * 用于初始化或修复历史统计
     *
     * @param merchantId 商户ID（null表示所有商户）
     * @param startDate  开始日期
     * @param endDate    结束日期
     */
    void backfillHistoricalStats(Long merchantId, LocalDate startDate, LocalDate endDate);
}
