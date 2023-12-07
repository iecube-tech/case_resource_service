package com.iecube.community.model.duplicate_checking.service.ex;

import com.iecube.community.baseservice.ex.ServiceException;

public class NoRepetitiveRateVoException extends ServiceException {
    public NoRepetitiveRateVoException() {
        super();
    }

    public NoRepetitiveRateVoException(String message) {
        super(message);
    }

    public NoRepetitiveRateVoException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoRepetitiveRateVoException(Throwable cause) {
        super(cause);
    }

    protected NoRepetitiveRateVoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
