package com.yvliangbao.common.mapper.merchant;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yvliangbao.common.pojo.entity.merchant.StoreInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 门店信息 Mapper
 *
 * @author 余量宝
 */
@Mapper
public interface StoreInfoMapper extends BaseMapper<StoreInfo> {
    
    /**
     * 统计各区域门店数量 TOP5
     */
    @Select("SELECT province as name, COUNT(*) as value FROM store_info " +
            "WHERE deleted = 0 AND status = 1 " +
            "GROUP BY province ORDER BY value DESC LIMIT 5")
    List<Map<String, Object>> selectStoreRegionStats();
}
