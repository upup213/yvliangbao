package com.yvliangbao.common.mapper.im;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yvliangbao.common.pojo.entity.im.ImCustomerService;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 客服会话分配 Mapper
 *
 * @author 余量宝
 */
@Mapper
public interface ImCustomerServiceMapper extends BaseMapper<ImCustomerService> {

    /**
     * 增加当前接待会话数
     */
    @Update("UPDATE im_customer_service SET current_sessions = current_sessions + 1 WHERE admin_id = #{adminId}")
    int incrementSessionCount(@Param("adminId") Long adminId);

    /**
     * 减少当前接待会话数
     */
    @Update("UPDATE im_customer_service SET current_sessions = GREATEST(current_sessions - 1, 0) WHERE admin_id = #{adminId}")
    int decrementSessionCount(@Param("adminId") Long adminId);

    /**
     * 增加累计服务用户数
     */
    @Update("UPDATE im_customer_service SET total_served = total_served + 1 WHERE admin_id = #{adminId}")
    int incrementTotalServed(@Param("adminId") Long adminId);
}
