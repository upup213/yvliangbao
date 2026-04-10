package com.yvliangbao.common.service.impl.im;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yvliangbao.common.mapper.im.ComplaintHandleLogMapper;
import com.yvliangbao.common.mapper.im.ComplaintInfoMapper;
import com.yvliangbao.common.mapper.user.UserInfoMapper;
import com.yvliangbao.common.pojo.dto.complaint.ComplaintCreateDTO;
import com.yvliangbao.common.pojo.dto.complaint.ComplaintHandleDTO;
import com.yvliangbao.common.pojo.dto.complaint.ComplaintQueryDTO;
import com.yvliangbao.common.pojo.entity.im.ComplaintHandleLog;
import com.yvliangbao.common.pojo.entity.im.ComplaintInfo;
import com.yvliangbao.common.pojo.entity.user.UserInfo;
import com.yvliangbao.common.pojo.vo.complaint.ComplaintHandleLogVO;
import com.yvliangbao.common.pojo.vo.complaint.ComplaintVO;
import com.yvliangbao.common.service.im.ComplaintService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 客诉服务实现
 *
 * @author 余量宝
 */
@Slf4j
@Service
public class ComplaintServiceImpl implements ComplaintService {

    @Autowired
    private ComplaintInfoMapper complaintInfoMapper;

    @Autowired
    private ComplaintHandleLogMapper complaintHandleLogMapper;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createComplaint(Long userId, ComplaintCreateDTO dto) {
        // 查询用户信息
        UserInfo user = userInfoMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 创建投诉记录
        ComplaintInfo complaint = new ComplaintInfo();
        BeanUtils.copyProperties(dto, complaint);
        complaint.setUserId(userId);
        complaint.setUserNickname(user.getNickname());
        complaint.setUserPhone(user.getPhone());
        complaint.setComplaintNo(generateComplaintNo());
        complaint.setStatus(0); // 待处理
        complaint.setCreateTime(LocalDateTime.now());
        complaint.setUpdateTime(LocalDateTime.now());
        complaint.setDeleted(0);

        complaintInfoMapper.insert(complaint);

        // 记录操作日志
        ComplaintHandleLog handleLog = new ComplaintHandleLog();
        handleLog.setComplaintId(complaint.getId());
        handleLog.setOperatorId(userId);
        handleLog.setOperatorName(user.getNickname());
        handleLog.setOperatorType(1); // 用户
        handleLog.setAction("创建投诉");
        handleLog.setContent(dto.getComplaintContent());
        handleLog.setCreateTime(LocalDateTime.now());

        complaintHandleLogMapper.insert(handleLog);

        log.info("用户 {} 创建投诉成功，投诉编号：{}", userId, complaint.getComplaintNo());

        return complaint.getId();
    }

    @Override
    public IPage<ComplaintVO> getComplaintPage(ComplaintQueryDTO query) {
        Page<ComplaintVO> page = new Page<>(query.getPage(), query.getSize());
        return complaintInfoMapper.selectComplaintPage(page, query);
    }

    @Override
    public IPage<ComplaintVO> getUserComplaintList(Long userId, Integer page, Integer size) {
        ComplaintQueryDTO query = new ComplaintQueryDTO();
        query.setUserId(userId);
        query.setPage(page);
        query.setSize(size);

        Page<ComplaintVO> pageObj = new Page<>(page, size);
        return complaintInfoMapper.selectComplaintPage(pageObj, query);
    }

    @Override
    public ComplaintVO getComplaintDetail(Long id) {
        ComplaintVO complaint = complaintInfoMapper.selectComplaintById(id);
        if (complaint == null) {
            throw new RuntimeException("投诉不存在");
        }
        return complaint;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handleComplaint(Long adminId, String adminName, ComplaintHandleDTO dto) {
        // 查询投诉记录
        ComplaintInfo complaint = complaintInfoMapper.selectById(dto.getComplaintId());
        if (complaint == null) {
            throw new RuntimeException("投诉不存在");
        }

        // 更新投诉状态
        complaint.setStatus(2); // 已解决
        complaint.setHandlerId(adminId);
        complaint.setHandlerName(adminName);
        complaint.setHandleTime(LocalDateTime.now());
        complaint.setHandleResult(dto.getHandleResult());
        complaint.setUpdateTime(LocalDateTime.now());

        complaintInfoMapper.updateById(complaint);

        // 记录操作日志
        ComplaintHandleLog handleLog = new ComplaintHandleLog();
        handleLog.setComplaintId(dto.getComplaintId());
        handleLog.setOperatorId(adminId);
        handleLog.setOperatorName(adminName);
        handleLog.setOperatorType(3); // 管理员
        handleLog.setAction("处理投诉");
        handleLog.setContent(dto.getHandleResult());
        handleLog.setCreateTime(LocalDateTime.now());

        complaintHandleLogMapper.insert(handleLog);

        log.info("管理员 {} 处理投诉成功，投诉ID：{}", adminId, dto.getComplaintId());

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean closeComplaint(Long id, Long operatorId, Integer operatorType, String reason) {
        // 查询投诉记录
        ComplaintInfo complaint = complaintInfoMapper.selectById(id);
        if (complaint == null) {
            throw new RuntimeException("投诉不存在");
        }

        // 更新投诉状态
        complaint.setStatus(3); // 已关闭
        complaint.setUpdateTime(LocalDateTime.now());

        complaintInfoMapper.updateById(complaint);

        // 记录操作日志
        ComplaintHandleLog handleLog = new ComplaintHandleLog();
        handleLog.setComplaintId(id);
        handleLog.setOperatorId(operatorId);
        handleLog.setOperatorType(operatorType);
        handleLog.setAction("关闭投诉");
        handleLog.setContent(reason);
        handleLog.setCreateTime(LocalDateTime.now());

        complaintHandleLogMapper.insert(handleLog);

        log.info("关闭投诉成功，投诉ID：{}, 操作人：{}", id, operatorId);

        return true;
    }

    @Override
    public List<ComplaintHandleLogVO> getHandleLogs(Long complaintId) {
        return complaintHandleLogMapper.selectByComplaintId(complaintId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean rateComplaint(Long userId, Long complaintId, Integer satisfied, String feedback) {
        // 查询投诉记录
        ComplaintInfo complaint = complaintInfoMapper.selectById(complaintId);
        if (complaint == null) {
            throw new RuntimeException("投诉不存在");
        }

        // 验证投诉是否属于该用户
        if (!complaint.getUserId().equals(userId)) {
            throw new RuntimeException("无权评价该投诉");
        }

        // 更新评价信息
        complaint.setUserSatisfied(satisfied);
        complaint.setUserFeedback(feedback);
        complaint.setUpdateTime(LocalDateTime.now());

        complaintInfoMapper.updateById(complaint);

        // 记录操作日志
        UserInfo user = userInfoMapper.selectById(userId);
        ComplaintHandleLog handleLog = new ComplaintHandleLog();
        handleLog.setComplaintId(complaintId);
        handleLog.setOperatorId(userId);
        handleLog.setOperatorName(user != null ? user.getNickname() : "用户");
        handleLog.setOperatorType(1); // 用户
        handleLog.setAction("评价投诉");
        handleLog.setContent((satisfied == 1 ? "满意" : "不满意") + (StringUtils.hasText(feedback) ? "：" + feedback : ""));
        handleLog.setCreateTime(LocalDateTime.now());

        complaintHandleLogMapper.insert(handleLog);

        log.info("用户 {} 评价投诉成功，投诉ID：{}", userId, complaintId);

        return true;
    }

    /**
     * 生成投诉编号
     * 格式：CP + 年月日时分秒 + 4位随机数
     */
    private String generateComplaintNo() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String randomStr = String.format("%04d", (int) (Math.random() * 10000));
        return "CP" + dateStr + randomStr;
    }
}
