package com.yvliangbao.common.service.impl.user;

import com.yvliangbao.common.exception.BusinessException;
import com.yvliangbao.common.mapper.user.BalanceLogMapper;
import com.yvliangbao.common.mapper.user.UserInfoMapper;
import com.yvliangbao.common.pojo.dto.balance.RechargeDTO;
import com.yvliangbao.common.pojo.entity.user.BalanceLog;
import com.yvliangbao.common.pojo.entity.user.UserInfo;
import com.yvliangbao.common.pojo.enums.BalanceChangeType;
import com.yvliangbao.common.pojo.vo.balance.BalanceLogVO;
import com.yvliangbao.common.pojo.vo.balance.RechargeVO;
import com.yvliangbao.common.service.user.BalanceService;
import com.yvliangbao.common.util.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * 余额服务实现
 *
 * @author 余量宝
 */
@Slf4j
@Service
public class BalanceServiceImpl implements BalanceService {

    @Autowired
    private BalanceLogMapper balanceLogMapper;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RechargeVO recharge(RechargeDTO dto) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException("请先登录");
        }

        BigDecimal amount = dto.getAmount();
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("充值金额必须大于0");
        }

        // 查询充值前余额
        UserInfo user = userInfoMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        BigDecimal beforeBalance = user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO;

        // 模拟充值（实际项目应接入微信支付）
        // 这里直接增加余额
        int rows = balanceLogMapper.increaseBalance(userId, amount);
        if (rows == 0) {
            throw new BusinessException("充值失败");
        }

        // 查询充值后余额
        user = userInfoMapper.selectById(userId);
        BigDecimal afterBalance = user.getBalance();

        // 生成流水号
        String logNo = generateLogNo();

        // 记录余额变动日志
        BalanceLog balanceLog = new BalanceLog();
        balanceLog.setLogNo(logNo);
        balanceLog.setUserId(userId);
        balanceLog.setChangeType(BalanceChangeType.RECHARGE.getCode());
        balanceLog.setChangeAmount(amount);
        balanceLog.setBeforeBalance(beforeBalance);
        balanceLog.setAfterBalance(afterBalance);
        balanceLog.setRemark(dto.getPayMethod() == 2 ? "模拟充值" : "微信充值");
        balanceLog.setCreateTime(LocalDateTime.now());
        balanceLogMapper.insert(balanceLog);

        log.info("用户充值成功: userId={}, amount={}, balance={}", userId, amount, afterBalance);

        // 返回结果
        RechargeVO vo = new RechargeVO();
        vo.setLogNo(logNo);
        vo.setAmount(amount);
        vo.setBalance(afterBalance);
        vo.setPayMethodDesc(dto.getPayMethod() == 2 ? "模拟充值" : "微信充值");
        return vo;
    }

    @Override
    public List<BalanceLogVO> getBalanceLogs(Integer page, Integer size) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException("请先登录");
        }

        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1 || size > 50) {
            size = 20;
        }

        int offset = (page - 1) * size;
        List<BalanceLog> logs = balanceLogMapper.selectByUserId(userId, offset, size);

        return logs.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean increaseBalance(Long userId, BigDecimal amount, Integer changeType, String relatedNo, String remark) {
        if (userId == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }

        // 查询充值前余额
        UserInfo user = userInfoMapper.selectById(userId);
        if (user == null) {
            return false;
        }
        BigDecimal beforeBalance = user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO;

        // 增加余额
        int rows = balanceLogMapper.increaseBalance(userId, amount);
        if (rows == 0) {
            return false;
        }

        // 查询充值后余额
        user = userInfoMapper.selectById(userId);
        BigDecimal afterBalance = user.getBalance();

        // 记录日志
        BalanceLog balanceLog = new BalanceLog();
        balanceLog.setLogNo(generateLogNo());
        balanceLog.setUserId(userId);
        balanceLog.setChangeType(changeType);
        balanceLog.setChangeAmount(amount);
        balanceLog.setBeforeBalance(beforeBalance);
        balanceLog.setAfterBalance(afterBalance);
        balanceLog.setRelatedNo(relatedNo);
        balanceLog.setRemark(remark);
        balanceLog.setCreateTime(LocalDateTime.now());
        balanceLogMapper.insert(balanceLog);

        log.info("余额增加: userId={}, amount={}, type={}, relatedNo={}", userId, amount, changeType, relatedNo);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean decreaseBalance(Long userId, BigDecimal amount, Integer changeType, String relatedNo, String remark) {
        if (userId == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }

        // 查询扣减前余额
        UserInfo user = userInfoMapper.selectById(userId);
        if (user == null) {
            return false;
        }
        BigDecimal beforeBalance = user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO;

        // 扣减余额（原子操作，余额不足返回0）
        int rows = balanceLogMapper.decreaseBalance(userId, amount);
        if (rows == 0) {
            log.warn("余额扣减失败（余额不足）: userId={}, amount={}, balance={}", userId, amount, beforeBalance);
            return false;
        }

        // 查询扣减后余额
        user = userInfoMapper.selectById(userId);
        BigDecimal afterBalance = user.getBalance();

        // 记录日志（负数表示减少）
        BalanceLog balanceLog = new BalanceLog();
        balanceLog.setLogNo(generateLogNo());
        balanceLog.setUserId(userId);
        balanceLog.setChangeType(changeType);
        balanceLog.setChangeAmount(amount.negate()); // 转为负数
        balanceLog.setBeforeBalance(beforeBalance);
        balanceLog.setAfterBalance(afterBalance);
        balanceLog.setRelatedNo(relatedNo);
        balanceLog.setRemark(remark);
        balanceLog.setCreateTime(LocalDateTime.now());
        balanceLogMapper.insert(balanceLog);

        log.info("余额扣减: userId={}, amount={}, type={}, relatedNo={}", userId, amount, changeType, relatedNo);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean decreaseBalanceWithVersion(Long userId, BigDecimal amount, Integer expectedVersion, 
                                              Integer changeType, String relatedNo, String remark) {
        if (userId == null || amount.compareTo(BigDecimal.ZERO) <= 0 || expectedVersion == null) {
            return false;
        }

        UserInfo user = userInfoMapper.selectById(userId);
        if (user == null) {
            return false;
        }
        BigDecimal beforeBalance = user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO;

        int rows = balanceLogMapper.decreaseBalanceWithVersion(userId, amount, expectedVersion);
        if (rows == 0) {
            log.warn("余额扣减失败（乐观锁冲突或余额不足）: userId={}, amount={}, expectedVersion={}, actualVersion={}", 
                    userId, amount, expectedVersion, user.getVersion());
            return false;
        }

        user = userInfoMapper.selectById(userId);
        BigDecimal afterBalance = user.getBalance();

        BalanceLog balanceLog = new BalanceLog();
        balanceLog.setLogNo(generateLogNo());
        balanceLog.setUserId(userId);
        balanceLog.setChangeType(changeType);
        balanceLog.setChangeAmount(amount.negate());
        balanceLog.setBeforeBalance(beforeBalance);
        balanceLog.setAfterBalance(afterBalance);
        balanceLog.setRelatedNo(relatedNo);
        balanceLog.setRemark(remark);
        balanceLog.setCreateTime(LocalDateTime.now());
        balanceLogMapper.insert(balanceLog);

        log.info("余额扣减（乐观锁）: userId={}, amount={}, version={}", userId, amount, expectedVersion);
        return true;
    }

    /**
     * 生成流水号
     */
    private String generateLogNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        int random = ThreadLocalRandom.current().nextInt(100000, 999999);
        return "BL" + timestamp + random;
    }

    /**
     * 转换为VO
     */
    private BalanceLogVO convertToVO(BalanceLog log) {
        BalanceLogVO vo = new BalanceLogVO();
        BeanUtils.copyProperties(log, vo);
        
        // 设置变动类型描述
        BalanceChangeType changeType = BalanceChangeType.getByCode(log.getChangeType());
        if (changeType != null) {
            vo.setChangeTypeDesc(changeType.getDesc());
        }
        
        return vo;
    }
}
