package xyz.luomu32.config.server.console.web.request;

import lombok.Data;
import xyz.luomu32.config.server.console.entity.MenuAction;

import java.util.List;

@Data
public class MenuWithAction {

    private Long menuId;

    private String menuName;

    private List<MenuAction> actions;

}
