package com.iecube.community.model.analysis.service.ex;

import com.iecube.community.baseservice.ex.ServiceException;

public class NoneOfTheProjectsUnderTheCaseHaveBeenCompleted extends ServiceException {
    public NoneOfTheProjectsUnderTheCaseHaveBeenCompleted() {
        super();
    }

    public NoneOfTheProjectsUnderTheCaseHaveBeenCompleted(String message) {
        super(message);
    }

    public NoneOfTheProjectsUnderTheCaseHaveBeenCompleted(String message, Throwable cause) {
        super(message, cause);
    }

    public NoneOfTheProjectsUnderTheCaseHaveBeenCompleted(Throwable cause) {
        super(cause);
    }

    protected NoneOfTheProjectsUnderTheCaseHaveBeenCompleted(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
