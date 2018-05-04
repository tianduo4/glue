package com.glue.exception;

import lombok.Data;

/**
 * @author xushipeng
 * @create 2018-05-04 17:20
 */
public class GlueException extends RuntimeException {
    private int code = -1;
    private String message;

    public GlueException(String message) {
        super(message);
        this.message = message;
    }

    public GlueException(int code, String message) {
        super(code + ":" + message);
        this.code = code;
        this.message = message;
    }
}
