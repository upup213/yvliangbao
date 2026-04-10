package com.yvliangbao.common.mapper.merchant;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yvliangbao.common.pojo.entity.merchant.MerchantViolation;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商户违规记录Mapper
 *
 * @author 余量宝
 */
@Mapper
public interface MerchantViolationMapper extends BaseMapper<MerchantViolation> {
}
