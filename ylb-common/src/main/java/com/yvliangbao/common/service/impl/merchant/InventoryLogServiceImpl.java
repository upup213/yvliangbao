package com.yvliangbao.common.service.impl.merchant;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yvliangbao.common.mapper.merchant.InventoryLogMapper;
import com.yvliangbao.common.pojo.entity.merchant.InventoryLog;
import com.yvliangbao.common.service.merchant.InventoryLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 库存变更记录 Service 实现
 *
 * @author 余量宝
 */
@Slf4j
@Service
public class InventoryLogServiceImpl extends ServiceImpl<InventoryLogMapper, InventoryLog> implements InventoryLogService {

    @Override
    public void log(Long productId, InventoryLog.ChangeType changeType, Integer changeAmount,
                    Integer beforeStock, Integer afterStock, String orderNo, String remark) {
        InventoryLog inventoryLog = new InventoryLog();
        inventoryLog.setProductId(productId);
        inventoryLog.setChangeType(changeType.getCode());
        inventoryLog.setChangeAmount(changeAmount);
        inventoryLog.setBeforeStock(beforeStock);
        inventoryLog.setAfterStock(afterStock);
        inventoryLog.setOrderNo(orderNo);
        inventoryLog.setRemark(remark);
        
        this.save(inventoryLog);
        
        log.info("库存变更记录: productId={}, type={}, amount={}, before={}, after={}, orderNo={}",
                productId, changeType.getDesc(), changeAmount, beforeStock, afterStock, orderNo);
    }

    @Override
    public boolean existsByOrderNoAndChangeType(String orderNo, InventoryLog.ChangeType changeType) {
        return this.lambdaQuery()
                .eq(InventoryLog::getOrderNo, orderNo)
                .eq(InventoryLog::getChangeType, changeType.getCode())
                .exists();
    }
}
