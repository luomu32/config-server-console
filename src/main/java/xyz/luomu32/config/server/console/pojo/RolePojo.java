package xyz.luomu32.config.server.console.pojo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class RolePojo {

    private Long id;

    @NotBlank(message = "{role.name.not.blank}")
    private String name;
}
