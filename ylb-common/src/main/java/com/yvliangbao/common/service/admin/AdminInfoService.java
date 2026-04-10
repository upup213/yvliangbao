package com.yvliangbao.common.service.admin;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yvliangbao.common.pojo.entity.admin.AdminInfo;


/**
 * 平台管理员 Service
 *
 * @author 余量宝
 */
public interface AdminInfoService extends IService<AdminInfo> {

    /**
     * 管理员登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 登录信息（包含token）
     */
    AdminInfo login(String username, String password);
}
