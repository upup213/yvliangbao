package com.yvliangbao.gateway.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yvliangbao.common.pojo.dto.complaint.ComplaintHandleDTO;
import com.yvliangbao.common.pojo.dto.complaint.ComplaintQueryDTO;
import com.yvliangbao.common.pojo.vo.complaint.ComplaintVO;
import com.yvliangbao.common.result.Result;
import com.yvliangbao.common.service.im.ComplaintService;
import com.yvliangbao.common.util.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 平台端客诉管理Controller
 *
 * @author 余量宝
 */
@Slf4j
@RestController
@RequestMapping("/complaint/admin")
@Api(tags = "平台端-客诉管理接口")
public class AdminComplaintController {

    @Autowired
    private ComplaintService complaintService;

    @ApiOperation("分页查询投诉列表")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('complaint:list') or hasAuthority('*:*')")
    public Result<IPage<ComplaintVO>> getComplaintPage(ComplaintQueryDTO query) {
        IPage<ComplaintVO> result = complaintService.getComplaintPage(query);
        return Result.success(result);
    }

    @ApiOperation("处理投诉")
    @PostMapping("/handle")
    @PreAuthorize("hasAuthority('complaint:list:handle') or hasAuthority('*:*')")
    public Result<Boolean> handleComplaint(@Valid @RequestBody ComplaintHandleDTO dto) {
        Long adminId = SecurityUtil.getCurrentUserId();
        String adminName = "管理员";
        boolean result = complaintService.handleComplaint(adminId, adminName, dto);
        return Result.success(result);
    }

    @ApiOperation("关闭投诉")
    @PostMapping("/close/{id}")
    @PreAuthorize("hasAuthority('complaint:list:close') or hasAuthority('*:*')")
    public Result<Boolean> closeComplaint(
            @PathVariable Long id,
            @RequestParam String reason) {
        Long adminId = SecurityUtil.getCurrentUserId();
        boolean result = complaintService.closeComplaint(id, adminId, 3, reason);
        return Result.success(result);
    }
}
