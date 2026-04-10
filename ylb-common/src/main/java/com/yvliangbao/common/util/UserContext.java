package com.yvliangbao.common.util;

/**
 * 用户上下文工具类（基于 ThreadLocal）
 *
 * @author 余量宝
 */
public class UserContext {

    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> OPENID = new ThreadLocal<>();

    /**
     * 设置用户ID
     */
    public static void setUserId(Long userId) {
        USER_ID.set(userId);
    }

    /**
     * 获取用户ID
     */
    public static Long getUserId() {
        return USER_ID.get();
    }

    /**
     * 设置 openid
     */
    public static void setOpenid(String openid) {
        OPENID.set(openid);
    }

    /**
     * 获取 openid
     */
    public static String getOpenid() {
        return OPENID.get();
    }

    /**
     * 清除上下文（请求结束时调用）
     */
    public static void clear() {
        USER_ID.remove();
        OPENID.remove();
    }
}
