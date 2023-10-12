package com.iecube.community.model.resource.service.ex;

import com.iecube.community.baseservice.ex.ServiceException;

public class FileDeletedFailedException extends ServiceException {
    public FileDeletedFailedException() {
        super();
    }

    public FileDeletedFailedException(String message) {
        super(message);
    }

    public FileDeletedFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileDeletedFailedException(Throwable cause) {
        super(cause);
    }

    protected FileDeletedFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
