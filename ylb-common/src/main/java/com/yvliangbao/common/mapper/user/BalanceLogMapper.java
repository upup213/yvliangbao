package com.yvliangbao.common.mapper.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yvliangbao.common.pojo.entity.user.BalanceLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.List;

/**
 * 余额变动记录Mapper
 *
 * @author 余量宝
 */
@Mapper
public interface BalanceLogMapper extends BaseMapper<BalanceLog> {

    /**
     * 原子增加余额（充值）
     *
     * @param userId 用户ID
     * @param amount 增加金额
     * @return 更新行数
     */
    @Update("UPDATE user_info SET balance = balance + #{amount}, " +
            "version = version + 1, update_time = NOW() " +
            "WHERE id = #{userId} AND deleted = 0")
    int increaseBalance(@Param("userId") Long userId, @Param("amount") BigDecimal amount);

    /**
     * 原子扣减余额（消费）
     *
     * @param userId 用户ID
     * @param amount 扣减金额
     * @return 更新行数（0表示余额不足）
     */
    @Update("UPDATE user_info SET balance = balance - #{amount}, update_time = NOW() " +
            "WHERE id = #{userId} AND balance >= #{amount} AND deleted = 0")
    int decreaseBalance(@Param("userId") Long userId, @Param("amount") BigDecimal amount);

    /**
     * 原子扣减余额（消费）- 乐观锁版本
     *
     * @param userId 用户ID
     * @param amount 扣减金额
     * @param expectedVersion 期望版本号
     * @return 更新行数（0表示余额不足或版本号不匹配）
     */
    @Update("UPDATE user_info SET balance = balance - #{amount}, " +
            "version = version + 1, update_time = NOW() " +
            "WHERE id = #{userId} AND version = #{expectedVersion} " +
            "AND balance >= #{amount} AND deleted = 0")
    int decreaseBalanceWithVersion(@Param("userId") Long userId, 
                                   @Param("amount") BigDecimal amount,
                                   @Param("expectedVersion") Integer expectedVersion);

    /**
     * 查询用户余额变动记录
     *
     * @param userId 用户ID
     * @param offset 偏移量
     * @param limit 限制条数
     * @return 记录列表
     */
    @Select("SELECT * FROM balance_log WHERE user_id = #{userId} ORDER BY create_time DESC LIMIT #{offset}, #{limit}")
    List<BalanceLog> selectByUserId(@Param("userId") Long userId, 
                                    @Param("offset") Integer offset, 
                                    @Param("limit") Integer limit);

    /**
     * 统计用户余额变动记录数
     *
     * @param userId 用户ID
     * @return 记录数
     */
    @Select("SELECT COUNT(*) FROM balance_log WHERE user_id = #{userId}")
    int countByUserId(@Param("userId") Long userId);

    /**
     * 统计用户累计充值金额
     *
     * @param userId 用户ID
     * @return 累计充值金额
     */
    @Select("SELECT COALESCE(SUM(change_amount), 0) FROM balance_log " +
            "WHERE user_id = #{userId} " +
            "  AND change_type = 1 " +  // 充值类型
            "  AND change_amount > 0")
    BigDecimal sumRechargedAmount(@Param("userId") Long userId);
}
