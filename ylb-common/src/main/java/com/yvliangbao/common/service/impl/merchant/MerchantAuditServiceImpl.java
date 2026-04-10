package com.yvliangbao.common.service.impl.merchant;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yvliangbao.common.mapper.merchant.MerchantInfoMapper;
import com.yvliangbao.common.pojo.dto.merchant.MerchantAuditListDTO;
import com.yvliangbao.common.pojo.entity.merchant.MerchantInfo;
import com.yvliangbao.common.pojo.enums.MerchantStatus;
import com.yvliangbao.common.pojo.vo.merchant.MerchantAuditVO;
import com.yvliangbao.common.service.merchant.MerchantAuditService;
import com.yvliangbao.common.util.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 商户审核Service实现
 *
 * @author 余量宝
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MerchantAuditServiceImpl extends ServiceImpl<MerchantInfoMapper, MerchantInfo>
        implements MerchantAuditService {

    @Override
    public IPage<MerchantAuditVO> listPage(MerchantAuditListDTO dto) {
        Page<MerchantInfo> page = new Page<>(dto.getPageNum(), dto.getPageSize());

        LambdaQueryWrapper<MerchantInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(dto.getMerchantName()), MerchantInfo::getMerchantName, dto.getMerchantName())
                .eq(StringUtils.hasText(dto.getContactPhone()), MerchantInfo::getContactPhone, dto.getContactPhone())
                .eq(dto.getStatus() != null, MerchantInfo::getStatus, dto.getStatus());

        // 处理日期条件（避免null拼接）
        if (StringUtils.hasText(dto.getStartDate())) {
            wrapper.ge(MerchantInfo::getCreateTime,
                    LocalDateTime.parse(dto.getStartDate() + " 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        if (StringUtils.hasText(dto.getEndDate())) {
            wrapper.le(MerchantInfo::getCreateTime,
                    LocalDateTime.parse(dto.getEndDate() + " 23:59:59", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }

        wrapper.orderByDesc(MerchantInfo::getCreateTime);

        IPage<MerchantInfo> resultPage = this.page(page, wrapper);

        // 转换为VO
        return resultPage.convert(this::convertToVO);
    }

    @Override
    public MerchantAuditVO getAuditDetail(Long merchantId) {
        MerchantInfo merchant = this.getById(merchantId);
        if (merchant == null) {
            throw new RuntimeException("商户不存在");
        }
        return convertToVO(merchant);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approve(Long merchantId, String remark) {
        MerchantInfo merchant = this.getById(merchantId);
        if (merchant == null) {
            throw new RuntimeException("商户不存在");
        }

        if (merchant.getStatus() != MerchantStatus.PENDING_AUDIT.getCode()) {
            throw new RuntimeException("该商户不是待审核状态");
        }

        // 更新商户状态为正常
        this.update()
                .eq("id", merchantId)
                .set("status", MerchantStatus.NORMAL.getCode())
                .set("audit_time", LocalDateTime.now())
                .set("auditor_id", getCurrentUserId())
                .set("reject_reason", null)
                .update();

        log.info("商户审核通过: merchantId={}, merchantName={}, remark={}", merchantId, merchant.getMerchantName(), remark);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reject(Long merchantId, String reason) {
        if (!StringUtils.hasText(reason)) {
            throw new RuntimeException("请填写拒绝原因");
        }

        MerchantInfo merchant = this.getById(merchantId);
        if (merchant == null) {
            throw new RuntimeException("商户不存在");
        }

        if (merchant.getStatus() != MerchantStatus.PENDING_AUDIT.getCode()) {
            throw new RuntimeException("该商户不是待审核状态");
        }

        // 更新商户状态为已驳回
        this.update()
                .eq("id", merchantId)
                .set("status", MerchantStatus.REJECTED.getCode())
                .set("audit_time", LocalDateTime.now())
                .set("auditor_id", getCurrentUserId())
                .set("reject_reason", reason)
                .update();

        log.info("商户审核拒绝: merchantId={}, merchantName={}, reason={}", merchantId, merchant.getMerchantName(), reason);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enable(Long merchantId) {
        MerchantInfo merchant = this.getById(merchantId);
        if (merchant == null) {
            throw new RuntimeException("商户不存在");
        }

        if (merchant.getStatus() == MerchantStatus.NORMAL.getCode()) {
            throw new RuntimeException("商户已是正常状态");
        }

        // 更新商户状态为正常
        this.update()
                .eq("id", merchantId)
                .set("status", MerchantStatus.NORMAL.getCode())
                .set("ban_reason", null)
                .update();

        log.info("商户启用成功: merchantId={}, merchantName={}", merchantId, merchant.getMerchantName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disable(Long merchantId, String reason) {
        if (!StringUtils.hasText(reason)) {
            throw new RuntimeException("请填写封禁原因");
        }

        MerchantInfo merchant = this.getById(merchantId);
        if (merchant == null) {
            throw new RuntimeException("商户不存在");
        }

        if (merchant.getStatus() == MerchantStatus.DISABLED.getCode()) {
            throw new RuntimeException("商户已被封禁");
        }

        // 更新商户状态为已禁用
        this.update()
                .eq("id", merchantId)
                .set("status", MerchantStatus.DISABLED.getCode())
                .set("ban_reason", reason)
                .update();

        log.info("商户封禁成功: merchantId={}, merchantName={}, reason={}", merchantId, merchant.getMerchantName(), reason);
    }

    private MerchantAuditVO convertToVO(MerchantInfo merchant) {
        MerchantAuditVO vo = new MerchantAuditVO();
        BeanUtils.copyProperties(merchant, vo);

        // 设置状态描述
        MerchantStatus status = MerchantStatus.getByCode(merchant.getStatus());
        vo.setStatusDesc(status != null ? status.getDesc() : "未知");

        // 设置商户类型描述
        String[] typeDescs = {"", "餐饮", "烘焙", "零售", "其他"};
        if (merchant.getMerchantType() != null && merchant.getMerchantType() >= 1 && merchant.getMerchantType() <= 4) {
            vo.setMerchantTypeDesc(typeDescs[merchant.getMerchantType()]);
        } else {
            vo.setMerchantTypeDesc("未知");
        }

        return vo;
    }

    private Long getCurrentUserId() {
        try {
            return UserContext.getUserId();
        } catch (Exception e) {
            return 0L;
        }
    }
}
