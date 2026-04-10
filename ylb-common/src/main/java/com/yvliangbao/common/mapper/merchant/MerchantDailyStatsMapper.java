package com.yvliangbao.common.mapper.merchant;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yvliangbao.common.pojo.entity.merchant.MerchantDailyStats;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 商户每日统计 Mapper
 *
 * @author 余量宝
 */
@Mapper
public interface MerchantDailyStatsMapper extends BaseMapper<MerchantDailyStats> {

    /**
     * 查询商户指定日期范围的每日统计
     */
    @Select("SELECT stat_date as date, revenue, order_count, refund_amount, verified_count, verified_amount " +
            "FROM merchant_daily_stats " +
            "WHERE merchant_id = #{merchantId} " +
            "  AND stat_date BETWEEN #{startDate} AND #{endDate} " +
            "ORDER BY stat_date")
    List<Map<String, Object>> selectByDateRange(@Param("merchantId") Long merchantId,
                                                  @Param("startDate") LocalDate startDate,
                                                  @Param("endDate") LocalDate endDate);

    /**
     * 批量插入或更新统计数据
     */
    int batchInsertOrUpdate(@Param("list") List<MerchantDailyStats> list);

    /**
     * 查询所有商户ID
     */
    @Select("SELECT DISTINCT merchant_id FROM merchant_info WHERE deleted = 0")
    List<Long> selectAllMerchantIds();
}
