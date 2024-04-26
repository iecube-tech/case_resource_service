package com.iecube.community.model.project_student_group.service.ex;

import com.iecube.community.baseservice.ex.ServiceException;

public class GroupCodeException extends ServiceException {
    public GroupCodeException() {
        super();
    }

    public GroupCodeException(String message) {
        super(message);
    }

    public GroupCodeException(String message, Throwable cause) {
        super(message, cause);
    }

    public GroupCodeException(Throwable cause) {
        super(cause);
    }

    protected GroupCodeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
