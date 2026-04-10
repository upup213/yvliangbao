package com.yvliangbao.common.mapper.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yvliangbao.common.pojo.entity.user.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 用户信息 Mapper
 *
 * @author 余量宝
 */
@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfo> {
    
    /**
     * 统计用户节省的食物总重量（kg）
     */
    @Select("SELECT COALESCE(SUM(saved_food_weight), 0) FROM user_info WHERE deleted = 0")
    Long selectSumSavedFoodWeight();

    /**
     * 统计用户减少的碳排放总量（kg CO₂）
     */
    @Select("SELECT COALESCE(SUM(carbon_reduction), 0) FROM user_info WHERE deleted = 0")
    Long selectSumCarbonReduction();
}
