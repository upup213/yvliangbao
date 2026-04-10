package com.yvliangbao.common.service.impl.common;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yvliangbao.common.mapper.merchant.MerchantInfoMapper;
import com.yvliangbao.common.mapper.merchant.StoreInfoMapper;
import com.yvliangbao.common.mapper.order.OrderInfoMapper;
import com.yvliangbao.common.mapper.user.UserInfoMapper;
import com.yvliangbao.common.pojo.entity.merchant.MerchantInfo;
import com.yvliangbao.common.pojo.entity.merchant.StoreInfo;
import com.yvliangbao.common.pojo.entity.order.OrderInfo;
import com.yvliangbao.common.pojo.entity.user.UserInfo;
import com.yvliangbao.common.pojo.enums.MerchantStatus;
import com.yvliangbao.common.service.common.StatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 平台数据统计 Service 实现
 *
 * @author 余量宝
 */
@Slf4j
@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private MerchantInfoMapper merchantInfoMapper;

    @Autowired
    private StoreInfoMapper storeInfoMapper;

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public Map<String, Object> getOverview() {
        Map<String, Object> result = new HashMap<>();

        // 今日日期
        LocalDate today = LocalDate.now();

        // 总用户数
        long totalUsers = userInfoMapper.selectCount(null);

        // 总商户数
        long totalMerchants = merchantInfoMapper.selectCount(null);

        // 总门店数
        long totalStores = storeInfoMapper.selectCount(null);

        // 总订单数
        long totalOrders = orderInfoMapper.selectCount(null);

        // 总GMV（已支付订单金额）
        BigDecimal totalAmount = orderInfoMapper.selectSumPayAmount();
        if (totalAmount == null) {
            totalAmount = BigDecimal.ZERO;
        }

        // 今日订单数
        LocalDateTime todayStart = LocalDateTime.of(today, LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(today, LocalTime.MAX);
        LambdaQueryWrapper<OrderInfo> todayOrderWrapper = new LambdaQueryWrapper<>();
        todayOrderWrapper.between(OrderInfo::getCreateTime, todayStart, todayEnd);
        long todayOrders = orderInfoMapper.selectCount(todayOrderWrapper);

        // 今日GMV
        LambdaQueryWrapper<OrderInfo> todayAmountWrapper = new LambdaQueryWrapper<>();
        todayAmountWrapper.between(OrderInfo::getCreateTime, todayStart, todayEnd)
                .eq(OrderInfo::getPayStatus, 1);
        BigDecimal todayAmount = orderInfoMapper.selectSumPayAmountByWrapper(todayAmountWrapper);
        if (todayAmount == null) {
            todayAmount = BigDecimal.ZERO;
        }

        // 今日新增用户
        LambdaQueryWrapper<UserInfo> todayUserWrapper = new LambdaQueryWrapper<>();
        todayUserWrapper.between(UserInfo::getCreateTime, todayStart, todayEnd);
        long todayNewUsers = userInfoMapper.selectCount(todayUserWrapper);

        // 今日新增商户
        LambdaQueryWrapper<MerchantInfo> todayMerchantWrapper = new LambdaQueryWrapper<>();
        todayMerchantWrapper.between(MerchantInfo::getCreateTime, todayStart, todayEnd);
        long todayNewMerchants = merchantInfoMapper.selectCount(todayMerchantWrapper);

        // 活跃用户数（30天内有订单的用户）
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        long activeUsers = orderInfoMapper.selectActiveUserCount(thirtyDaysAgo);

        // 活跃商户数（30天内有订单的商户）
        long activeMerchants = orderInfoMapper.selectActiveMerchantCount(thirtyDaysAgo);

        result.put("totalUsers", totalUsers);
        result.put("totalMerchants", totalMerchants);
        result.put("totalStores", totalStores);
        result.put("totalOrders", totalOrders);
        result.put("totalAmount", totalAmount);
        result.put("todayOrders", todayOrders);
        result.put("todayAmount", todayAmount);
        result.put("todayNewUsers", todayNewUsers);
        result.put("todayNewMerchants", todayNewMerchants);
        result.put("activeUsers", activeUsers);
        result.put("activeMerchants", activeMerchants);

        return result;
    }

    @Override
    public List<Map<String, Object>> getTrend(int days) {
        if (days <= 0) {
            days = 7;
        }

        List<Map<String, Object>> result = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            LocalDateTime dateStart = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime dateEnd = LocalDateTime.of(date, LocalTime.MAX);

            Map<String, Object> dayData = new HashMap<>();

            // 日期
            dayData.put("date", date.format(DateTimeFormatter.ofPattern("MM-dd")));

            // 订单数
            LambdaQueryWrapper<OrderInfo> orderWrapper = new LambdaQueryWrapper<>();
            orderWrapper.between(OrderInfo::getCreateTime, dateStart, dateEnd);
            long orderCount = orderInfoMapper.selectCount(orderWrapper);
            dayData.put("orderCount", orderCount);

            // GMV
            LambdaQueryWrapper<OrderInfo> amountWrapper = new LambdaQueryWrapper<>();
            amountWrapper.between(OrderInfo::getCreateTime, dateStart, dateEnd)
                    .eq(OrderInfo::getPayStatus, 1);
            BigDecimal amount = orderInfoMapper.selectSumPayAmountByWrapper(amountWrapper);
            if (amount == null) {
                amount = BigDecimal.ZERO;
            }
            dayData.put("orderAmount", amount);

            // 新增用户
            LambdaQueryWrapper<UserInfo> userWrapper = new LambdaQueryWrapper<>();
            userWrapper.between(UserInfo::getCreateTime, dateStart, dateEnd);
            long newUsers = userInfoMapper.selectCount(userWrapper);
            dayData.put("newUsers", newUsers);

            // 新增商户
            LambdaQueryWrapper<MerchantInfo> merchantWrapper = new LambdaQueryWrapper<>();
            merchantWrapper.between(MerchantInfo::getCreateTime, dateStart, dateEnd);
            long newMerchants = merchantInfoMapper.selectCount(merchantWrapper);
            dayData.put("newMerchants", newMerchants);

            result.add(dayData);
        }

        return result;
    }

    @Override
    public Map<String, Object> getUserStats() {
        Map<String, Object> result = new HashMap<>();

        // 总用户数
        long totalUsers = userInfoMapper.selectCount(null);

        // 今日新增
        LocalDate today = LocalDate.now();
        LocalDateTime todayStart = LocalDateTime.of(today, LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(today, LocalTime.MAX);
        LambdaQueryWrapper<UserInfo> todayWrapper = new LambdaQueryWrapper<>();
        todayWrapper.between(UserInfo::getCreateTime, todayStart, todayEnd);
        long todayNew = userInfoMapper.selectCount(todayWrapper);

        // 本月新增
        LocalDate firstDayOfMonth = today.withDayOfMonth(1);
        LocalDateTime monthStart = LocalDateTime.of(firstDayOfMonth, LocalTime.MIN);
        LambdaQueryWrapper<UserInfo> monthWrapper = new LambdaQueryWrapper<>();
        monthWrapper.between(UserInfo::getCreateTime, monthStart, todayEnd);
        long monthNew = userInfoMapper.selectCount(monthWrapper);

        // 活跃用户（30天内有订单）
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        long activeUsers = orderInfoMapper.selectActiveUserCount(thirtyDaysAgo);

        // 累计节省食物重量
        Long savedWeight = userInfoMapper.selectSumSavedFoodWeight();
        BigDecimal totalSavedFoodWeight = savedWeight != null ? new BigDecimal(savedWeight) : BigDecimal.ZERO;

        // 累计减碳量
        Long carbonReduction = userInfoMapper.selectSumCarbonReduction();
        BigDecimal totalCarbonReduction = carbonReduction != null ? new BigDecimal(carbonReduction) : BigDecimal.ZERO;

        result.put("totalUsers", totalUsers);
        result.put("todayNew", todayNew);
        result.put("monthNew", monthNew);
        result.put("activeUsers", activeUsers);
        result.put("totalSavedFoodWeight", totalSavedFoodWeight);
        result.put("totalCarbonReduction", totalCarbonReduction);

        return result;
    }

    @Override
    public Map<String, Object> getMerchantStats() {
        Map<String, Object> result = new HashMap<>();

        // 总商户数
        long totalMerchants = merchantInfoMapper.selectCount(null);

        // 待审核
        LambdaQueryWrapper<MerchantInfo> pendingWrapper = new LambdaQueryWrapper<>();
        pendingWrapper.eq(MerchantInfo::getStatus, MerchantStatus.PENDING_AUDIT.getCode());
        long pending = merchantInfoMapper.selectCount(pendingWrapper);

        // 正常运营
        LambdaQueryWrapper<MerchantInfo> normalWrapper = new LambdaQueryWrapper<>();
        normalWrapper.eq(MerchantInfo::getStatus, MerchantStatus.NORMAL.getCode());
        long normal = merchantInfoMapper.selectCount(normalWrapper);

        // 已驳回
        LambdaQueryWrapper<MerchantInfo> rejectedWrapper = new LambdaQueryWrapper<>();
        rejectedWrapper.eq(MerchantInfo::getStatus, MerchantStatus.REJECTED.getCode());
        long rejected = merchantInfoMapper.selectCount(rejectedWrapper);

        // 已禁用
        LambdaQueryWrapper<MerchantInfo> bannedWrapper = new LambdaQueryWrapper<>();
        bannedWrapper.eq(MerchantInfo::getStatus, MerchantStatus.DISABLED.getCode());
        long banned = merchantInfoMapper.selectCount(bannedWrapper);

        // 总门店数
        long totalStores = storeInfoMapper.selectCount(null);

        // 营业中门店
        LambdaQueryWrapper<StoreInfo> openWrapper = new LambdaQueryWrapper<>();
        openWrapper.eq(StoreInfo::getBusinessStatus, 1);
        long openStores = storeInfoMapper.selectCount(openWrapper);

        // 活跃商户（30天内有订单）
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        long activeMerchants = orderInfoMapper.selectActiveMerchantCount(thirtyDaysAgo);

        result.put("totalMerchants", totalMerchants);
        result.put("pending", pending);
        result.put("normal", normal);
        result.put("rejected", rejected);
        result.put("banned", banned);
        result.put("totalStores", totalStores);
        result.put("openStores", openStores);
        result.put("activeMerchants", activeMerchants);

        return result;
    }

    @Override
    public List<Map<String, Object>> getRegionStats() {
        // 按区域统计订单数和交易额
        List<Map<String, Object>> result = orderInfoMapper.selectRegionOrderStats();

        if (result == null || result.isEmpty()) {
            return new ArrayList<>();
        }

        // 计算总订单数用于百分比
        long totalOrders = result.stream()
                .mapToLong(m -> ((Number) m.get("orderCount")).longValue())
                .sum();

        // 添加百分比字段
        for (Map<String, Object> item : result) {
            long orderCount = ((Number) item.get("orderCount")).longValue();
            int percentage = totalOrders > 0 ? (int) (orderCount * 100 / totalOrders) : 0;
            item.put("percentage", percentage);
        }

        return result;
    }

    @Override
    public Map<String, Object> getGmvStats() {
        Map<String, Object> result = new HashMap<>();

        LocalDate today = LocalDate.now();

        // 今日 GMV
        LocalDateTime todayStart = LocalDateTime.of(today, LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(today, LocalTime.MAX);
        LambdaQueryWrapper<OrderInfo> todayWrapper = new LambdaQueryWrapper<>();
        todayWrapper.between(OrderInfo::getCreateTime, todayStart, todayEnd)
                .eq(OrderInfo::getPayStatus, 1);
        BigDecimal todayGmv = orderInfoMapper.selectSumPayAmountByWrapper(todayWrapper);
        if (todayGmv == null) {
            todayGmv = BigDecimal.ZERO;
        }
        result.put("todayGmv", todayGmv);

        // 本月 GMV
        LocalDate firstDayOfMonth = today.withDayOfMonth(1);
        LocalDateTime monthStart = LocalDateTime.of(firstDayOfMonth, LocalTime.MIN);
        LambdaQueryWrapper<OrderInfo> monthWrapper = new LambdaQueryWrapper<>();
        monthWrapper.between(OrderInfo::getCreateTime, monthStart, todayEnd)
                .eq(OrderInfo::getPayStatus, 1);
        BigDecimal monthGmv = orderInfoMapper.selectSumPayAmountByWrapper(monthWrapper);
        if (monthGmv == null) {
            monthGmv = BigDecimal.ZERO;
        }
        result.put("monthGmv", monthGmv);

        // 累计 GMV
        BigDecimal totalGmv = orderInfoMapper.selectSumPayAmount();
        if (totalGmv == null) {
            totalGmv = BigDecimal.ZERO;
        }
        result.put("totalGmv", totalGmv);

        // 今日订单数
        LambdaQueryWrapper<OrderInfo> todayOrderWrapper = new LambdaQueryWrapper<>();
        todayOrderWrapper.between(OrderInfo::getCreateTime, todayStart, todayEnd);
        long todayOrders = orderInfoMapper.selectCount(todayOrderWrapper);
        result.put("todayOrders", todayOrders);

        // 本月订单数
        LambdaQueryWrapper<OrderInfo> monthOrderWrapper = new LambdaQueryWrapper<>();
        monthOrderWrapper.between(OrderInfo::getCreateTime, monthStart, todayEnd);
        long monthOrders = orderInfoMapper.selectCount(monthOrderWrapper);
        result.put("monthOrders", monthOrders);

        // 累计订单数
        long totalOrders = orderInfoMapper.selectCount(null);
        result.put("totalOrders", totalOrders);

        return result;
    }
}
