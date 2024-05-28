package com.iecube.community.model.tcpClient.ex;

import com.iecube.community.baseservice.ex.ServiceException;

public class CannotConnectException extends ServiceException {
    public CannotConnectException() {
        super();
    }

    public CannotConnectException(String message) {
        super(message);
    }

    public CannotConnectException(String message, Throwable cause) {
        super(message, cause);
    }

    public CannotConnectException(Throwable cause) {
        super(cause);
    }

    protected CannotConnectException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
