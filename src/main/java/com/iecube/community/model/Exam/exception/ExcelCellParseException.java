package com.iecube.community.model.Exam.exception;

import com.iecube.community.baseservice.ex.ServiceException;

public class ExcelCellParseException extends ServiceException {
    public ExcelCellParseException() {
        super();
    }

    public ExcelCellParseException(String message) {
        super(message);
    }

    public ExcelCellParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExcelCellParseException(Throwable cause) {
        super(cause);
    }

    protected ExcelCellParseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
