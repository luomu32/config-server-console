package xyz.luomu32.config.server.console.web.exception;

import lombok.Getter;

public enum ServiceExceptionEnum {

    CONFIG_SERVER_NOT_FOUND("1002", "config.server.not.found"),
    CONFIG_EXISTED("1003", "config.existed"),
    CONFIG_NOT_EXISTED("1004", "config.not.existed"),


    USER_PASSWORD_NOT_MATCH("user.password.not.match"),
    USER_NOT_FOUND("user.not.found"),
    USER_EXISTED("user.existed"),

    ROLE_NOT_FOUND("role.not.found"),

    MENU_NOT_FOUND("menu.not.found"),
    MENU_ACTION_NOT_FOUND("menu.action.not.found");

    @Getter
    private String code;

    @Getter
    private String message;

    ServiceExceptionEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    ServiceExceptionEnum(String message) {
        this.message = message;
    }

}
