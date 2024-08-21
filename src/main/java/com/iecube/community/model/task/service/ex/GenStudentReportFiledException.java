package com.iecube.community.model.task.service.ex;

import com.iecube.community.baseservice.ex.ServiceException;

public class GenStudentReportFiledException extends ServiceException {
    public GenStudentReportFiledException() {
        super();
    }

    public GenStudentReportFiledException(String message) {
        super(message);
    }

    public GenStudentReportFiledException(String message, Throwable cause) {
        super(message, cause);
    }

    public GenStudentReportFiledException(Throwable cause) {
        super(cause);
    }

    protected GenStudentReportFiledException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
