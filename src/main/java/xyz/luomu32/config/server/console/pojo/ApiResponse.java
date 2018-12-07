package xyz.luomu32.config.server.console.pojo;

import lombok.Data;

@Data
public class ApiResponse {

    private String code;

    private String message;

    public ApiResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public ApiResponse(String message) {
        this.message = message;
    }
}
