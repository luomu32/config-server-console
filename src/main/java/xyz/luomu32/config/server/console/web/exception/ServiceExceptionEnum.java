package xyz.luomu32.config.server.console.web.exception;

import lombok.Getter;

public enum ServiceExceptionEnum {

    CONFIG_SERVER_NOT_FOUND(1002, "config.server.not.found");


    @Getter
    private int code;

    @Getter
    private String message;
    
    ServiceExceptionEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
