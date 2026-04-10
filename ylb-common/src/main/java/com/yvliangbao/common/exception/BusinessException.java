package com.yvliangbao.common.exception;

import com.yuliangbao.common.result.ResultCode;
import lombok.Getter;

/**
 * 业务异常
 *
 * @author 余量宝
 * @date 2026-02-28
 */
@Getter
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 消息
     */
    private final String message;

    public BusinessException(String message) {
        super(message);
        this.code = ResultCode.FAILED.getCode();
        this.message = message;
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }

    public BusinessException(ResultCode resultCode, String message) {
        super(message);
        this.code = resultCode.getCode();
        this.message = message;
    }

    /**
     * 根据错误码和消息创建异常
     */
    public BusinessException(String code, String message) {
        super(message);
        this.code = Integer.parseInt(code);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
