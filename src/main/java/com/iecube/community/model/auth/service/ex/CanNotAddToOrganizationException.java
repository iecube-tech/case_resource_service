package com.iecube.community.model.auth.service.ex;

import com.iecube.community.baseservice.ex.ServiceException;

public class CanNotAddToOrganizationException extends ServiceException {
    public CanNotAddToOrganizationException() {
        super();
    }

    public CanNotAddToOrganizationException(String message) {
        super(message);
    }

    public CanNotAddToOrganizationException(String message, Throwable cause) {
        super(message, cause);
    }

    public CanNotAddToOrganizationException(Throwable cause) {
        super(cause);
    }

    protected CanNotAddToOrganizationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
