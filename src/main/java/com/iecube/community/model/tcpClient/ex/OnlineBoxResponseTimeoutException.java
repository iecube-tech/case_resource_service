package com.iecube.community.model.tcpClient.ex;

import com.iecube.community.baseservice.ex.ServiceException;

public class OnlineBoxResponseTimeoutException extends ServiceException {
    public OnlineBoxResponseTimeoutException() {
        super();
    }

    public OnlineBoxResponseTimeoutException(String message) {
        super(message);
    }

    public OnlineBoxResponseTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

    public OnlineBoxResponseTimeoutException(Throwable cause) {
        super(cause);
    }

    protected OnlineBoxResponseTimeoutException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
