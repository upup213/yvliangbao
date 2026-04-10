package com.yvliangbao.gateway.controller.user;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yvliangbao.common.pojo.dto.complaint.ComplaintCreateDTO;
import com.yvliangbao.common.pojo.vo.complaint.ComplaintHandleLogVO;
import com.yvliangbao.common.pojo.vo.complaint.ComplaintVO;
import com.yvliangbao.common.result.Result;
import com.yvliangbao.common.service.im.ComplaintService;
import com.yvliangbao.common.util.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 用户端客诉Controller
 *
 * @author 余量宝
 */
@Slf4j
@RestController
@RequestMapping("/complaint")
@Api(tags = "用户端-客诉接口")
public class UserComplaintController {

    @Autowired
    private ComplaintService complaintService;

    @ApiOperation("创建投诉")
    @PostMapping("/create")
    public Result<Long> createComplaint(@Valid @RequestBody ComplaintCreateDTO dto) {
        Long userId = SecurityUtil.getCurrentUserId();
        Long complaintId = complaintService.createComplaint(userId, dto);
        return Result.success(complaintId);
    }

    @ApiOperation("查询自己的投诉列表")
    @GetMapping("/my")
    public Result<IPage<ComplaintVO>> getUserComplaintList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = SecurityUtil.getCurrentUserId();
        IPage<ComplaintVO> result = complaintService.getUserComplaintList(userId, page, size);
        return Result.success(result);
    }

    @ApiOperation("查询投诉详情")
    @GetMapping("/{id}")
    public Result<ComplaintVO> getComplaintDetail(@PathVariable Long id) {
        ComplaintVO complaint = complaintService.getComplaintDetail(id);
        return Result.success(complaint);
    }

    @ApiOperation("评价投诉处理结果")
    @PostMapping("/rate")
    public Result<Boolean> rateComplaint(
            @RequestParam Long complaintId,
            @RequestParam Integer satisfied,
            @RequestParam(required = false) String feedback) {
        Long userId = SecurityUtil.getCurrentUserId();
        boolean result = complaintService.rateComplaint(userId, complaintId, satisfied, feedback);
        return Result.success(result);
    }

    @ApiOperation("查询投诉处理记录")
    @GetMapping("/logs/{complaintId}")
    public Result<List<ComplaintHandleLogVO>> getHandleLogs(@PathVariable Long complaintId) {
        List<ComplaintHandleLogVO> logs = complaintService.getHandleLogs(complaintId);
        return Result.success(logs);
    }
}
