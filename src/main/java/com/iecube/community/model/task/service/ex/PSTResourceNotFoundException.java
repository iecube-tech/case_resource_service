package com.iecube.community.model.task.service.ex;

import com.iecube.community.baseservice.ex.ServiceException;

public class PSTResourceNotFoundException extends ServiceException {
    public PSTResourceNotFoundException() {
        super();
    }

    public PSTResourceNotFoundException(String message) {
        super(message);
    }

    public PSTResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public PSTResourceNotFoundException(Throwable cause) {
        super(cause);
    }

    protected PSTResourceNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
