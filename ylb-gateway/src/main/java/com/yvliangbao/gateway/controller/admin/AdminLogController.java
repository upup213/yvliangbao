package com.yvliangbao.gateway.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yvliangbao.common.pojo.vo.admin.OperationLogVO;
import com.yvliangbao.common.result.Result;
import com.yvliangbao.common.service.admin.AdminOperationLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Api(tags = "操作日志")
@RestController
@RequestMapping("/admin/log")
public class AdminLogController {

    @Autowired
    private AdminOperationLogService adminOperationLogService;

    @ApiOperation("获取日志列表")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('system:log:view')")
    public Result<IPage<OperationLogVO>> list(@RequestParam(required = false) Long adminId,
                                              @RequestParam(required = false) String module,
                                              @RequestParam(required = false) String operationType,
                                              @RequestParam(required = false) String startTime,
                                              @RequestParam(required = false) String endTime,
                                              @RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "20") int size) {
        return Result.success(adminOperationLogService.pageList(adminId, module, operationType, startTime, endTime, page, size));
    }

    @ApiOperation("获取日志详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:log:view')")
    public Result<OperationLogVO> getDetail(@PathVariable Long id) {
        return Result.success(adminOperationLogService.getById(id) != null ? 
            new OperationLogVO() : null);
    }
}
