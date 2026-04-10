package com.yvliangbao.common.service.merchant;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yvliangbao.common.pojo.dto.merchant.MerchantViolationCreateDTO;
import com.yvliangbao.common.pojo.dto.merchant.MerchantViolationDTO;
import com.yvliangbao.common.pojo.entity.merchant.MerchantViolation;
import com.yvliangbao.common.pojo.vo.merchant.MerchantViolationVO;


/**
 * 商户违规记录Service接口
 *
 * @author 余量宝
 */
public interface MerchantViolationService extends IService<MerchantViolation> {

    /**
     * 分页查询违规记录
     *
     * @param dto 查询条件
     * @return 分页结果
     */
    IPage<MerchantViolationVO> listPage(MerchantViolationDTO dto);

    /**
     * 创建违规记录
     *
     * @param dto 违规信息
     * @return 违规记录ID
     */
    Long createViolation(MerchantViolationCreateDTO dto);

    /**
     * 处理违规记录
     *
     * @param id      违规记录ID
     * @param handleType 处理方式
     * @param remark  备注
     */
    void handleViolation(Long id, Integer handleType, String remark);
}
