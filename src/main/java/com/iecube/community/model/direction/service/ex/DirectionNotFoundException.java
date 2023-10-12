package com.iecube.community.model.direction.service.ex;

import com.iecube.community.baseservice.ex.ServiceException;

public class DirectionNotFoundException extends ServiceException {
    public DirectionNotFoundException() {
        super();
    }

    public DirectionNotFoundException(String message) {
        super(message);
    }

    public DirectionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public DirectionNotFoundException(Throwable cause) {
        super(cause);
    }

    protected DirectionNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
