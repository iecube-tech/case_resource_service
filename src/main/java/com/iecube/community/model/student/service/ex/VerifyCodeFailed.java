package com.iecube.community.model.student.service.ex;

import com.iecube.community.baseservice.ex.ServiceException;

public class VerifyCodeFailed extends ServiceException {
    public VerifyCodeFailed(String message) {
        super(message);
    }

    public VerifyCodeFailed() {
        super();
    }

    public VerifyCodeFailed(String message, Throwable cause) {
        super(message, cause);
    }

    public VerifyCodeFailed(Throwable cause) {
        super(cause);
    }

    protected VerifyCodeFailed(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
