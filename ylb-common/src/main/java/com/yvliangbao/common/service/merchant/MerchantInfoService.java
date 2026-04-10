package com.yvliangbao.common.service.merchant;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yvliangbao.common.pojo.dto.merchant.MerchantCompleteInfoDTO;
import com.yvliangbao.common.pojo.dto.merchant.MerchantLoginDTO;
import com.yvliangbao.common.pojo.dto.merchant.MerchantRegisterDTO;
import com.yvliangbao.common.pojo.dto.merchant.MerchantSimpleRegisterDTO;
import com.yvliangbao.common.pojo.entity.merchant.MerchantInfo;
import com.yvliangbao.common.pojo.vo.merchant.*;


/**
 * 商户信息 Service
 *
 * @author 余量宝
 */
public interface MerchantInfoService extends IService<MerchantInfo> {
    
    /**
     * 根据商户编号查询
     *
     * @param merchantNo 商户编号
     * @return 商户信息
     */
    MerchantInfo getByMerchantNo(String merchantNo);
    
    /**
     * 根据联系电话查询
     *
     * @param phone 联系电话
     * @return 商户信息
     */
    MerchantInfo getByContactPhone(String phone);
    
    /**
     * 生成商户编号
     *
     * @return 商户编号
     */
    String generateMerchantNo();
    
    /**
     * 商户入驻
     *
     * @param dto 商户注册信息
     * @return 注册结果
     */
    MerchantInfoVO register(MerchantRegisterDTO dto);

    /**
     * 商户简单注册（第一步）
     *
     * @param dto 简单注册信息
     * @return 注册结果
     */
    MerchantInfoVO simpleRegister(MerchantSimpleRegisterDTO dto);

    /**
     * 商户登录
     *
     * @param dto 登录信息
     * @return 登录结果
     */
    MerchantInfoVO login(MerchantLoginDTO dto);
    
    /**
     * 获取商户信息
     *
     * @param merchantId 商户ID
     * @return 商户信息
     */
    MerchantInfoVO getMerchantInfo(Long merchantId);

    /**
     * 完善商户信息
     *
     * @param merchantId 商户ID
     * @param dto 完善信息DTO
     * @return 商户信息
     */
    MerchantInfoVO completeInfo(Long merchantId, MerchantCompleteInfoDTO dto);

    /**
     * 获取商户统计数据
     *
     * @param merchantId 商户ID
     * @return 统计数据
     */
    MerchantStatsVO getMerchantStats(Long merchantId);

    /**
     * 获取营收趋势
     *
     * @param merchantId 商户ID
     * @param days 天数
     * @return 营收趋势
     */
    RevenueTrendVO getRevenueTrend(Long merchantId, Integer days);

    /**
     * 获取历史营收
     *
     * @param merchantId 商户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 历史营收
     */
    RevenueHistoryVO getRevenueHistory(Long merchantId, String startDate, String endDate);

    /**
     * 分页获取历史营收明细
     *
     * @param merchantId 商户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页数据
     */
    Page<DailyStatsVO> getRevenueHistoryPage(Long merchantId, String startDate, String endDate, Integer pageNum, Integer pageSize);
}
