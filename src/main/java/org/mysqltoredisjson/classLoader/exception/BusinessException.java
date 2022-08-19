package org.mysqltoredisjson.classLoader.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

/**
 * 业务异常
 */
@Data
public class BusinessException extends RuntimeException {
    private int code;

    private String msg;

    public BusinessException(String msg) {
        super(msg);
        this.code = 1;
        this.msg = msg;
    }

    public BusinessException(int code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public BusinessException(String msg, Throwable cause) {
        super(msg, cause);
        this.code = 1;
        this.msg = msg;
    }

    public BusinessException(int code, String msg, Throwable cause) {
        super(msg, cause);
        this.code = code;
        this.msg = msg;
    }

    public BusinessException(HttpStatus errorEnum) {
        super(errorEnum.getReasonPhrase());
        this.code = errorEnum.value();
        this.msg = errorEnum.getReasonPhrase();
    }

    public BusinessException(HttpStatus errorEnum, Throwable cause) {
        super(errorEnum.getReasonPhrase(), cause);
        this.code = errorEnum.value();
        this.msg = errorEnum.getReasonPhrase();
    }

}
