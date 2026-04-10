package com.yvliangbao.common.pojo.enums;

import lombok.Getter;

/**
 * 违规等级枚举
 *
 * @author 余量宝
 */
@Getter
public enum ViolationLevel {

    /**
     * 轻微
     */
    LIGHT(1, "轻微"),

    /**
     * 一般
     */
    NORMAL(2, "一般"),

    /**
     * 严重
     */
    SERIOUS(3, "严重"),

    /**
     * 极其严重
     */
    EXTREME(4, "极其严重");

    private final Integer code;
    private final String desc;

    ViolationLevel(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ViolationLevel getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (ViolationLevel level : values()) {
            if (level.getCode().equals(code)) {
                return level;
            }
        }
        return null;
    }
}
