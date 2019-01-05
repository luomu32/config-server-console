package xyz.luomu32.config.server.console.web.response;

import lombok.Data;

@Data
public class ApiErrorResponse {

    private String code;

    private String message;

    public ApiErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public ApiErrorResponse(String message) {
        this.message = message;
    }
}
