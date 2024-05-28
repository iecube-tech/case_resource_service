package com.iecube.community.model.device.service.ex;

import com.iecube.community.baseservice.ex.ServiceException;

public class SameStateException extends ServiceException {
    public SameStateException() {
        super();
    }

    public SameStateException(String message) {
        super(message);
    }

    public SameStateException(String message, Throwable cause) {
        super(message, cause);
    }

    public SameStateException(Throwable cause) {
        super(cause);
    }

    protected SameStateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
