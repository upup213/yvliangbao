package com.yvliangbao.common.mapper.merchant;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yvliangbao.common.pojo.entity.merchant.MerchantInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 商户信息 Mapper
 *
 * @author 余量宝
 */
@Mapper
public interface MerchantInfoMapper extends BaseMapper<MerchantInfo> {

    @Update("UPDATE merchant_info SET balance = #{newBalance}, version = version + 1 " +
            "WHERE id = #{merchantId} AND version = #{expectedVersion}")
    int updateBalanceWithVersion(@Param("merchantId") Long merchantId,
                                  @Param("expectedVersion") Integer expectedVersion,
                                  @Param("newBalance") Long newBalance);

}
