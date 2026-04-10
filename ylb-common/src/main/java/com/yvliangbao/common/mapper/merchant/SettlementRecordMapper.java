package com.yvliangbao.common.mapper.merchant;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yvliangbao.common.pojo.entity.merchant.SettlementRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 结算记录Mapper接口
 *
 * @author 余量宝
 */
@Mapper
public interface SettlementRecordMapper extends BaseMapper<SettlementRecord> {

    /**
     * 查询商户的结算记录
     */
    List<SettlementRecord> selectByMerchantId(@Param("merchantId") Long merchantId);

    /**
     * 查询商户待结算金额
     */
    Long selectPendingSettleAmount(@Param("merchantId") Long merchantId);
}
