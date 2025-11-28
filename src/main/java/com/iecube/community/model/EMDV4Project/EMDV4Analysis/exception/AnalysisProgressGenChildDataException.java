package com.iecube.community.model.EMDV4Project.EMDV4Analysis.exception;

import com.iecube.community.baseservice.ex.ServiceException;

public class AnalysisProgressGenChildDataException extends ServiceException {
    public AnalysisProgressGenChildDataException(String message) {
        super(message);
    }

    public AnalysisProgressGenChildDataException() {
        super();
    }

    public AnalysisProgressGenChildDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public AnalysisProgressGenChildDataException(Throwable cause) {
        super(cause);
    }

    protected AnalysisProgressGenChildDataException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
