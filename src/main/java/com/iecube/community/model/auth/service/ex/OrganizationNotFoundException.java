package com.iecube.community.model.auth.service.ex;

import com.iecube.community.baseservice.ex.ServiceException;

public class OrganizationNotFoundException extends ServiceException {
    public OrganizationNotFoundException() {
        super();
    }

    public OrganizationNotFoundException(String message) {
        super(message);
    }

    public OrganizationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public OrganizationNotFoundException(Throwable cause) {
        super(cause);
    }

    protected OrganizationNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
