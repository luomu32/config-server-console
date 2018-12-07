package xyz.luomu32.config.server.console.pojo;

import lombok.Data;
import xyz.luomu32.config.server.console.entity.Log;

@Data
public class LogRquest extends Log {
    private String operationName;

    private String application;
}
