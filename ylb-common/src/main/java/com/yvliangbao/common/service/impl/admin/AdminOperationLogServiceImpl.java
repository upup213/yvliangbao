package com.yvliangbao.common.service.impl.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.yvliangbao.common.mapper.admin.AdminOperationLogMapper;
import com.yvliangbao.common.pojo.entity.admin.AdminOperationLog;
import com.yvliangbao.common.pojo.vo.admin.OperationLogVO;
import com.yvliangbao.common.service.admin.AdminOperationLogService;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class AdminOperationLogServiceImpl extends ServiceImpl<AdminOperationLogMapper, AdminOperationLog> implements AdminOperationLogService {

    @Override
    public IPage<OperationLogVO> pageList(Long adminId, String module, String operationType, String startTime, String endTime, int page, int size) {
        Page<AdminOperationLog> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<AdminOperationLog> wrapper = new LambdaQueryWrapper<>();
        if (adminId != null) wrapper.eq(AdminOperationLog::getAdminId, adminId);
        if (StringUtils.hasText(module)) wrapper.eq(AdminOperationLog::getModule, module);
        if (StringUtils.hasText(operationType)) wrapper.eq(AdminOperationLog::getOperationType, operationType);
        if (StringUtils.hasText(startTime)) wrapper.ge(AdminOperationLog::getCreateTime, LocalDateTime.parse(startTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        if (StringUtils.hasText(endTime)) wrapper.le(AdminOperationLog::getCreateTime, LocalDateTime.parse(endTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        wrapper.orderByDesc(AdminOperationLog::getCreateTime);
        IPage<AdminOperationLog> result = page(pageParam, wrapper);
        return result.convert(this::toVO);
    }

    @Override
    @Async
    public void recordLog(Long adminId, String adminName, String operationType, String module, String content, String ip, Integer result, String errorMsg) {
        AdminOperationLog log = new AdminOperationLog();
        log.setAdminId(adminId);
        log.setAdminName(adminName);
        log.setOperationType(operationType);
        log.setModule(module);
        log.setContent(content);
        log.setIp(ip);
        log.setResult(result);
        log.setErrorMsg(errorMsg);
        save(log);
    }

    private OperationLogVO toVO(AdminOperationLog entity) {
        OperationLogVO vo = new OperationLogVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
