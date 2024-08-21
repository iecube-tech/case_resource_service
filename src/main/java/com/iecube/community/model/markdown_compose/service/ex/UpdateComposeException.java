package com.iecube.community.model.markdown_compose.service.ex;

import com.iecube.community.baseservice.ex.ServiceException;

public class UpdateComposeException extends ServiceException {
    public UpdateComposeException() {
        super();
    }

    public UpdateComposeException(String message) {
        super(message);
    }

    public UpdateComposeException(String message, Throwable cause) {
        super(message, cause);
    }

    public UpdateComposeException(Throwable cause) {
        super(cause);
    }

    protected UpdateComposeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
