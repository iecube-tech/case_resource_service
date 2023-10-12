package com.iecube.community.model.student.service.ex;

import com.iecube.community.baseservice.ex.ServiceException;

public class StudentDuplicateException extends ServiceException {
    public StudentDuplicateException() {
        super();
    }

    public StudentDuplicateException(String message) {
        super(message);
    }

    public StudentDuplicateException(String message, Throwable cause) {
        super(message, cause);
    }

    public StudentDuplicateException(Throwable cause) {
        super(cause);
    }

    protected StudentDuplicateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
