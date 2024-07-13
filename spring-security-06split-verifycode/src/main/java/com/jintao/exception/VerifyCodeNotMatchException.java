package com.jintao.exception;

import org.springframework.security.core.AuthenticationException;

public class VerifyCodeNotMatchException extends AuthenticationException {
    public VerifyCodeNotMatchException(String detail) {
        super(detail);
    }

    public VerifyCodeNotMatchException(String detail, Throwable ex) {
        super(detail, ex);
    }
}
