package com.yvliangbao.common.mapper.admin;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yvliangbao.common.pojo.entity.admin.AdminOperationLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 管理员操作日志Mapper
 *
 * @author 余量宝
 */
@Mapper
public interface AdminOperationLogMapper extends BaseMapper<AdminOperationLog> {

}
