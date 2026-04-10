package com.yvliangbao.common.service.user;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yvliangbao.common.pojo.dto.user.UserInfoVO;
import com.yvliangbao.common.pojo.dto.user.UserLoginDTO;
import com.yvliangbao.common.pojo.dto.user.UserUpdateDTO;
import com.yvliangbao.common.pojo.entity.user.UserInfo;
import com.yvliangbao.common.pojo.vo.user.UserStatsVO;


/**
 * 用户信息 Service
 *
 * @author 余量宝
 */
public interface UserInfoService extends IService<UserInfo> {
    
    /**
     * 根据openid查询用户
     *
     * @param openid 微信openid
     * @return 用户信息
     */
    UserInfo getByOpenid(String openid);
    
    /**
     * 根据手机号查询用户
     *
     * @param phone 手机号
     * @return 用户信息
     */
    UserInfo getByPhone(String phone);

    /**
     * 微信授权登录
     *
     * @param dto 登录请求
     * @return 用户信息VO（含token）
     */
    UserInfoVO wechatLogin(UserLoginDTO dto);

    /**
     * 获取用户信息（指定用户ID）
     * 适用场景：管理后台、内部服务调用
     *
     * @param userId 用户ID
     * @return 用户信息VO
     */
    UserInfoVO getUserInfo(Long userId);

    /**
     * 获取当前登录用户信息
     * 适用场景：用户端接口
     *
     * @return 用户信息VO
     */
    UserInfoVO getCurrentUserInfo();

    /**
     * 获取用户统计数据（当前用户）
     *
     * @return 统计数据VO
     */
    UserStatsVO getUserStats();

    /**
     * 更新用户信息（当前用户）
     *
     * @param dto 更新请求
     * @return 用户信息VO
     */
    UserInfoVO updateCurrentUserInfo(UserUpdateDTO dto);

    /**
     * 更新用户信息（别名，调用updateCurrentUserInfo）
     *
     * @param dto 更新请求
     * @return 用户信息VO
     */
    default UserInfoVO updateUserInfo(UserUpdateDTO dto) {
        return updateCurrentUserInfo(dto);
    }

    /**
     * 绑定手机号（指定用户ID）
     *
     * @param userId 用户ID
     * @param phone 手机号
     * @return 用户信息VO
     */
    UserInfoVO bindPhone(Long userId, String phone);

    /**
     * 绑定手机号（当前用户）
     *
     * @param phone 手机号
     * @return 用户信息VO
     */
    UserInfoVO bindCurrentPhone(String phone);
}
