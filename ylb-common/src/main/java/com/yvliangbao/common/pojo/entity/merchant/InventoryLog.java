package com.yvliangbao.common.pojo.entity.merchant;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 库存变更记录实体
 *
 * @author 余量宝
 */
@Data
@TableName("inventory_log")
@ApiModel(value = "InventoryLog", description = "库存变更记录")
public class InventoryLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 变更类型枚举
     */
    public enum ChangeType {
        INCREASE(1, "增加"),
        DEDUCT(2, "扣减"),
        LOCK(3, "锁定"),
        RELEASE(4, "释放");

        private final int code;
        private final String desc;

        ChangeType(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public int getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "商品ID")
    @TableField("product_id")
    private Long productId;

    @ApiModelProperty(value = "变更类型：1-增加，2-扣减，3-锁定，4-释放")
    @TableField("change_type")
    private Integer changeType;

    @ApiModelProperty(value = "变更数量")
    @TableField("change_amount")
    private Integer changeAmount;

    @ApiModelProperty(value = "变更前库存（通过反推计算，保证准确性）")
    @TableField("before_stock")
    private Integer beforeStock;

    @ApiModelProperty(value = "变更后库存（UPDATE后立即查询，MySQL行锁保证准确性）")
    @TableField("after_stock")
    private Integer afterStock;

    @ApiModelProperty(value = "关联订单号")
    @TableField("order_no")
    private String orderNo;

    @ApiModelProperty(value = "备注")
    @TableField("remark")
    private String remark;

    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
