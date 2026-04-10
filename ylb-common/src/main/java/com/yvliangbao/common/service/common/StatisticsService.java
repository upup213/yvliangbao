package com.yvliangbao.common.service.common;

import java.util.List;
import java.util.Map;

/**
 * 平台数据统计 Service 接口
 *
 * @author 余量宝
 */
public interface StatisticsService {

    /**
     * 获取数据概览
     *
     * @return 统计数据
     */
    Map<String, Object> getOverview();

    /**
     * 获取趋势数据
     *
     * @param days 天数
     * @return 趋势数据列表
     */
    List<Map<String, Object>> getTrend(int days);

    /**
     * 获取用户统计数据
     *
     * @return 用户统计数据
     */
    Map<String, Object> getUserStats();

    /**
     * 获取商户统计数据
     *
     * @return 商户统计数据
     */
    Map<String, Object> getMerchantStats();

    /**
     * 获取区域统计数据
     *
     * @return 区域统计数据
     */
    List<Map<String, Object>> getRegionStats();

    /**
     * 获取 GMV 统计数据
     *
     * @return GMV 统计数据
     */
    Map<String, Object> getGmvStats();
}
