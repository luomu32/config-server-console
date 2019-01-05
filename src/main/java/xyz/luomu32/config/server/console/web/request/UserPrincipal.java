package xyz.luomu32.config.server.console.web.request;

import lombok.Data;
import xyz.luomu32.config.server.console.entity.Menu;
import xyz.luomu32.config.server.console.entity.MenuAction;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class UserPrincipal implements Serializable {

    private Long id;

    private String username;

    private Long roleId;

    private String roleName;

    private List<Menu> menus;

    private List<MenuAction> actions;
}
