package xyz.luomu32.config.server.console.web.exception;

import lombok.Getter;

public class ServiceException extends RuntimeException {

    @Getter
    private String code;
    @Getter
    private Object[] params;

    @Getter
    private Throwable targetException;

    public ServiceException(ServiceExceptionEnum exception) {
        super(exception.getMessage());
        this.code = exception.getCode();
    }

    public ServiceException(ServiceExceptionEnum exception, Throwable targetException) {
        super(exception.getMessage());
        this.code = exception.getCode();
        this.targetException = targetException;
    }

    public ServiceException(ServiceExceptionEnum exception, Object[] params) {
        super(exception.getMessage());
        this.code = exception.getCode();
        this.params = params;
    }


}
