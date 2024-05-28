package com.iecube.community.model.device.service.ex;

import com.iecube.community.baseservice.ex.ServiceException;

public class OnlineBoxHasBeenUsedException extends ServiceException {
    public OnlineBoxHasBeenUsedException() {
        super();
    }

    public OnlineBoxHasBeenUsedException(String message) {
        super(message);
    }

    public OnlineBoxHasBeenUsedException(String message, Throwable cause) {
        super(message, cause);
    }

    public OnlineBoxHasBeenUsedException(Throwable cause) {
        super(cause);
    }

    protected OnlineBoxHasBeenUsedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
