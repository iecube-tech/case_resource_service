package com.iecube.community.model.classification.service.ex;

import com.iecube.community.baseservice.ex.ServiceException;

public class ClassificationDuplicateException extends ServiceException {
    public ClassificationDuplicateException() {
        super();
    }

    public ClassificationDuplicateException(String message) {
        super(message);
    }

    public ClassificationDuplicateException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClassificationDuplicateException(Throwable cause) {
        super(cause);
    }

    protected ClassificationDuplicateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
