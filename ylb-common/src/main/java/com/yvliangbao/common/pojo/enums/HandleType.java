package com.yvliangbao.common.pojo.enums;

import lombok.Getter;

/**
 * 处理方式枚举
 *
 * @author 余量宝
 */
@Getter
public enum HandleType {

    /**
     * 警告
     */
    WARNING(1, "警告"),

    /**
     * 罚款
     */
    FINE(2, "罚款"),

    /**
     * 限单
     */
    LIMIT_ORDER(3, "限单"),

    /**
     * 封禁
     */
    BAN(4, "封禁");

    private final Integer code;
    private final String desc;

    HandleType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static HandleType getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (HandleType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
