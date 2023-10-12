package com.iecube.community.model.content.service.ex;

import com.iecube.community.baseservice.ex.ServiceException;

public class ContentDidNotDel extends ServiceException {
    public ContentDidNotDel() {
        super();
    }

    public ContentDidNotDel(String message) {
        super(message);
    }

    public ContentDidNotDel(String message, Throwable cause) {
        super(message, cause);
    }

    public ContentDidNotDel(Throwable cause) {
        super(cause);
    }

    protected ContentDidNotDel(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
