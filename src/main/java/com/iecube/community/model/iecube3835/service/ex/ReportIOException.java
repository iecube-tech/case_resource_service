package com.iecube.community.model.iecube3835.service.ex;

import com.iecube.community.baseservice.ex.ServiceException;

public class ReportIOException extends ServiceException {
    public ReportIOException() {
        super();
    }

    public ReportIOException(String message) {
        super(message);
    }

    public ReportIOException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReportIOException(Throwable cause) {
        super(cause);
    }

    protected ReportIOException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
