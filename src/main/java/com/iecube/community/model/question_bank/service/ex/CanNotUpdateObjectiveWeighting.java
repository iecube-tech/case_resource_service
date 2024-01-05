package com.iecube.community.model.question_bank.service.ex;

import com.iecube.community.baseservice.ex.ServiceException;

public class CanNotUpdateObjectiveWeighting extends ServiceException {
    public CanNotUpdateObjectiveWeighting() {
        super();
    }

    public CanNotUpdateObjectiveWeighting(String message) {
        super(message);
    }

    public CanNotUpdateObjectiveWeighting(String message, Throwable cause) {
        super(message, cause);
    }

    public CanNotUpdateObjectiveWeighting(Throwable cause) {
        super(cause);
    }

    protected CanNotUpdateObjectiveWeighting(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
