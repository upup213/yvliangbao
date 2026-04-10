package com.yvliangbao.common.service.user;

import com.yvliangbao.common.pojo.dto.balance.RechargeDTO;
import com.yvliangbao.common.pojo.vo.balance.BalanceLogVO;
import com.yvliangbao.common.pojo.vo.balance.RechargeVO;

import java.math.BigDecimal;
import java.util.List;

/**
 * 余额服务接口
 *
 * @author 余量宝
 */
public interface BalanceService {

    /**
     * 用户充值
     *
     * @param dto 充值请求
     * @return 充值结果
     */
    RechargeVO recharge(RechargeDTO dto);

    /**
     * 获取余额变动记录
     *
     * @param page 页码
     * @param size 每页条数
     * @return 记录列表
     */
    List<BalanceLogVO> getBalanceLogs(Integer page, Integer size);

    /**
     * 增加用户余额（内部调用）
     *
     * @param userId 用户ID
     * @param amount 金额
     * @param changeType 变动类型
     * @param relatedNo 关联单号
     * @param remark 备注
     * @return 是否成功
     */
    boolean increaseBalance(Long userId, BigDecimal amount, Integer changeType, String relatedNo, String remark);

    /**
     * 扣减用户余额（内部调用）
     *
     * @param userId 用户ID
     * @param amount 金额
     * @param changeType 变动类型
     * @param relatedNo 关联单号
     * @param remark 备注
     * @return 是否成功
     */
    boolean decreaseBalance(Long userId, BigDecimal amount, Integer changeType, String relatedNo, String remark);

    /**
     * 扣减用户余额（乐观锁版本）
     *
     * @param userId 用户ID
     * @param amount 金额
     * @param expectedVersion 期望版本号
     * @param changeType 变动类型
     * @param relatedNo 关联单号
     * @param remark 备注
     * @return 是否成功（版本号不匹配或余额不足返回false）
     */
    boolean decreaseBalanceWithVersion(Long userId, BigDecimal amount, Integer expectedVersion, 
                                       Integer changeType, String relatedNo, String remark);
}
