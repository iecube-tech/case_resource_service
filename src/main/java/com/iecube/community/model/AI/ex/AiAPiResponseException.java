package com.iecube.community.model.AI.ex;

import com.iecube.community.baseservice.ex.ServiceException;

public class AiAPiResponseException extends ServiceException {
    public AiAPiResponseException(String message) {
        super(message);
    }

    public AiAPiResponseException() {
        super();
    }

    public AiAPiResponseException(String message, Throwable cause) {
        super(message, cause);
    }

    public AiAPiResponseException(Throwable cause) {
        super(cause);
    }

    protected AiAPiResponseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
