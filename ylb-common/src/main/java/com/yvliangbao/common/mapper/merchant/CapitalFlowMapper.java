package com.yvliangbao.common.mapper.merchant;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yvliangbao.common.pojo.entity.merchant.CapitalFlow;
import com.yvliangbao.common.pojo.vo.settlement.CapitalFlowVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 资金流水Mapper接口
 *
 * @author 余量宝
 */
@Mapper
public interface CapitalFlowMapper extends BaseMapper<CapitalFlow> {

    /**
     * 查询商户的资金流水
     */
    List<CapitalFlowVO> selectByMerchantId(@Param("merchantId") Long merchantId,
                                           @Param("flowType") Integer flowType);

    /**
     * 查询商户当前余额
     */
    Long selectCurrentBalance(@Param("merchantId") Long merchantId);
}
