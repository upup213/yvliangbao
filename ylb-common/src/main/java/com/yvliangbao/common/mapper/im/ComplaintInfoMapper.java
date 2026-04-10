package com.yvliangbao.common.mapper.im;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yvliangbao.common.pojo.dto.complaint.ComplaintQueryDTO;
import com.yvliangbao.common.pojo.entity.im.ComplaintInfo;
import com.yvliangbao.common.pojo.vo.complaint.ComplaintVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 客诉记录Mapper接口
 *
 * @author 余量宝
 */
@Mapper
public interface ComplaintInfoMapper extends BaseMapper<ComplaintInfo> {

    /**
     * 分页查询投诉列表
     *
     * @param page 分页对象
     * @param query 查询条件
     * @return 投诉列表
     */
    IPage<ComplaintVO> selectComplaintPage(Page<ComplaintVO> page, @Param("query") ComplaintQueryDTO query);

    /**
     * 根据ID查询投诉详情
     *
     * @param id 投诉ID
     * @return 投诉详情
     */
    ComplaintVO selectComplaintById(@Param("id") Long id);
}
