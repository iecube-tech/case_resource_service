package com.iecube.community.model.student.service.ex;

import com.iecube.community.baseservice.ex.ServiceException;

public class StudentKeyFieldNotExistException extends ServiceException {
    public StudentKeyFieldNotExistException() {
        super();
    }

    public StudentKeyFieldNotExistException(String message) {
        super(message);
    }

    public StudentKeyFieldNotExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public StudentKeyFieldNotExistException(Throwable cause) {
        super(cause);
    }

    protected StudentKeyFieldNotExistException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
