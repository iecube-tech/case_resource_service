package com.iecube.community.model.project.service.ex;

import com.iecube.community.baseservice.ex.ServiceException;

public class StudentAlreadyInProject extends ServiceException {
    public StudentAlreadyInProject() {
        super();
    }

    public StudentAlreadyInProject(String message) {
        super(message);
    }

    public StudentAlreadyInProject(String message, Throwable cause) {
        super(message, cause);
    }

    public StudentAlreadyInProject(Throwable cause) {
        super(cause);
    }

    protected StudentAlreadyInProject(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
