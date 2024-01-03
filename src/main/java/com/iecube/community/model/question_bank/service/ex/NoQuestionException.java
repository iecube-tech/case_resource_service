package com.iecube.community.model.question_bank.service.ex;

import com.iecube.community.baseservice.ex.ServiceException;

public class NoQuestionException extends ServiceException {
    public NoQuestionException() {
        super();
    }

    public NoQuestionException(String message) {
        super(message);
    }

    public NoQuestionException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoQuestionException(Throwable cause) {
        super(cause);
    }

    protected NoQuestionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
