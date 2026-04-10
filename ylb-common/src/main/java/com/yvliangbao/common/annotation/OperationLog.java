package com.yvliangbao.common.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解
 * 标注在Controller方法上，自动记录操作日志
 *
 * @author 余量宝
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {
    
    /**
     * 操作模块
     */
    String module() default "";
    
    /**
     * 操作类型
     */
    OperationType type() default OperationType.OTHER;
    
    /**
     * 操作描述
     */
    String description() default "";
    
    /**
     * 是否保存请求参数
     */
    boolean saveRequestData() default true;
    
    /**
     * 是否保存响应结果
     */
    boolean saveResponseData() default true;
    
    /**
     * 操作类型枚举
     */
    enum OperationType {
        /**
         * 新增
         */
        CREATE("新增"),
        /**
         * 修改
         */
        UPDATE("修改"),
        /**
         * 删除
         */
        DELETE("删除"),
        /**
         * 查询
         */
        QUERY("查询"),
        /**
         * 导出
         */
        EXPORT("导出"),
        /**
         * 导入
         */
        IMPORT("导入"),
        /**
         * 登录
         */
        LOGIN("登录"),
        /**
         * 登出
         */
        LOGOUT("登出"),
        /**
         * 其他
         */
        OTHER("其他");
        
        private final String description;
        
        OperationType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
}
