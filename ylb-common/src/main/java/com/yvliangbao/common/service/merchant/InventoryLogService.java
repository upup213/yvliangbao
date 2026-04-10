package com.yvliangbao.common.service.merchant;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yvliangbao.common.pojo.entity.merchant.InventoryLog;


/**
 * 库存变更记录 Service
 *
 * @author 余量宝
 */
public interface InventoryLogService extends IService<InventoryLog> {

    /**
     * 记录库存变更日志
     *
     * @param productId 商品ID
     * @param changeType 变更类型
     * @param changeAmount 变更数量
     * @param beforeStock 变更前库存
     * @param afterStock 变更后库存
     * @param orderNo 关联订单号
     * @param remark 备注
     */
    void log(Long productId, InventoryLog.ChangeType changeType, Integer changeAmount,
             Integer beforeStock, Integer afterStock, String orderNo, String remark);

    /**
     * 检查指定订单是否已有某种类型的库存变更记录
     * 用于幂等性检查，防止重复操作
     *
     * @param orderNo 订单号
     * @param changeType 变更类型
     * @return 是否存在记录
     */
    boolean existsByOrderNoAndChangeType(String orderNo, InventoryLog.ChangeType changeType);
}
