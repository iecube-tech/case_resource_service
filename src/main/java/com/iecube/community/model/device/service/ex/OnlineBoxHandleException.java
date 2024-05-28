package com.iecube.community.model.device.service.ex;

import com.iecube.community.baseservice.ex.ServiceException;

public class OnlineBoxHandleException extends ServiceException {
    public OnlineBoxHandleException() {
        super();
    }

    public OnlineBoxHandleException(String message) {
        super(message);
    }

    public OnlineBoxHandleException(String message, Throwable cause) {
        super(message, cause);
    }

    public OnlineBoxHandleException(Throwable cause) {
        super(cause);
    }

    protected OnlineBoxHandleException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
