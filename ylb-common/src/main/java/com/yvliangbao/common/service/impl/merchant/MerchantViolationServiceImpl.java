package com.yvliangbao.common.service.impl.merchant;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yvliangbao.common.mapper.merchant.MerchantViolationMapper;
import com.yvliangbao.common.pojo.dto.merchant.MerchantViolationCreateDTO;
import com.yvliangbao.common.pojo.dto.merchant.MerchantViolationDTO;
import com.yvliangbao.common.pojo.entity.merchant.MerchantInfo;
import com.yvliangbao.common.pojo.entity.merchant.MerchantViolation;
import com.yvliangbao.common.pojo.enums.HandleType;
import com.yvliangbao.common.pojo.enums.MerchantStatus;
import com.yvliangbao.common.pojo.enums.ViolationLevel;
import com.yvliangbao.common.pojo.enums.ViolationType;
import com.yvliangbao.common.pojo.vo.merchant.MerchantViolationVO;
import com.yvliangbao.common.service.merchant.MerchantInfoService;
import com.yvliangbao.common.service.merchant.MerchantViolationService;
import com.yvliangbao.common.util.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 商户违规记录Service实现
 *
 * @author 余量宝
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MerchantViolationServiceImpl extends ServiceImpl<MerchantViolationMapper, MerchantViolation>
        implements MerchantViolationService {

    private final MerchantInfoService merchantInfoService;

    @Override
    public IPage<MerchantViolationVO> listPage(MerchantViolationDTO dto) {
        Page<MerchantViolation> page = new Page<>(dto.getPageNum(), dto.getPageSize());

        LambdaQueryWrapper<MerchantViolation> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(dto.getMerchantName()), MerchantViolation::getMerchantName, dto.getMerchantName())
                .eq(dto.getViolationType() != null, MerchantViolation::getViolationType, dto.getViolationType())
                .eq(dto.getViolationLevel() != null, MerchantViolation::getViolationLevel, dto.getViolationLevel())
                .eq(dto.getStatus() != null, MerchantViolation::getStatus, dto.getStatus())
                .ge(StringUtils.hasText(dto.getStartDate()), MerchantViolation::getCreateTime, dto.getStartDate() + " 00:00:00")
                .le(StringUtils.hasText(dto.getEndDate()), MerchantViolation::getCreateTime, dto.getEndDate() + " 23:59:59")
                .orderByDesc(MerchantViolation::getCreateTime);

        IPage<MerchantViolation> resultPage = this.page(page, wrapper);

        // 转换为VO
        return resultPage.convert(this::convertToVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createViolation(MerchantViolationCreateDTO dto) {
        // 查询商户信息
        MerchantInfo merchant = merchantInfoService.getById(dto.getMerchantId());
        if (merchant == null) {
            throw new RuntimeException("商户不存在");
        }

        MerchantViolation violation = new MerchantViolation();
        violation.setMerchantId(dto.getMerchantId());
        violation.setMerchantName(merchant.getMerchantName());
        violation.setViolationType(dto.getViolationType());
        violation.setViolationTypeDesc(ViolationType.getByCode(dto.getViolationType()) != null
                ? ViolationType.getByCode(dto.getViolationType()).getDesc() : "未知");
        violation.setViolationLevel(dto.getViolationLevel());
        violation.setViolationLevelDesc(ViolationLevel.getByCode(dto.getViolationLevel()) != null
                ? ViolationLevel.getByCode(dto.getViolationLevel()).getDesc() : "未知");
        violation.setDescription(dto.getDescription());
        violation.setHandleType(dto.getHandleType());
        violation.setHandleTypeDesc(HandleType.getByCode(dto.getHandleType()) != null
                ? HandleType.getByCode(dto.getHandleType()).getDesc() : "未知");
        violation.setPenaltyAmount(dto.getPenaltyAmount());
        violation.setRemark(dto.getRemark());
        violation.setOrderId(dto.getOrderId());
        violation.setOrderNo(dto.getOrderNo());
        violation.setStatus(0); // 待处理

        // 获取处理人信息
        try {
            violation.setHandlerId(UserContext.getUserId());
        } catch (Exception e) {
            violation.setHandlerId(0L);
        }

        this.save(violation);

        // 如果处理方式是封禁，则同时封禁商户
        if (dto.getHandleType() != null && dto.getHandleType() == HandleType.BAN.getCode()) {
            merchantInfoService.update()
                    .eq("id", dto.getMerchantId())
                    .set("status", MerchantStatus.DISABLED.getCode())
                    .set("ban_reason", dto.getDescription())
                    .update();
        }

        log.info("创建违规记录成功: violationId={}, merchantId={}", violation.getId(), dto.getMerchantId());
        return violation.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleViolation(Long id, Integer handleType, String remark) {
        MerchantViolation violation = this.getById(id);
        if (violation == null) {
            throw new RuntimeException("违规记录不存在");
        }

        if (violation.getStatus() != 0) {
            throw new RuntimeException("该违规记录已处理");
        }

        // 更新违规记录
        violation.setHandleType(handleType);
        violation.setHandleTypeDesc(HandleType.getByCode(handleType) != null
                ? HandleType.getByCode(handleType).getDesc() : "未知");
        violation.setRemark(remark);
        violation.setStatus(1); // 已处理

        try {
            violation.setHandlerId(UserContext.getUserId());
        } catch (Exception e) {
            violation.setHandlerId(0L);
        }

        this.updateById(violation);

        // 如果处理方式是封禁，则同时封禁商户
        if (handleType == HandleType.BAN.getCode()) {
            merchantInfoService.update()
                    .eq("id", violation.getMerchantId())
                    .set("status", MerchantStatus.DISABLED.getCode())
                    .set("ban_reason", violation.getDescription())
                    .update();
        }

        log.info("处理违规记录成功: violationId={}, handleType={}", id, handleType);
    }

    private MerchantViolationVO convertToVO(MerchantViolation violation) {
        MerchantViolationVO vo = new MerchantViolationVO();
        BeanUtils.copyProperties(violation, vo);

        // 设置状态描述
        switch (violation.getStatus()) {
            case 0:
                vo.setStatusDesc("待处理");
                break;
            case 1:
                vo.setStatusDesc("已处理");
                break;
            case 2:
                vo.setStatusDesc("已申诉");
                break;
            default:
                vo.setStatusDesc("未知");
        }

        return vo;
    }
}
