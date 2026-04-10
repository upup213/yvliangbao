package com.yvliangbao.common.service.impl.merchant;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yvliangbao.common.mapper.merchant.InventoryInfoMapper;
import com.yvliangbao.common.pojo.entity.merchant.InventoryInfo;
import com.yvliangbao.common.pojo.entity.merchant.InventoryLog;
import com.yvliangbao.common.service.merchant.InventoryInfoService;
import com.yvliangbao.common.service.merchant.InventoryLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 库存信息 Service 实现
 *
 * 提供库存的并发安全管理：
 * 1. 利用数据库行锁保证原子性
 * 2. 记录所有库存变更日志
 * 3. 支持库存预占机制
 *
 * 并发安全说明：
 * - 所有库存操作使用原子 SQL + @Transactional 保证并发安全
 * - UPDATE 和 SELECT 在同一事务中，行锁持续到事务提交
 * - 日志中的 beforeStock/afterStock 通过"事务内查询"保证准确性
 *
 * @author 余量宝
 */
@Slf4j
@Service
public class InventoryInfoServiceImpl extends ServiceImpl<InventoryInfoMapper, InventoryInfo> implements InventoryInfoService {

    @Autowired
    private InventoryLogService inventoryLogService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int lockStock(Long productId, Integer quantity, String orderNo) {
        log.info("锁定库存开始: productId={}, quantity={}, orderNo={}", productId, quantity, orderNo);

        // 1. 原子锁定库存（利用数据库行锁保证并发安全）
        int updatedRows = baseMapper.lockStock(productId, quantity);

        if (updatedRows > 0) {
            // 2. 查询更新后库存（同一事务内，行锁未释放，保证这是本次操作的结果）
            InventoryInfo after = baseMapper.selectByProductId(productId);

            // 3. 反推变更前库存（事务内数据一致，before = after + changeAmount）
            int beforeAvailableStock = after.getAvailableStock() + quantity;

            // 4. 记录库存变更日志
            inventoryLogService.log(productId, InventoryLog.ChangeType.LOCK, quantity,
                    beforeAvailableStock, after.getAvailableStock(), orderNo, "下单锁定库存");

            log.info("锁定库存成功: productId={}, quantity={}, availableStock={}, lockedStock={}",
                    productId, quantity, after.getAvailableStock(), after.getLockedStock());
        } else {
            // 库存不足，查询当前库存用于日志
            InventoryInfo current = baseMapper.selectByProductId(productId);
            log.warn("锁定库存失败: 库存不足, productId={}, quantity={}, availableStock={}",
                    productId, quantity, current != null ? current.getAvailableStock() : 0);
        }

        return updatedRows;
    }

    @Override
    public int confirmDeduct(Long productId, Integer quantity, String orderNo) {
        log.info("确认扣减库存开始: productId={}, quantity={}, orderNo={}", productId, quantity, orderNo);

        // 1. 原子确认扣减（利用数据库行锁保证并发安全）
        int updatedRows = baseMapper.confirmDeduct(productId, quantity);

        if (updatedRows > 0) {
            // 2. 查询更新后库存（MySQL 行锁保证这是本次操作的结果）
            InventoryInfo after = baseMapper.selectByProductId(productId);

            // 3. 反推变更前锁定库存（before = after + changeAmount）
            int beforeLockedStock = after.getLockedStock() + quantity;

            // 4. 记录库存变更日志（记录的是锁定库存的变化）
            inventoryLogService.log(productId, InventoryLog.ChangeType.DEDUCT, quantity,
                    beforeLockedStock, after.getLockedStock(), orderNo, "支付成功确认扣减");

            log.info("确认扣减库存成功: productId={}, quantity={}, lockedStock={}",
                    productId, quantity, after.getLockedStock());
        } else {
            // 锁定库存不足，查询当前库存用于日志
            InventoryInfo current = baseMapper.selectByProductId(productId);
            log.warn("确认扣减库存失败: 锁定库存不足, productId={}, quantity={}, lockedStock={}",
                    productId, quantity, current != null ? current.getLockedStock() : 0);
        }

        return updatedRows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int releaseStock(Long productId, Integer quantity, String orderNo) {
        log.info("释放库存开始: productId={}, quantity={}, orderNo={}", productId, quantity, orderNo);

        // 幂等性检查：如果该订单已经释放过库存，直接返回成功（防止重复释放）
        if (orderNo != null && inventoryLogService.existsByOrderNoAndChangeType(orderNo, InventoryLog.ChangeType.RELEASE)) {
            log.warn("库存已释放（幂等性检查）: orderNo={}, 跳过重复释放", orderNo);
            return 1; // 返回成功，表示库存已释放
        }

        // 1. 原子释放库存（利用数据库行锁保证并发安全）
        int updatedRows = baseMapper.releaseStock(productId, quantity);

        if (updatedRows > 0) {
            // 2. 查询更新后库存（同一事务内，行锁未释放，保证这是本次操作的结果）
            InventoryInfo after = baseMapper.selectByProductId(productId);

            // 3. 反推变更前锁定库存（事务内数据一致，before = after + changeAmount）
            int beforeLockedStock = after.getLockedStock() + quantity;

            // 4. 记录库存变更日志（记录的是锁定库存的变化）
            inventoryLogService.log(productId, InventoryLog.ChangeType.RELEASE, quantity,
                    beforeLockedStock, after.getLockedStock(), orderNo, "取消订单释放库存");

            log.info("释放库存成功: productId={}, quantity={}, availableStock={}, lockedStock={}",
                    productId, quantity, after.getAvailableStock(), after.getLockedStock());
        } else {
            // 锁定库存不足，查询当前库存用于日志
            InventoryInfo current = baseMapper.selectByProductId(productId);
            log.warn("释放库存失败: 锁定库存不足, productId={}, quantity={}, lockedStock={}",
                    productId, quantity, current != null ? current.getLockedStock() : 0);
        }

        return updatedRows;
    }

    @Override
    @Deprecated
    public int reduceStock(Long productId, Integer quantity) {
        log.warn("使用已废弃的 reduceStock 方法，建议使用 lockStock + confirmDeduct");
        log.debug("原子扣减库存: productId={}, quantity={}", productId, quantity);

        int updatedRows = baseMapper.reduceStock(productId, quantity);

        if (updatedRows > 0) {
            log.info("扣减库存成功: productId={}, quantity={}", productId, quantity);
        } else {
            log.warn("扣减库存失败: 库存不足, productId={}, quantity={}", productId, quantity);
        }

        return updatedRows;
    }

    @Override
    @Deprecated
    public int restoreStock(Long productId, Integer quantity) {
        log.warn("使用已废弃的 restoreStock 方法，建议使用 releaseStock");
        log.debug("恢复库存: productId={}, quantity={}", productId, quantity);

        int updatedRows = baseMapper.restoreStock(productId, quantity);

        if (updatedRows > 0) {
            log.info("恢复库存成功: productId={}, quantity={}", productId, quantity);
        } else {
            log.warn("恢复库存失败: 库存记录不存在, productId={}", productId);
        }

        return updatedRows;
    }

    @Override
    public InventoryInfo getByProductId(Long productId) {
        return baseMapper.selectByProductId(productId);
    }

    @Override
    public void initInventory(Long productId, Integer totalStock) {
        log.info("初始化库存: productId={}, totalStock={}", productId, totalStock);

        InventoryInfo inventory = new InventoryInfo();
        inventory.setProductId(productId);
        inventory.setTotalStock(totalStock);
        inventory.setAvailableStock(totalStock);
        inventory.setLockedStock(0);
        inventory.setVersion(0);
        this.save(inventory);

        // 记录库存变更日志
        inventoryLogService.log(productId, InventoryLog.ChangeType.INCREASE, totalStock,
                0, totalStock, null, "初始化库存");
    }

    @Override
    public void updateTotalStock(Long productId, Integer totalStock) {
        log.info("更新总库存: productId={}, totalStock={}", productId, totalStock);

        InventoryInfo inventory = this.getByProductId(productId);
        if (inventory == null) {
            log.warn("库存记录不存在，自动初始化: productId={}", productId);
            initInventory(productId, totalStock);
            return;
        }

        // 计算库存变化量，同步更新可用库存
        int stockDiff = totalStock - inventory.getTotalStock();
        int newAvailableStock = inventory.getAvailableStock() + stockDiff;

        // 可用库存不能为负
        if (newAvailableStock < 0) {
            newAvailableStock = 0;
        }

        inventory.setTotalStock(totalStock);
        inventory.setAvailableStock(newAvailableStock);
        this.updateById(inventory);

        // 记录库存变更日志
        if (stockDiff != 0) {
            inventoryLogService.log(productId,
                    stockDiff > 0 ? InventoryLog.ChangeType.INCREASE : InventoryLog.ChangeType.DEDUCT,
                    Math.abs(stockDiff),
                    inventory.getAvailableStock() - stockDiff, inventory.getAvailableStock(),
                    null, "商户调整库存");
        }

        log.info("更新总库存成功: productId={}, totalStock={}, availableStock={}",
                productId, totalStock, newAvailableStock);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int increaseStock(Long productId, Integer quantity, String orderNo) {
        log.info("增加库存开始: productId={}, quantity={}, orderNo={}", productId, quantity, orderNo);

        // 1. 原子增加库存（利用数据库行锁保证并发安全）
        int updatedRows = baseMapper.increaseStock(productId, quantity);

        if (updatedRows > 0) {
            // 2. 查询更新后库存（同一事务内，行锁未释放，保证这是本次操作的结果）
            InventoryInfo after = baseMapper.selectByProductId(productId);

            // 3. 反推变更前库存（事务内数据一致，before = after - changeAmount，因为这是增加操作）
            int beforeAvailableStock = after.getAvailableStock() - quantity;

            // 4. 记录库存变更日志
            inventoryLogService.log(productId, InventoryLog.ChangeType.INCREASE, quantity,
                    beforeAvailableStock, after.getAvailableStock(), orderNo, "退款恢复库存");

            log.info("增加库存成功: productId={}, quantity={}, availableStock={}",
                    productId, quantity, after.getAvailableStock());
        } else {
            log.warn("增加库存失败: productId={}", productId);
        }

        return updatedRows;
    }
}
