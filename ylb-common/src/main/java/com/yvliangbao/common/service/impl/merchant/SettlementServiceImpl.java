package com.yvliangbao.common.service.impl.merchant;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.yvliangbao.common.mapper.merchant.CapitalFlowMapper;
import com.yvliangbao.common.mapper.merchant.MerchantInfoMapper;
import com.yvliangbao.common.mapper.merchant.SettlementRecordMapper;
import com.yvliangbao.common.mapper.merchant.SettlementRuleMapper;
import com.yvliangbao.common.pojo.entity.merchant.MerchantInfo;
import com.yvliangbao.common.pojo.entity.merchant.SettlementRecord;
import com.yvliangbao.common.pojo.entity.merchant.SettlementRule;
import com.yvliangbao.common.pojo.vo.settlement.CapitalFlowVO;
import com.yvliangbao.common.pojo.vo.settlement.SettlementRecordVO;
import com.yvliangbao.common.pojo.vo.settlement.SettlementRuleVO;
import com.yvliangbao.common.service.common.CacheService;
import com.yvliangbao.common.service.common.SettlementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 结算服务实现类
 *
 * @author 余量宝
 */
@Slf4j
@Service
public class SettlementServiceImpl implements SettlementService {

    @Autowired
    private SettlementRuleMapper settlementRuleMapper;

    @Autowired
    private SettlementRecordMapper settlementRecordMapper;

    @Autowired
    private CapitalFlowMapper capitalFlowMapper;

    @Autowired
    private MerchantInfoMapper merchantInfoMapper;

    @Autowired
    private CacheService cacheService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public List<SettlementRuleVO> getSettlementRules() {
        LambdaQueryWrapper<SettlementRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SettlementRule::getStatus, 1)
                .orderByAsc(SettlementRule::getSortOrder);
        List<SettlementRule> rules = settlementRuleMapper.selectList(wrapper);

        return rules.stream().map(rule -> {
            SettlementRuleVO vo = new SettlementRuleVO();
            BeanUtils.copyProperties(rule, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<SettlementRecordVO> getSettlementRecords(Long merchantId) {
        List<SettlementRecord> records = settlementRecordMapper.selectByMerchantId(merchantId);

        return records.stream().map(record -> {
            SettlementRecordVO vo = new SettlementRecordVO();
            BeanUtils.copyProperties(record, vo);
            // 金额从分转为元
            vo.setTotalAmount(record.getTotalAmount() / 100.0);
            vo.setServiceFee(record.getServiceFee() / 100.0);
            vo.setSettleAmount(record.getSettleAmount() / 100.0);
            vo.setRefundAmount(record.getRefundAmount() / 100.0);
            // 日期格式化
            if (record.getPeriodStart() != null) {
                vo.setPeriodStart(record.getPeriodStart().format(DATE_FORMATTER));
            }
            if (record.getPeriodEnd() != null) {
                vo.setPeriodEnd(record.getPeriodEnd().format(DATE_FORMATTER));
            }
            if (record.getSettleTime() != null) {
                vo.setSettleTime(record.getSettleTime().format(DATETIME_FORMATTER));
            }
            if (record.getCreateTime() != null) {
                vo.setCreateTime(record.getCreateTime().format(DATETIME_FORMATTER));
            }
            // 状态文本
            vo.setStatusText(getStatusText(record.getStatus()));
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<CapitalFlowVO> getCapitalFlows(Long merchantId, Integer flowType) {
        return capitalFlowMapper.selectByMerchantId(merchantId, flowType);
    }

    @Override
    public Long getPendingSettleAmount(Long merchantId) {
        return settlementRecordMapper.selectPendingSettleAmount(merchantId);
    }

    @Override
    public Long getMerchantBalance(Long merchantId) {
        // 优先从商户表的余额字段读取
        MerchantInfo merchant = merchantInfoMapper.selectById(merchantId);
        if (merchant != null && merchant.getBalance() != null) {
            return merchant.getBalance();
        }

        // 备用：从资金流水计算
        Long balance = capitalFlowMapper.selectCurrentBalance(merchantId);

        // 如果计算出了余额，同步更新到商户表
        if (balance != null && merchant != null) {
            LambdaUpdateWrapper<MerchantInfo> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(MerchantInfo::getId, merchantId)
                    .set(MerchantInfo::getBalance, balance);
            merchantInfoMapper.update(null, updateWrapper);
        }

        return balance != null ? balance : 0L;
    }

    @Override
    public Long updateMerchantBalance(Long merchantId, Long amount) {
        log.debug("更新商户余额: merchantId={}, amount={}", merchantId, amount);

        int retryCount = 0;
        int maxRetries = 3;

        while (retryCount < maxRetries) {
            try {
                MerchantInfo merchant = merchantInfoMapper.selectById(merchantId);
                if (merchant == null) {
                    throw new RuntimeException("商户不存在");
                }

                Long currentBalance = merchant.getBalance() != null ? merchant.getBalance() : 0L;
                Long newBalance = currentBalance + amount;

                if (newBalance < 0) {
                    throw new RuntimeException("余额不足");
                }

                Integer currentVersion = merchant.getVersion() != null ? merchant.getVersion() : 0;

                int rows = merchantInfoMapper.updateBalanceWithVersion(merchantId, currentVersion, newBalance);

                if (rows > 0) {
                    log.info("商户余额更新成功: merchantId={}, 原余额={}, 变动={}, 新余额={}, 版本号={}",
                            merchantId, currentBalance, amount, newBalance, currentVersion + 1);
                    return newBalance;
                }

                retryCount++;
                log.warn("商户余额更新冲突（版本号不匹配），重试第{}次: merchantId={}", retryCount, merchantId);

            } catch (Exception e) {
                if (e.getMessage() != null && (e.getMessage().contains("余额不足") || e.getMessage().contains("商户不存在"))) {
                    throw new RuntimeException(e.getMessage());
                }
                retryCount++;
                log.warn("商户余额更新异常，重试第{}次: merchantId={}, error={}", retryCount, merchantId, e.getMessage());
            }
        }

        throw new RuntimeException("系统繁忙，余额更新失败，请稍后重试");
    }

    @Override
    public Integer getPlatformServiceFeeRate() {
        // 查询平台服务费规则（ruleType=1）
        LambdaQueryWrapper<SettlementRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SettlementRule::getRuleType, 1)
                .eq(SettlementRule::getStatus, 1);
        SettlementRule rule = settlementRuleMapper.selectOne(wrapper);
        
        if (rule != null && rule.getRuleValue() != null) {
            try {
                return Integer.parseInt(rule.getRuleValue());
            } catch (NumberFormatException e) {
                log.warn("平台服务费比例解析失败: {}", rule.getRuleValue());
            }
        }
        
        // 默认5%
        return 5;
    }

    private String getStatusText(Integer status) {
        switch (status) {
            case 0:
                return "待结算";
            case 1:
                return "待确认";
            case 2:
                return "已结算";
            case 3:
                return "已拒绝";
            default:
                return "未知";
        }
    }
}
