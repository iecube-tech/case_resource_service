package com.iecube.community.model.remote_project.service.ex;

import com.iecube.community.baseservice.ex.ServiceException;

public class DeviceHasBeenUsedException extends ServiceException {
    public DeviceHasBeenUsedException() {
        super();
    }

    public DeviceHasBeenUsedException(String message) {
        super(message);
    }

    public DeviceHasBeenUsedException(String message, Throwable cause) {
        super(message, cause);
    }

    public DeviceHasBeenUsedException(Throwable cause) {
        super(cause);
    }

    protected DeviceHasBeenUsedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
