package xyz.luomu32.config.server.console.web.request;

import lombok.Data;
import xyz.luomu32.config.server.console.entity.ConfigServerType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ServerUpdate {

    @NotNull(message = "server.update.id.not.null")
    private Long id;

    @NotBlank(message = "server.update.name.not.null")
    private String name;

    private String prefix;

    @NotBlank
    private String url;

    private ConfigServerType type;
}
