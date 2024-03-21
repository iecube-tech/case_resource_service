package com.iecube.community.model.iecube3835.service.ex;

import com.iecube.community.baseservice.ex.ServiceException;

public class GenerateStudentReportException extends ServiceException {
    public GenerateStudentReportException() {
        super();
    }

    public GenerateStudentReportException(String message) {
        super(message);
    }

    public GenerateStudentReportException(String message, Throwable cause) {
        super(message, cause);
    }

    public GenerateStudentReportException(Throwable cause) {
        super(cause);
    }

    protected GenerateStudentReportException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
