package com.iecube.community.model.auth.service.ex;

import com.iecube.community.baseservice.ex.ServiceException;

public class AuthCodeErrorException extends ServiceException {
    public AuthCodeErrorException() {
        super();
    }

    public AuthCodeErrorException(String message) {
        super(message);
    }

    public AuthCodeErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthCodeErrorException(Throwable cause) {
        super(cause);
    }

    protected AuthCodeErrorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
