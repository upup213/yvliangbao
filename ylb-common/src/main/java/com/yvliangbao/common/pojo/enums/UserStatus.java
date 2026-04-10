package com.yvliangbao.common.pojo.enums;

import lombok.Getter;

/**
 * 用户状态枚举
 *
 * @author 余量宝
 */
@Getter
public enum UserStatus {

    /**
     * 禁用
     */
    DISABLED(0, "禁用"),

    /**
     * 正常
     */
    NORMAL(1, "正常");

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 状态描述
     */
    private final String desc;

    UserStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据状态码获取枚举
     */
    public static UserStatus getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (UserStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 判断是否正常状态
     */
    public boolean isNormal() {
        return this == NORMAL;
    }
}
