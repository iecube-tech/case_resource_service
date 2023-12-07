package com.iecube.community.model.duplicate_checking.service.ex;

import com.iecube.community.baseservice.ex.ServiceException;

public class NoPDFFilesException extends ServiceException {
    public NoPDFFilesException() {
        super();
    }

    public NoPDFFilesException(String message) {
        super(message);
    }

    public NoPDFFilesException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoPDFFilesException(Throwable cause) {
        super(cause);
    }

    protected NoPDFFilesException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
