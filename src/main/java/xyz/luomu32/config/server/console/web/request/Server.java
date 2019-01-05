package xyz.luomu32.config.server.console.web.request;

import lombok.Data;
import xyz.luomu32.config.server.console.entity.ConfigServerType;

@Data
public class Server {

    private Long id;
    
    private String name;

    private String prefix;

    private String url;

    private ConfigServerType type;
}
