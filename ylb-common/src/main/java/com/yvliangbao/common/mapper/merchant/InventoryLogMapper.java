package com.yvliangbao.common.mapper.merchant;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yvliangbao.common.pojo.entity.merchant.InventoryLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 库存变更记录 Mapper
 *
 * @author 余量宝
 */
@Mapper
public interface InventoryLogMapper extends BaseMapper<InventoryLog> {
}
