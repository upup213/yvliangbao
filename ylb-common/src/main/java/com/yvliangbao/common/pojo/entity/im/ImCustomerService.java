package com.yvliangbao.common.pojo.entity.im;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 客服会话分配表实体类
 *
 * @author 余量宝
 */
@Data
@TableName("im_customer_service")
public class ImCustomerService {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 管理员ID
     */
    private Long adminId;

    /**
     * 客服用户名
     */
    private String username;

    /**
     * 客服真实姓名
     */
    private String realName;

    /**
     * 在线状态：0-离线，1-在线，2-忙碌
     */
    private Integer status;

    /**
     * 最大接待会话数
     */
    private Integer maxSessions;

    /**
     * 当前接待会话数
     */
    private Integer currentSessions;

    /**
     * 累计服务用户数
     */
    private Integer totalServed;

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

    // ========== 常量 ==========

    /**
     * 状态：离线
     */
    public static final int STATUS_OFFLINE = 0;

    /**
     * 状态：在线
     */
    public static final int STATUS_ONLINE = 1;

    /**
     * 状态：忙碌
     */
    public static final int STATUS_BUSY = 2;
}
