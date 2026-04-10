package com.yvliangbao.common.exception;


import com.yvliangbao.common.result.ResultCode;
import lombok.Getter;

/**
 * 认证异常
 *
 * @author 余量宝
 */
@Getter
public class AuthException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final Integer code;
    private final String message;

    public AuthException(String message) {
        super(message);
        this.code = ResultCode.UNAUTHORIZED.getCode();
        this.message = message;
    }

    public AuthException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }

    @Override
    public String getMessage() {
        return message;
    }
}
