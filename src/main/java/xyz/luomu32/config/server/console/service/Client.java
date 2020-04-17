package xyz.luomu32.config.server.console.service;

import xyz.luomu32.config.server.console.entity.ConfigServer;
import xyz.luomu32.config.server.console.entity.ConfigServerType;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface Client {

    ConfigServerType getType();

    boolean add(ConfigServer server, String application, String profile, String key, String value);

    void update(ConfigServer server, String application, Optional<String> profile, String key, String newValue);

    void delete(ConfigServer server, String application, String profile, String key);

    String findValue(ConfigServer server, String application, String profile, String key);

    Map<String, String> findAll(ConfigServer server, String application, String profile);

    boolean hasChildren(ConfigServer server);

    List<String> getApplicationsWithProfile(ConfigServer server);

    List<String> getProfiles(ConfigServer server, String application);

    void addApplication(ConfigServer server, String application, String profile);

    void deleteApplication(ConfigServer server, String application, String profile);

    void addProfile(ConfigServer server, String application, String profile);
}
