package com.iecube.community.model.classification.service.ex;

import com.iecube.community.baseservice.ex.ServiceException;

public class ClassificationNotFoundException extends ServiceException {
    public ClassificationNotFoundException() {
        super();
    }

    public ClassificationNotFoundException(String message) {
        super(message);
    }

    public ClassificationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClassificationNotFoundException(Throwable cause) {
        super(cause);
    }

    protected ClassificationNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
