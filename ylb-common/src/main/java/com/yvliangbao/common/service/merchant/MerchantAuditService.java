package com.yvliangbao.common.service.merchant;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yvliangbao.common.pojo.dto.merchant.MerchantAuditListDTO;
import com.yvliangbao.common.pojo.vo.merchant.MerchantAuditVO;


/**
 * 商户审核Service接口
 *
 * @author 余量宝
 */
public interface MerchantAuditService {

    /**
     * 分页查询商户审核列表
     *
     * @param dto 查询条件
     * @return 分页结果
     */
    IPage<MerchantAuditVO> listPage(MerchantAuditListDTO dto);

    /**
     * 获取商户资质详情
     *
     * @param merchantId 商户ID
     * @return 商户审核信息
     */
    MerchantAuditVO getAuditDetail(Long merchantId);

    /**
     * 审核通过
     *
     * @param merchantId 商户ID
     * @param remark     备注
     */
    void approve(Long merchantId, String remark);

    /**
     * 审核拒绝
     *
     * @param merchantId 商户ID
     * @param reason     拒绝原因
     */
    void reject(Long merchantId, String reason);

    /**
     * 启用/解封商户
     *
     * @param merchantId 商户ID
     */
    void enable(Long merchantId);

    /**
     * 禁用/封禁商户
     *
     * @param merchantId 商户ID
     * @param reason     封禁原因
     */
    void disable(Long merchantId, String reason);
}
