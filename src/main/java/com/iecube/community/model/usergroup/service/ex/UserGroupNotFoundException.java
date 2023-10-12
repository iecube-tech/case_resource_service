package com.iecube.community.model.usergroup.service.ex;

import com.iecube.community.baseservice.ex.ServiceException;

public class UserGroupNotFoundException extends ServiceException {
    public UserGroupNotFoundException() {
        super();
    }

    public UserGroupNotFoundException(String message) {
        super(message);
    }

    public UserGroupNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserGroupNotFoundException(Throwable cause) {
        super(cause);
    }

    protected UserGroupNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
