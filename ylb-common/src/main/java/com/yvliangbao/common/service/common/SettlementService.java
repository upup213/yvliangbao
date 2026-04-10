package com.yvliangbao.common.service.common;



import com.yvliangbao.common.pojo.vo.settlement.CapitalFlowVO;
import com.yvliangbao.common.pojo.vo.settlement.SettlementRecordVO;
import com.yvliangbao.common.pojo.vo.settlement.SettlementRuleVO;

import java.util.List;

/**
 * 结算服务接口
 *
 * @author 余量宝
 */
public interface SettlementService {

    /**
     * 获取结算规则列表
     */
    List<SettlementRuleVO> getSettlementRules();

    /**
     * 获取商户结算记录
     */
    List<SettlementRecordVO> getSettlementRecords(Long merchantId);

    /**
     * 获取商户资金流水
     */
    List<CapitalFlowVO> getCapitalFlows(Long merchantId, Integer flowType);

    /**
     * 获取商户当前可结算金额
     */
    Long getPendingSettleAmount(Long merchantId);

    /**
     * 获取商户当前余额
     */
    Long getMerchantBalance(Long merchantId);

    /**
     * 更新商户余额（增加或减少）
     *
     * @param merchantId 商户ID
     * @param amount 金额（正数为增加，负数为减少，单位：分）
     * @return 更新后的余额
     */
    Long updateMerchantBalance(Long merchantId, Long amount);

    /**
     * 获取平台服务费比例
     *
     * @return 服务费比例（如5表示5%）
     */
    Integer getPlatformServiceFeeRate();
}
