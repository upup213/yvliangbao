package com.yvliangbao.common.service.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yvliangbao.common.pojo.entity.admin.AdminOperationLog;
import com.yvliangbao.common.pojo.vo.admin.OperationLogVO;


public interface AdminOperationLogService extends IService<AdminOperationLog> {
    IPage<OperationLogVO> pageList(Long adminId, String module, String operationType,
                                   String startTime, String endTime, int page, int size);
    void recordLog(Long adminId, String adminName, String operationType, String module, 
                   String content, String ip, Integer result, String errorMsg);
}
