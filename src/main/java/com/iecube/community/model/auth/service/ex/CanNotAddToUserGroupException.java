package com.iecube.community.model.auth.service.ex;

import com.iecube.community.baseservice.ex.ServiceException;

public class CanNotAddToUserGroupException extends ServiceException {
    public CanNotAddToUserGroupException() {
        super();
    }

    public CanNotAddToUserGroupException(String message) {
        super(message);
    }

    public CanNotAddToUserGroupException(String message, Throwable cause) {
        super(message, cause);
    }

    public CanNotAddToUserGroupException(Throwable cause) {
        super(cause);
    }

    protected CanNotAddToUserGroupException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
