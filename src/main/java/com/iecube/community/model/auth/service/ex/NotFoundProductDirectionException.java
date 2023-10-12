package com.iecube.community.model.auth.service.ex;

import com.iecube.community.baseservice.ex.ServiceException;

public class NotFoundProductDirectionException extends ServiceException {
    public NotFoundProductDirectionException() {
        super();
    }

    public NotFoundProductDirectionException(String message) {
        super(message);
    }

    public NotFoundProductDirectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundProductDirectionException(Throwable cause) {
        super(cause);
    }

    protected NotFoundProductDirectionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
