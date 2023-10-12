package com.iecube.community.model.direction.service.ex;

import com.iecube.community.baseservice.ex.ServiceException;

public class DirectionDuplicateException extends ServiceException {
    public DirectionDuplicateException() {
        super();
    }

    public DirectionDuplicateException(String message) {
        super(message);
    }

    public DirectionDuplicateException(String message, Throwable cause) {
        super(message, cause);
    }

    public DirectionDuplicateException(Throwable cause) {
        super(cause);
    }

    protected DirectionDuplicateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
