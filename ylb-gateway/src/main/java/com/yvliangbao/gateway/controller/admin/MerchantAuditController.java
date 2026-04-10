package com.yvliangbao.gateway.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yvliangbao.common.pojo.dto.merchant.MerchantAuditListDTO;
import com.yvliangbao.common.pojo.dto.merchant.MerchantViolationCreateDTO;
import com.yvliangbao.common.pojo.dto.merchant.MerchantViolationDTO;
import com.yvliangbao.common.pojo.vo.merchant.MerchantAuditVO;
import com.yvliangbao.common.pojo.vo.merchant.MerchantViolationVO;
import com.yvliangbao.common.result.Result;
import com.yvliangbao.common.service.merchant.MerchantAuditService;
import com.yvliangbao.common.service.merchant.MerchantViolationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 商户审核管理控制器（平台端）
 *
 * @author 余量宝
 */
@Slf4j
@Api(tags = "平台端-商户审核管理")
@RestController
@RequestMapping("/admin/merchant")
@RequiredArgsConstructor
public class MerchantAuditController {

    private final MerchantAuditService merchantAuditService;
    private final MerchantViolationService merchantViolationService;

    @ApiOperation("商户审核列表")
    @GetMapping("/audit/list")
    @PreAuthorize("hasAuthority('merchant:audit:view')")
    public Result<IPage<MerchantAuditVO>> getAuditList(@Validated MerchantAuditListDTO dto) {
        IPage<MerchantAuditVO> page = merchantAuditService.listPage(dto);
        return Result.success(page);
    }

    @ApiOperation("商户资质详情")
    @GetMapping("/audit/detail/{merchantId}")
    @PreAuthorize("hasAuthority('merchant:audit:view')")
    public Result<MerchantAuditVO> getAuditDetail(
            @ApiParam(value = "商户ID", required = true) @PathVariable Long merchantId) {
        MerchantAuditVO vo = merchantAuditService.getAuditDetail(merchantId);
        return Result.success(vo);
    }

    @ApiOperation("审核通过")
    @PostMapping("/audit/approve/{merchantId}")
    @PreAuthorize("hasAuthority('merchant:audit:pass')")
    public Result<Void> approve(
            @ApiParam(value = "商户ID", required = true) @PathVariable Long merchantId,
            @ApiParam(value = "备注") @RequestParam(required = false) String remark) {
        merchantAuditService.approve(merchantId, remark);
        return Result.success();
    }

    @ApiOperation("审核拒绝")
    @PostMapping("/audit/reject/{merchantId}")
    @PreAuthorize("hasAuthority('merchant:audit:reject')")
    public Result<Void> reject(
            @ApiParam(value = "商户ID", required = true) @PathVariable Long merchantId,
            @ApiParam(value = "拒绝原因", required = true) @RequestParam String reason) {
        merchantAuditService.reject(merchantId, reason);
        return Result.success();
    }

    @ApiOperation("启用商户")
    @PostMapping("/enable/{merchantId}")
    @PreAuthorize("hasAuthority('merchant:unban')")
    public Result<Void> enable(
            @ApiParam(value = "商户ID", required = true) @PathVariable Long merchantId) {
        merchantAuditService.enable(merchantId);
        return Result.success();
    }

    @ApiOperation("禁用商户")
    @PostMapping("/disable/{merchantId}")
    @PreAuthorize("hasAuthority('merchant:ban')")
    public Result<Void> disable(
            @ApiParam(value = "商户ID", required = true) @PathVariable Long merchantId,
            @ApiParam(value = "封禁原因", required = true) @RequestParam String reason) {
        merchantAuditService.disable(merchantId, reason);
        return Result.success();
    }

    @ApiOperation("商户列表")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('merchant:list') or hasAuthority('merchant:audit:view')")
    public Result<IPage<MerchantAuditVO>> getMerchantList(
            @ApiParam(value = "商户名称（模糊搜索）") @RequestParam(required = false) String merchantName,
            @ApiParam(value = "联系人手机号") @RequestParam(required = false) String contactPhone,
            @ApiParam(value = "商户状态") @RequestParam(required = false) Integer status,
            @ApiParam(value = "当前页码") @RequestParam(defaultValue = "1") int page,
            @ApiParam(value = "每页数量") @RequestParam(defaultValue = "10") int size) {
        MerchantAuditListDTO dto = new MerchantAuditListDTO();
        dto.setMerchantName(merchantName);
        dto.setContactPhone(contactPhone);
        dto.setStatus(status);
        dto.setPageNum(page);
        dto.setPageSize(size);
        IPage<MerchantAuditVO> pageResult = merchantAuditService.listPage(dto);
        return Result.success(pageResult);
    }

    // ==================== 违规记录管理 ====================

    @ApiOperation("违规记录列表")
    @GetMapping("/violation/list")
    public Result<IPage<MerchantViolationVO>> getViolationList(@Validated MerchantViolationDTO dto) {
        IPage<MerchantViolationVO> page = merchantViolationService.listPage(dto);
        return Result.success(page);
    }

    @ApiOperation("创建违规记录")
    @PostMapping("/violation/create")
    public Result<Long> createViolation(@Validated @RequestBody MerchantViolationCreateDTO dto) {
        Long violationId = merchantViolationService.createViolation(dto);
        return Result.success(violationId);
    }

    @ApiOperation("处理违规记录")
    @PostMapping("/violation/handle/{id}")
    public Result<Void> handleViolation(
            @ApiParam(value = "违规记录ID", required = true) @PathVariable Long id,
            @ApiParam(value = "处理方式：1-警告，2-罚款，3-限单，4-封禁", required = true) @RequestParam Integer handleType,
            @ApiParam(value = "备注") @RequestParam(required = false) String remark) {
        merchantViolationService.handleViolation(id, handleType, remark);
        return Result.success();
    }
}
