package com.yvliangbao.common.pojo.entity.im;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 客诉记录实体
 *
 * @author 余量宝
 */
@Data
@TableName("complaint_info")
public class ComplaintInfo {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 客诉编号
     */
    private String complaintNo;

    /**
     * 关联订单号
     */
    private String orderNo;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户昵称（冗余）
     */
    private String userNickname;

    /**
     * 用户手机号（冗余）
     */
    private String userPhone;

    /**
     * 商户ID
     */
    private Long merchantId;

    /**
     * 商户名称（冗余）
     */
    private String merchantName;

    /**
     * 投诉类型：1-商品质量问题，2-服务态度问题，3-配送问题，4-退款问题，5-其他
     */
    private Integer complaintType;

    /**
     * 投诉内容
     */
    private String complaintContent;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 图片证据（逗号分隔）
     */
    private String imageUrls;

    /**
     * 状态：0-待处理，1-处理中，2-已解决，3-已关闭
     */
    private Integer status;

    /**
     * 处理人ID
     */
    private Long handlerId;

    /**
     * 处理人姓名
     */
    private String handlerName;

    /**
     * 处理时间
     */
    private LocalDateTime handleTime;

    /**
     * 处理结果
     */
    private String handleResult;

    /**
     * 用户是否满意：0-不满意，1-满意
     */
    private Integer userSatisfied;

    /**
     * 用户反馈
     */
    private String userFeedback;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer deleted;

    // ========== 常量定义 ==========

    /**
     * 投诉类型
     */
    public static final int TYPE_QUALITY = 1;       // 商品质量问题
    public static final int TYPE_SERVICE = 2;       // 服务态度问题
    public static final int TYPE_DELIVERY = 3;      // 配送问题
    public static final int TYPE_REFUND = 4;        // 退款问题
    public static final int TYPE_OTHER = 5;         // 其他

    /**
     * 状态
     */
    public static final int STATUS_PENDING = 0;     // 待处理
    public static final int STATUS_PROCESSING = 1; // 处理中
    public static final int STATUS_RESOLVED = 2;   // 已解决
    public static final int STATUS_CLOSED = 3;     // 已关闭
}
