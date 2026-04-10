package com.yvliangbao.common.result;

import lombok.Getter;

/**
 * 响应状态码
 *
 * @author 余量宝
 * @date 2026-02-28
 */
@Getter
public enum ResultCode {

    /**
     * 操作成功
     */
    SUCCESS(200, "操作成功"),

    /**
     * 操作失败
     */
    FAILED(500, "操作失败"),

    /**
     * 参数验证失败
     */
    VALIDATE_FAILED(400, "参数验证失败"),

    /**
     * 未登录
     */
    UNAUTHORIZED(401, "未登录或登录已过期"),

    /**
     * 未授权
     */
    FORBIDDEN(403, "没有相关权限"),

    /**
     * 资源不存在
     */
    NOT_FOUND(404, "资源不存在"),

    /**
     * 请求方法不支持
     */
    METHOD_NOT_ALLOWED(405, "请求方法不支持"),

    /**
     * 请求超时
     */
    REQUEST_TIMEOUT(408, "请求超时"),

    /**
     * 系统异常
     */
    INTERNAL_SERVER_ERROR(500, "系统异常，请稍后重试"),

    /**
     * 服务不可用
     */
    SERVICE_UNAVAILABLE(503, "服务不可用"),

    // ========== 业务状态码 1000-9999 ==========

    /**
     * 用户相关 1000-1999
     */
    USER_NOT_FOUND(1001, "用户不存在"),
    USER_ALREADY_EXISTS(1002, "用户已存在"),
    USER_PHONE_BINDED(1003, "手机号已绑定"),
    USER_PHONE_NOT_BINDED(1004, "手机号未绑定"),
    USER_BALANCE_NOT_ENOUGH(1005, "余额不足"),

    /**
     * 商户相关 2000-2999
     */
    MERCHANT_NOT_FOUND(2001, "商户不存在"),
    MERCHANT_ALREADY_EXISTS(2002, "商户已存在"),
    MERCHANT_NOT_AUDITED(2003, "商户未审核"),
    MERCHANT_AUDIT_FAILED(2004, "商户审核未通过"),
    MERCHANT_BANNED(2005, "商户已被封禁"),
    MERCHANT_PASSWORD_ERROR(2006, "密码错误"),

    /**
     * 商品相关 3000-3999
     */
    PRODUCT_NOT_FOUND(3001, "商品不存在"),
    PRODUCT_OFFLINE(3002, "商品已下架"),
    PRODUCT_SOLD_OUT(3003, "商品已售罄"),

    /**
     * 库存相关 4000-4999
     */
    STOCK_NOT_ENOUGH(4001, "库存不足"),
    STOCK_LOCK_FAILED(4002, "库存锁定失败"),
    STOCK_RELEASE_FAILED(4003, "库存释放失败"),

    /**
     * 订单相关 5000-5999
     */
    ORDER_NOT_FOUND(5001, "订单不存在"),
    ORDER_ALREADY_PAID(5002, "订单已支付"),
    ORDER_ALREADY_CANCELED(5003, "订单已取消"),
    ORDER_ALREADY_COMPLETED(5004, "订单已完成"),
    ORDER_STATUS_ERROR(5005, "订单状态错误"),
    ORDER_EXPIRED(5006, "订单已过期"),
    ORDER_CANCEL_FAILED(5007, "订单取消失败"),

    /**
     * 支付相关 6000-6999
     */
    PAY_FAILED(6001, "支付失败"),
    PAY_TIMEOUT(6002, "支付超时"),
    PAY_AMOUNT_ERROR(6003, "支付金额错误"),
    PAY_METHOD_NOT_SUPPORT(6004, "不支持的支付方式"),

    /**
     * 核销相关 7000-7999
     */
    VERIFICATION_CODE_ERROR(7001, "提货码错误"),
    VERIFICATION_CODE_USED(7002, "提货码已使用"),
    VERIFICATION_TIME_ERROR(7003, "不在取餐时间范围内"),
    VERIFICATION_ORDER_ERROR(7004, "订单状态异常，无法核销"),

    /**
     * 文件相关 8000-8999
     */
    FILE_UPLOAD_FAILED(8001, "文件上传失败"),
    FILE_TYPE_NOT_SUPPORT(8002, "文件类型不支持"),
    FILE_SIZE_EXCEED(8003, "文件大小超过限制");

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 消息
     */
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
