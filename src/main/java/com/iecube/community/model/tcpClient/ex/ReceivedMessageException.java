package com.iecube.community.model.tcpClient.ex;

import com.iecube.community.baseservice.ex.ServiceException;

public class ReceivedMessageException extends ServiceException {
    public ReceivedMessageException() {
        super();
    }

    public ReceivedMessageException(String message) {
        super(message);
    }

    public ReceivedMessageException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReceivedMessageException(Throwable cause) {
        super(cause);
    }

    protected ReceivedMessageException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
