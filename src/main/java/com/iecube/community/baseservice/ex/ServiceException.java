package com.iecube.community.baseservice.ex;

/**业务层异常 继承运行时异常**/
public class ServiceException extends RuntimeException{
    public ServiceException() {
        super();
    }

    /**异常信息**/
    public ServiceException(String message) {
        super(message);
    }


    /**异常信息  异常对象**/
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceException(Throwable cause) {
        super(cause);
    }

    protected ServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
