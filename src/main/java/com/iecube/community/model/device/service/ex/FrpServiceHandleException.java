package com.iecube.community.model.device.service.ex;

import com.iecube.community.baseservice.ex.ServiceException;

public class FrpServiceHandleException extends ServiceException {
    public FrpServiceHandleException() {
        super();
    }

    public FrpServiceHandleException(String message) {
        super(message);
    }

    public FrpServiceHandleException(String message, Throwable cause) {
        super(message, cause);
    }

    public FrpServiceHandleException(Throwable cause) {
        super(cause);
    }

    protected FrpServiceHandleException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
