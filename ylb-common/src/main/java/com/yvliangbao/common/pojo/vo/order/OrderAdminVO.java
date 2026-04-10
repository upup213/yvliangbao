package com.yvliangbao.common.pojo.vo.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 平台端订单详情VO
 *
 * @author 余量宝
 */
@Data
public class OrderAdminVO {

    // ===== 订单基本信息 =====
    private Long id;
    private String orderNo;
    private Integer status;
    private String statusDesc;
    private BigDecimal totalAmount;
    private BigDecimal payAmount;
    private BigDecimal refundAmount;
    private Integer quantity;
    private String pickupCode;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime finishTime;
    
    private String refundReason;
    private String cancelReason;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime refundApplyTime;

    // ===== 商品信息 =====
    private Long productId;
    private String productName;
    private String productImage;
    private BigDecimal productPrice;
    private BigDecimal originalPrice;

    // ===== 用户信息 =====
    private Long userId;
    private String userNickname;
    private String userPhone;
    private String userAvatar;

    // ===== 商户信息 =====
    private Long merchantId;
    private String merchantName;
    private String merchantPhone;
    
    // ===== 门店信息 =====
    private Long storeId;
    private String storeName;
    private String storeAddress;

    // ===== 订单状态描述 =====
    public String getStatusDesc() {
        if (status == null) return "未知";
        switch (status) {
            case 1: return "待支付";
            case 2: return "已支付";
            case 3: return "待取餐";
            case 4: return "已完成";
            case 5: return "已取消";
            case 6: return "退款中";
            case 7: return "已退款";
            default: return "未知";
        }
    }
}
