package com.iecube.community.model.auth.service.ex;

import com.iecube.community.baseservice.ex.ServiceException;

public class UnLoginException extends ServiceException {
    public UnLoginException() {
        super();
    }

    public UnLoginException(String message) {
        super(message);
    }

    public UnLoginException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnLoginException(Throwable cause) {
        super(cause);
    }

    protected UnLoginException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
