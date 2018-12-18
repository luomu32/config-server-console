package xyz.luomu32.config.server.console.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserPrincipal implements Serializable {

    private Long id;

    private String username;

    private Long roleId;

    private String roleName;
}
