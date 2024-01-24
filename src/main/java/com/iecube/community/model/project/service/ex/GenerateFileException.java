package com.iecube.community.model.project.service.ex;

import com.iecube.community.baseservice.ex.ServiceException;

public class GenerateFileException extends ServiceException {
    public GenerateFileException() {
        super();
    }

    public GenerateFileException(String message) {
        super(message);
    }

    public GenerateFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public GenerateFileException(Throwable cause) {
        super(cause);
    }

    protected GenerateFileException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
