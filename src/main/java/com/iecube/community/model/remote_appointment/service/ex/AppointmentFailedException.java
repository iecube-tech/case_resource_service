package com.iecube.community.model.remote_appointment.service.ex;

import com.iecube.community.baseservice.ex.ServiceException;

public class AppointmentFailedException extends ServiceException {
    public AppointmentFailedException() {
        super();
    }

    public AppointmentFailedException(String message) {
        super(message);
    }

    public AppointmentFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public AppointmentFailedException(Throwable cause) {
        super(cause);
    }

    protected AppointmentFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
