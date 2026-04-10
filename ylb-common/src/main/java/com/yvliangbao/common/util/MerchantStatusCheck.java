package com.yvliangbao.common.util;


import com.yvliangbao.common.exception.BusinessException;
import com.yvliangbao.common.pojo.entity.merchant.MerchantInfo;
import com.yvliangbao.common.pojo.enums.MerchantStatus;

/**
 * 商户状态检查工具类
 */
public class MerchantStatusCheck {

    /**
     * 检查商户是否可以进行业务操作
     * 只有状态为 NORMAL(1) 时才能进行业务操作
     *
     * @param merchant 商户信息
     */
    public static void checkBusinessOperation(MerchantInfo merchant) {
        if (merchant == null) {
            throw new BusinessException("商户不存在");
        }

        Integer status = merchant.getStatus();
        if (MerchantStatus.PENDING_AUDIT.getCode().equals(status)) {
            throw new BusinessException("AUDIT_PENDING", "您的入驻申请正在审核中，请耐心等待");
        }
        if (MerchantStatus.REJECTED.getCode().equals(status)) {
            throw new BusinessException("AUDIT_REJECTED", "您的入驻申请被拒绝，请重新提交");
        }
        if (MerchantStatus.DISABLED.getCode().equals(status)) {
            throw new BusinessException("商户已被禁用，请联系平台管理员");
        }
        if (!MerchantStatus.NORMAL.getCode().equals(status)) {
            throw new BusinessException("商户状态异常，无法进行操作");
        }
    }

    /**
     * 检查商户是否可以编辑资料
     * 只有 PENDING_AUDIT 和 REJECTED 状态可以编辑
     *
     * @param merchant 商户信息
     */
    public static void checkCanEditInfo(MerchantInfo merchant) {
        if (merchant == null) {
            throw new BusinessException("商户不存在");
        }

        Integer status = merchant.getStatus();
        // 待审核或被拒绝时可以重新提交
        if (MerchantStatus.PENDING_AUDIT.getCode().equals(status)) {
            return;
        }
        if (MerchantStatus.REJECTED.getCode().equals(status)) {
            return;
        }

        throw new BusinessException("当前状态不允许修改资料");
    }
}
