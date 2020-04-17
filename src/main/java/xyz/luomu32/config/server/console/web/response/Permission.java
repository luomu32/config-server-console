package xyz.luomu32.config.server.console.web.response;

import lombok.Data;
import xyz.luomu32.config.server.console.entity.Menu;
import xyz.luomu32.config.server.console.entity.MenuAction;

import java.io.Serializable;
import java.util.List;

@Data
public class Permission implements Serializable {

    private List<Menu> menus;

    private List<MenuAction> actions;

}
