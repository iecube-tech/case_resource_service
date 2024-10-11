package com.iecube.community.model.task.service.ex;

import com.iecube.community.baseservice.ex.ServiceException;

public class SQLBatchProcessingException extends ServiceException {
    public SQLBatchProcessingException() {
        super();
    }

    public SQLBatchProcessingException(String message) {
        super(message);
    }

    public SQLBatchProcessingException(String message, Throwable cause) {
        super(message, cause);
    }

    public SQLBatchProcessingException(Throwable cause) {
        super(cause);
    }

    protected SQLBatchProcessingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
