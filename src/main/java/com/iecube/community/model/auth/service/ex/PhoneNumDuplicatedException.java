package com.iecube.community.model.auth.service.ex;

import com.iecube.community.baseservice.ex.ServiceException;

/**
 * 手机号已存在异常
 */
public class PhoneNumDuplicatedException extends ServiceException {
    public PhoneNumDuplicatedException() {
        super();
    }

    public PhoneNumDuplicatedException(String message) {
        super(message);
    }

    public PhoneNumDuplicatedException(String message, Throwable cause) {
        super(message, cause);
    }

    public PhoneNumDuplicatedException(Throwable cause) {
        super(cause);
    }

    protected PhoneNumDuplicatedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
