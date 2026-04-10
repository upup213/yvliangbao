package com.yvliangbao.common.service.impl.admin;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yvliangbao.common.exception.BusinessException;
import com.yvliangbao.common.mapper.admin.AdminInfoMapper;
import com.yvliangbao.common.pojo.entity.admin.AdminInfo;
import com.yvliangbao.common.service.admin.AdminInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 平台管理员 Service 实现
 *
 * @author 余量宝
 */
@Slf4j
@Service
public class AdminInfoServiceImpl extends ServiceImpl<AdminInfoMapper, AdminInfo> implements AdminInfoService {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public AdminInfo login(String username, String password) {
        log.info("管理员登录: username={}", username);

        // 查询管理员
        AdminInfo admin = this.lambdaQuery()
                .eq(AdminInfo::getUsername, username)
                .one();

        if (admin == null) {
            throw new BusinessException("用户名或密码错误");
        }

        // 验证密码（支持BCrypt和明文两种方式）
        boolean passwordMatch = false;
        String storedPassword = admin.getPassword();
        
        log.info("===== 密码验证调试 =====");
        log.info("输入密码: {}", password);
        log.info("数据库密码: {}", storedPassword);
        log.info("是否BCrypt格式: {}", storedPassword != null && storedPassword.startsWith("$2a$"));
        
        if (storedPassword != null && storedPassword.startsWith("$2a$")) {
            // BCrypt加密密码
            passwordMatch = passwordEncoder.matches(password, storedPassword);
            log.info("BCrypt匹配结果: {}", passwordMatch);
            
            // 生成一个新的BCrypt密码用于对比
            String newHash = passwordEncoder.encode(password);
            log.info("输入密码重新加密后: {}", newHash);
        } else {
            // 明文密码（兼容旧数据）
            passwordMatch = password != null && password.equals(storedPassword);
            log.info("明文匹配结果: {}", passwordMatch);
        }
        log.info("===== 密码验证结束 =====");

        if (!passwordMatch) {
            throw new BusinessException("用户名或密码错误");
        }

        // 检查状态
        if (admin.getStatus() != 1) {
            throw new BusinessException("账号已被禁用");
        }

        // 更新最后登录时间
        admin.setLastLoginTime(LocalDateTime.now());
        this.updateById(admin);

        log.info("管理员登录成功: adminId={}", admin.getId());
        return admin;
    }
}
