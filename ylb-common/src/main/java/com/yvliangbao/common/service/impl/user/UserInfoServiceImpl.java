package com.yvliangbao.common.service.impl.user;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yvliangbao.common.exception.BusinessException;
import com.yvliangbao.common.mapper.order.OrderInfoMapper;
import com.yvliangbao.common.mapper.user.BalanceLogMapper;
import com.yvliangbao.common.mapper.user.UserInfoMapper;
import com.yvliangbao.common.pojo.dto.user.UserInfoVO;
import com.yvliangbao.common.pojo.dto.user.UserLoginDTO;
import com.yvliangbao.common.pojo.dto.user.UserUpdateDTO;
import com.yvliangbao.common.pojo.entity.user.UserInfo;
import com.yvliangbao.common.pojo.enums.UserStatus;
import com.yvliangbao.common.pojo.vo.user.UserStatsVO;
import com.yvliangbao.common.service.user.UserInfoService;
import com.yvliangbao.common.util.JwtUtil;
import com.yvliangbao.common.util.RedisUtil;
import com.yvliangbao.common.util.UserContext;
import com.yvliangbao.common.util.WechatUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 用户信息 Service 实现
 *
 * @author 余量宝
 */
@Slf4j
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Autowired
    private WechatUtil wechatUtil;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private BalanceLogMapper balanceLogMapper;

    /**
     * 缓存键前缀
     */
    private static final String CACHE_USER_PREFIX = "user:info:";
    
    /**
     * 缓存过期时间（小时）
     */
    private static final long CACHE_EXPIRE_HOURS = 24;

    @Override
    public UserInfo getByOpenid(String openid) {
        return this.lambdaQuery()
                .eq(UserInfo::getOpenid, openid)
                .one();
    }

    @Override
    public UserInfo getByPhone(String phone) {
        return this.lambdaQuery()
                .eq(UserInfo::getPhone, phone)
                .one();
    }

    @Override
    public UserInfoVO wechatLogin(UserLoginDTO dto) {
        log.info("微信登录开始: code={}", dto.getCode());
        
        try {
            // 1. 调用微信接口获取 openid
            JSONObject wxResult = wechatUtil.code2Session(dto.getCode());
            if (wxResult == null) {
                log.error("微信登录失败: 调用微信接口返回null, code={}", dto.getCode());
                throw new BusinessException("微信登录失败，请稍后重试");
            }

            String openid = wxResult.getString("openid");
            if (openid == null) {
                log.error("微信登录失败: 未获取到openid, wxResult={}", wxResult.toJSONString());
                throw new BusinessException("微信登录失败，请检查code是否有效");
            }

            log.info("微信登录成功获取openid: openid={}", openid);

            // 2. 查询或创建用户
            UserInfo user = this.getByOpenid(openid);
            if (user == null) {
                // 新用户，自动注册
                log.info("新用户注册: openid={}", openid);
                
                user = new UserInfo();
                user.setOpenid(openid);
                user.setNickname(dto.getNickname() != null ? dto.getNickname() : "用户" + UUID.randomUUID().toString().substring(0, 8));
                user.setAvatar(dto.getAvatar());
                user.setStatus(UserStatus.NORMAL.getCode());
                this.save(user);
                
                log.info("新用户注册成功: userId={}, openid={}, nickname={}", user.getId(), openid, user.getNickname());
            } else {
                // 老用户，更新登录信息
                log.info("老用户登录: userId={}, openid={}", user.getId(), openid);
                
                boolean needUpdate = false;
                if (dto.getNickname() != null && !dto.getNickname().equals(user.getNickname())) {
                    user.setNickname(dto.getNickname());
                    needUpdate = true;
                }
                if (dto.getAvatar() != null && !dto.getAvatar().equals(user.getAvatar())) {
                    user.setAvatar(dto.getAvatar());
                    needUpdate = true;
                }
                
                if (needUpdate) {
                    this.updateById(user);
                    log.info("更新用户信息: userId={}", user.getId());
                }
            }

            // 3. 构造 VO 并返回
            UserInfoVO vo = buildUserInfoVO(user);
            
            log.info("微信登录完成: userId={}, openid={}", user.getId(), openid);
            return vo;
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("微信登录异常: code={}, error={}", dto.getCode(), e.getMessage(), e);
            throw new BusinessException("登录失败：" + e.getMessage());
        }
    }

    @Override
    public UserInfoVO getUserInfo(Long userId) {
        log.debug("获取用户信息: userId={}", userId);
        
        // 1. 尝试从缓存获取
        String cacheKey = CACHE_USER_PREFIX + userId;
        Object cached = null;
        try {
            cached = redisUtil.get(cacheKey);
        } catch (Exception e) {
            log.warn("Redis连接异常，跳过缓存: userId={}, error={}", userId, e.getMessage());
        }
        
        if (cached != null) {
            log.debug("从缓存获取用户信息: userId={}", userId);
            return (UserInfoVO) cached;
        }
        
        // 2. 从数据库查询
        UserInfo user = this.getById(userId);
        if (user == null) {
            log.warn("用户不存在: userId={}", userId);
            throw new BusinessException("用户不存在");
        }

        // 3. 构造 VO
        UserInfoVO vo = buildUserInfoVO(user);
        
        // 4. 存入缓存
        redisUtil.set(cacheKey, vo, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
        log.debug("用户信息已缓存: userId={}", userId);
        
        return vo;
    }

    @Override
    public UserInfoVO getCurrentUserInfo() {
        Long userId = UserContext.getUserId();
        log.debug("获取当前用户信息: userId={}", userId);
        return getUserInfo(userId);
    }

    @Override
    public UserStatsVO getUserStats() {
        Long userId = UserContext.getUserId();
        log.debug("获取用户统计数据: userId={}", userId);
        
        // 查询用户基本信息
        UserInfo user = this.getById(userId);
        if (user == null) {
            log.warn("用户不存在: userId={}", userId);
            throw new BusinessException("用户不存在");
        }
        
        UserStatsVO vo = new UserStatsVO();
        
        try {
            // ========== 订单统计 ==========
            vo.setUnpaidCount(orderInfoMapper.countByStatus(userId, 0));        // 待支付
            vo.setWaitingCount(orderInfoMapper.countWaitingOrders(userId));      // 待取货（已支付+待取餐）
            vo.setCompletedCount(orderInfoMapper.countCompletedOrders(userId));  // 已完成
            vo.setCancelledCount(orderInfoMapper.countByStatus(userId, 4));     // 已取消
            vo.setRefundingCount(orderInfoMapper.countByStatus(userId, 6));     // 退款中
            
            // ========== 金额统计 ==========
            // 累计消费（分转元）
            Long totalSpentFen = orderInfoMapper.sumTotalSpent(userId);
            vo.setTotalSpent(new BigDecimal(totalSpentFen).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP));
            
            // 累计节省（分转元）
            Long totalSavedFen = orderInfoMapper.sumSavedAmount(userId);
            vo.setTotalSaved(new BigDecimal(totalSavedFen).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP));
            
            // 本月消费（分转元）
            Long monthSpentFen = orderInfoMapper.sumMonthSpent(userId);
            vo.setMonthSpent(new BigDecimal(monthSpentFen).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP));
            
            // 本月节省（分转元）
            Long monthSavedFen = orderInfoMapper.sumMonthSaved(userId);
            vo.setMonthSaved(new BigDecimal(monthSavedFen).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP));
            
            // ========== 环保成就 ==========
            // 环保行动次数 = 已完成订单数
            vo.setEcoActions(vo.getCompletedCount());
            
            // TODO: 环保成就计算优化 - 当前使用估算模型
            // 估算规则：每个订单平均拯救0.5kg食物，每拯救1kg食物减碳0.4kg CO₂
            if (user.getSavedFoodWeight() == null || user.getSavedFoodWeight().compareTo(BigDecimal.ZERO) == 0) {
                BigDecimal savedFoodWeight = new BigDecimal(vo.getCompletedCount() * 0.5).setScale(2, RoundingMode.HALF_UP);
                vo.setSavedFoodWeight(savedFoodWeight);
            } else {
                vo.setSavedFoodWeight(user.getSavedFoodWeight());
            }
            
            if (user.getCarbonReduction() == null || user.getCarbonReduction().compareTo(BigDecimal.ZERO) == 0) {
                BigDecimal carbonReduction = vo.getSavedFoodWeight().multiply(new BigDecimal("0.4")).setScale(2, RoundingMode.HALF_UP);
                vo.setCarbonReduction(carbonReduction);
            } else {
                vo.setCarbonReduction(user.getCarbonReduction());
            }
            
            // ========== 余额相关 ==========
            vo.setBalance(user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO);
            vo.setTotalRecharged(balanceLogMapper.sumRechargedAmount(userId));
            
        } catch (Exception e) {
            log.error("获取用户统计数据失败: userId={}, error={}", userId, e.getMessage(), e);
            // 返回默认值
            vo.setUnpaidCount(0);
            vo.setWaitingCount(0);
            vo.setCompletedCount(0);
            vo.setCancelledCount(0);
            vo.setRefundingCount(0);
            vo.setTotalSpent(BigDecimal.ZERO);
            vo.setTotalSaved(BigDecimal.ZERO);
            vo.setMonthSpent(BigDecimal.ZERO);
            vo.setMonthSaved(BigDecimal.ZERO);
            vo.setEcoActions(0);
            vo.setSavedFoodWeight(BigDecimal.ZERO);
            vo.setCarbonReduction(BigDecimal.ZERO);
            vo.setBalance(user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO);
            vo.setTotalRecharged(BigDecimal.ZERO);
        }
        
        return vo;
    }

    @Override
    public UserInfoVO updateCurrentUserInfo(UserUpdateDTO dto) {
        Long userId = UserContext.getUserId();
        log.info("更新用户信息: userId={}, nickname={}, avatar={}", userId, dto.getNickname(), dto.getAvatar());
        
        // 查询用户
        UserInfo user = this.getById(userId);
        if (user == null) {
            log.warn("用户不存在: userId={}", userId);
            throw new BusinessException("用户不存在");
        }

        // 更新信息
        boolean needUpdate = false;
        if (dto.getNickname() != null && !dto.getNickname().equals(user.getNickname())) {
            user.setNickname(dto.getNickname());
            needUpdate = true;
        }
        if (dto.getAvatar() != null && !dto.getAvatar().equals(user.getAvatar())) {
            user.setAvatar(dto.getAvatar());
            needUpdate = true;
        }
        
        if (needUpdate) {
            this.updateById(user);
            
            // 清除缓存
            String cacheKey = CACHE_USER_PREFIX + userId;
            try {
                redisUtil.delete(cacheKey);
            } catch (Exception e) {
                log.warn("Redis连接异常，跳过缓存删除: userId={}, error={}", userId, e.getMessage());
            }
            
            log.info("用户信息更新成功: userId={}", userId);
        }

        return buildUserInfoVO(user);
    }

    @Override
    public UserInfoVO bindPhone(Long userId, String phone) {
        log.info("绑定手机号开始: userId={}, phone={}", userId, phone);
        
        // 查询用户
        UserInfo user = this.getById(userId);
        if (user == null) {
            log.warn("用户不存在: userId={}", userId);
            throw new BusinessException("用户不存在");
        }

        // 检查手机号是否已被绑定
        UserInfo existUser = this.getByPhone(phone);
        if (existUser != null && !existUser.getId().equals(userId)) {
            log.warn("手机号已被其他用户绑定: phone={}, existUserId={}", phone, existUser.getId());
            throw new BusinessException("该手机号已被其他用户绑定");
        }

        // 绑定手机号
        user.setPhone(phone);
        this.updateById(user);
        
        // 清除缓存
        String cacheKey = CACHE_USER_PREFIX + userId;
        try {
            redisUtil.delete(cacheKey);
        } catch (Exception e) {
            log.warn("Redis连接异常，跳过缓存删除: userId={}, error={}", userId, e.getMessage());
        }
        
        log.info("绑定手机号成功: userId={}, phone={}", userId, phone);

        // 构造 VO 并返回
        return buildUserInfoVO(user);
    }

    @Override
    public UserInfoVO bindCurrentPhone(String phone) {
        Long userId = UserContext.getUserId();
        log.info("当前用户绑定手机号: userId={}, phone={}", userId, phone);
        return bindPhone(userId, phone);
    }

    /**
     * 构造用户信息 VO
     *
     * @param user 用户实体
     * @return 用户信息 VO
     */
    private UserInfoVO buildUserInfoVO(UserInfo user) {
        UserInfoVO vo = new UserInfoVO();
        BeanUtils.copyProperties(user, vo);
        
        // 生成 token
        String token = JwtUtil.generateToken(user.getId(), user.getOpenid());
        vo.setToken(token);
        
        // 查询已完成订单数
        try {
            int completedOrders = orderInfoMapper.countCompletedOrders(user.getId());
            vo.setCompletedOrders(completedOrders);
            
            // TODO: 环保成就计算优化 - 当前使用估算模型
            // 估算规则：每个订单平均拯救0.5kg食物，每拯救1kg食物减碳0.4kg CO₂
            // 后续改进：订单完成时根据商品实际重量累加，每个商品可配置碳足迹系数
            if (user.getSavedFoodWeight() == null || user.getSavedFoodWeight().compareTo(BigDecimal.ZERO) == 0) {
                // 假设每个订单平均拯救0.5kg食物
                BigDecimal savedFoodWeight = new BigDecimal(completedOrders * 0.5).setScale(2, RoundingMode.HALF_UP);
                vo.setSavedFoodWeight(savedFoodWeight);
            } else {
                vo.setSavedFoodWeight(user.getSavedFoodWeight());
            }
            
            if (user.getCarbonReduction() == null || user.getCarbonReduction().compareTo(BigDecimal.ZERO) == 0) {
                // 假设每拯救1kg食物减碳0.4kg CO₂
                BigDecimal carbonReduction = vo.getSavedFoodWeight().multiply(new BigDecimal("0.4")).setScale(2, RoundingMode.HALF_UP);
                vo.setCarbonReduction(carbonReduction);
            } else {
                vo.setCarbonReduction(user.getCarbonReduction());
            }
        } catch (Exception e) {
            log.warn("获取用户统计数据失败: userId={}, error={}", user.getId(), e.getMessage());
            vo.setCompletedOrders(0);
            vo.setSavedFoodWeight(user.getSavedFoodWeight() != null ? user.getSavedFoodWeight() : BigDecimal.ZERO);
            vo.setCarbonReduction(user.getCarbonReduction() != null ? user.getCarbonReduction() : BigDecimal.ZERO);
        }
        
        return vo;
    }
}
