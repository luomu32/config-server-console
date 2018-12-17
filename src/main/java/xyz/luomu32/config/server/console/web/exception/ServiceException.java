package xyz.luomu32.config.server.console.web.exception;

import lombok.Getter;

public class ServiceException extends RuntimeException {

    @Getter
    private int code;

    public ServiceException(ServiceExceptionEnum exception) {
        super(exception.getMessage());
        this.code = exception.getCode();
    }

}
