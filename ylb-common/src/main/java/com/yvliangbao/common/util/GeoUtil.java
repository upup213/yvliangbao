package com.yvliangbao.common.util;

import java.math.BigDecimal;

/**
 * 地理位置工具类
 *
 * @author 余量宝
 */
public class GeoUtil {

    /**
     * 地球半径（米）
     */
    private static final double EARTH_RADIUS = 6371000;

    /**
     * 计算两点之间的距离（Haversine公式）
     *
     * @param lat1 点1纬度
     * @param lon1 点1经度
     * @param lat2 点2纬度
     * @param lon2 点2经度
     * @return 距离（米）
     */
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // 将角度转换为弧度
        double radLat1 = Math.toRadians(lat1);
        double radLat2 = Math.toRadians(lat2);
        double deltaLat = Math.toRadians(lat2 - lat1);
        double deltaLon = Math.toRadians(lon2 - lon1);

        // Haversine公式
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }

    /**
     * 计算两点之间的距离
     *
     * @param lat1 点1纬度
     * @param lon1 点1经度
     * @param lat2 点2纬度（BigDecimal）
     * @param lon2 点2经度（BigDecimal）
     * @return 距离（米）
     */
    public static double calculateDistance(double lat1, double lon1, BigDecimal lat2, BigDecimal lon2) {
        return calculateDistance(lat1, lon1, lat2.doubleValue(), lon2.doubleValue());
    }

    /**
     * 格式化距离显示
     *
     * @param distance 距离（米）
     * @return 格式化后的距离文本
     */
    public static String formatDistance(double distance) {
        if (distance < 1000) {
            return String.format("%.0fm", distance);
        } else {
            return String.format("%.1fkm", distance / 1000);
        }
    }

    /**
     * 判断点是否在指定范围内
     *
     * @param centerLat 中心点纬度
     * @param centerLon 中心点经度
     * @param targetLat 目标点纬度
     * @param targetLon 目标点经度
     * @param radius    半径（米）
     * @return 是否在范围内
     */
    public static boolean isInRange(double centerLat, double centerLon,
                                    double targetLat, double targetLon, double radius) {
        return calculateDistance(centerLat, centerLon, targetLat, targetLon) <= radius;
    }
}
