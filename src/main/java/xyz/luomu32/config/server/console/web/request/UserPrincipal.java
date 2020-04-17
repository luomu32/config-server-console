package xyz.luomu32.config.server.console.web.request;

import lombok.Data;
import xyz.luomu32.config.server.console.web.response.Permission;

import java.io.Serializable;

@Data
public class UserPrincipal implements Serializable {

    private Long id;

    private String username;

    private Long roleId;

    private String roleName;

    private Permission permission;
}
