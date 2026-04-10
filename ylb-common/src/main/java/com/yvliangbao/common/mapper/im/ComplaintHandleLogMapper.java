package com.yvliangbao.common.mapper.im;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yvliangbao.common.pojo.entity.im.ComplaintHandleLog;
import com.yvliangbao.common.pojo.vo.complaint.ComplaintHandleLogVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 客诉处理记录Mapper接口
 *
 * @author 余量宝
 */
@Mapper
public interface ComplaintHandleLogMapper extends BaseMapper<ComplaintHandleLog> {

    /**
     * 查询投诉处理记录列表
     *
     * @param complaintId 投诉ID
     * @return 处理记录列表
     */
    List<ComplaintHandleLogVO> selectByComplaintId(@Param("complaintId") Long complaintId);
}
