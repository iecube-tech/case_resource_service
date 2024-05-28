package com.iecube.community.model.device.service.ex;

import com.iecube.community.baseservice.ex.ServiceException;

public class IpConflictException extends ServiceException {
    public IpConflictException() {
        super();
    }

    public IpConflictException(String message) {
        super(message);
    }

    public IpConflictException(String message, Throwable cause) {
        super(message, cause);
    }

    public IpConflictException(Throwable cause) {
        super(cause);
    }

    protected IpConflictException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
