package xyz.luomu32.config.server.console.web.request;

import lombok.Data;

import java.util.Set;

@Data
public class Application {
    private String name;
    private Set<String> profiles;
}
