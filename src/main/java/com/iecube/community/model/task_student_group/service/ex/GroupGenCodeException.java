package com.iecube.community.model.task_student_group.service.ex;

import com.iecube.community.baseservice.ex.ServiceException;

public class GroupGenCodeException extends ServiceException {
    public GroupGenCodeException() {
        super();
    }

    public GroupGenCodeException(String message) {
        super(message);
    }

    public GroupGenCodeException(String message, Throwable cause) {
        super(message, cause);
    }

    public GroupGenCodeException(Throwable cause) {
        super(cause);
    }

    protected GroupGenCodeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
