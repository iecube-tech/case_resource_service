package com.iecube.community.model.task_student_group.service.ex;

import com.iecube.community.baseservice.ex.ServiceException;

public class GroupLimitException extends ServiceException {
    public GroupLimitException() {
        super();
    }

    public GroupLimitException(String message) {
        super(message);
    }

    public GroupLimitException(String message, Throwable cause) {
        super(message, cause);
    }

    public GroupLimitException(Throwable cause) {
        super(cause);
    }

    protected GroupLimitException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
