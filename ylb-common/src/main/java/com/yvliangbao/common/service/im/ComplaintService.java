package com.yvliangbao.common.service.im;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yvliangbao.common.pojo.dto.complaint.ComplaintCreateDTO;
import com.yvliangbao.common.pojo.dto.complaint.ComplaintHandleDTO;
import com.yvliangbao.common.pojo.dto.complaint.ComplaintQueryDTO;
import com.yvliangbao.common.pojo.vo.complaint.ComplaintHandleLogVO;
import com.yvliangbao.common.pojo.vo.complaint.ComplaintVO;

import java.util.List;

/**
 * 客诉服务接口
 *
 * @author 余量宝
 */
public interface ComplaintService {

    /**
     * 创建投诉（用户端）
     *
     * @param userId 用户ID
     * @param dto 创建投诉请求
     * @return 投诉ID
     */
    Long createComplaint(Long userId, ComplaintCreateDTO dto);

    /**
     * 分页查询投诉列表（平台端/商户端）
     *
     * @param query 查询条件
     * @return 投诉列表
     */
    IPage<ComplaintVO> getComplaintPage(ComplaintQueryDTO query);

    /**
     * 查询用户投诉列表（用户端）
     *
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 投诉列表
     */
    IPage<ComplaintVO> getUserComplaintList(Long userId, Integer page, Integer size);

    /**
     * 查询投诉详情
     *
     * @param id 投诉ID
     * @return 投诉详情
     */
    ComplaintVO getComplaintDetail(Long id);

    /**
     * 处理投诉（平台端）
     *
     * @param adminId 管理员ID
     * @param adminName 管理员姓名
     * @param dto 处理投诉请求
     * @return 是否成功
     */
    boolean handleComplaint(Long adminId, String adminName, ComplaintHandleDTO dto);

    /**
     * 关闭投诉
     *
     * @param id 投诉ID
     * @param operatorId 操作人ID
     * @param operatorType 操作人类型：1-用户，2-商户，3-管理员
     * @param reason 关闭原因
     * @return 是否成功
     */
    boolean closeComplaint(Long id, Long operatorId, Integer operatorType, String reason);

    /**
     * 查询投诉处理记录
     *
     * @param complaintId 投诉ID
     * @return 处理记录列表
     */
    List<ComplaintHandleLogVO> getHandleLogs(Long complaintId);

    /**
     * 用户评价投诉处理结果
     *
     * @param userId 用户ID
     * @param complaintId 投诉ID
     * @param satisfied 是否满意
     * @param feedback 用户反馈
     * @return 是否成功
     */
    boolean rateComplaint(Long userId, Long complaintId, Integer satisfied, String feedback);
}
