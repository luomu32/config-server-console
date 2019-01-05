package xyz.luomu32.config.server.console.web.request;

import lombok.Data;

import java.util.List;

@Data
public class MenusAndActions {

    List<Long> menus;

    List<Long> actions;
}
