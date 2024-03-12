package com.iecube.community.model.video.service.ex;

import com.iecube.community.baseservice.ex.ServiceException;

public class FFmpegUseException extends ServiceException {
    public FFmpegUseException() {
        super();
    }

    public FFmpegUseException(String message) {
        super(message);
    }

    public FFmpegUseException(String message, Throwable cause) {
        super(message, cause);
    }

    public FFmpegUseException(Throwable cause) {
        super(cause);
    }

    protected FFmpegUseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
