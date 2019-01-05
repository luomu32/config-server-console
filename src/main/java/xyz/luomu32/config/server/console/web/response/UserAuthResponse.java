package xyz.luomu32.config.server.console.web.response;

import lombok.Data;
import xyz.luomu32.config.server.console.entity.Menu;
import xyz.luomu32.config.server.console.entity.MenuAction;
import xyz.luomu32.config.server.console.entity.Role;

import java.util.List;
import java.util.Map;

@Data
public class UserAuthResponse {

    private Long id;

    private String username;

    private Long expireAt;

    private Role role;

    private List<Menu> menus;

    private Map<Long, List<MenuAction>> actions;
}
