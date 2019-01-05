package xyz.luomu32.config.server.console.web.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class RolePojo {
    @NotBlank(message = "{role.name.not.blank}")
    private String name;
}
