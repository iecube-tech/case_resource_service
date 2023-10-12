package com.iecube.community.model.auth.service.ex;

import com.iecube.community.baseservice.ex.ServiceException;

public class EmailDuplicateException extends ServiceException {
    public EmailDuplicateException() {
        super();
    }

    public EmailDuplicateException(String message) {
        super(message);
    }

    public EmailDuplicateException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmailDuplicateException(Throwable cause) {
        super(cause);
    }

    protected EmailDuplicateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
