package xyz.luomu32.config.server.console.service;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import xyz.luomu32.config.server.console.entity.ConfigServer;
import xyz.luomu32.config.server.console.entity.ConfigServerType;

import java.util.*;
import java.util.stream.Stream;

@Service
public class ClientService implements Client, ApplicationContextAware {


    private final Map<ConfigServerType, Client> clientMaps = new HashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        String[] clientBeanNames = applicationContext.getBeanNamesForType(Client.class);
        Stream.of(clientBeanNames).forEach(n -> {
            Client client = (Client) applicationContext.getBean(n);
            if (null != client.getType())
                clientMaps.put(client.getType(), client);
        });
    }

    @Override
    public boolean hasChildren(ConfigServer server) {
        Client client = this.clientMaps.get(server.getType());
        if (client == null)
            return false;

        return client.hasChildren(server);
    }

    @Override
    public ConfigServerType getType() {
        return null;
    }

    @Override
    public void add(ConfigServer server, String application, String profile, String key, String value) {
        Optional.ofNullable(this.clientMaps.get(server.getType())).ifPresent(client -> client.add(server, application, profile, key, value));
    }

    @Override
    public void update(ConfigServer server, String application, Optional<String> profile, String key, String newValue) {
        Optional.ofNullable(this.clientMaps.get(server.getType())).ifPresent(client -> client.update(server, application, profile, key, newValue));
    }

    @Override
    public void delete(ConfigServer server, String application, String profile, String key) {
        Optional.ofNullable(this.clientMaps.get(server.getType())).ifPresent(client -> client.delete(server, application, profile, key));
    }

    @Override
    public String findValue(ConfigServer server, String application, String profile, String key) {
        Client client = this.clientMaps.get(server.getType());
        if (client == null)
            return null;
        return client.findValue(server, application, profile, key);
    }

    @Override
    public Map<String, String> findAll(ConfigServer server, String application, String profile) {
        Client client = this.clientMaps.get(server.getType());
        if (client == null)
            return Collections.emptyMap();
        return client.findAll(server, application, profile);
    }

    @Override
    public List<String> getApplicationsWithProfile(ConfigServer server) {
        Client client = this.clientMaps.get(server.getType());
        if (client == null)
            return Collections.emptyList();
        return client.getApplicationsWithProfile(server);
    }

    @Override
    public void addApplication(ConfigServer server, String application, String profile) {
        Optional.ofNullable(this.clientMaps.get(server.getType())).ifPresent(
                c -> {
                    c.addApplication(server, application, profile);
                }
        );
    }

    @Override
    public List<String> getProfiles(ConfigServer server, String application) {
        Client client = this.clientMaps.get(server.getType());
        if (client == null)
            return Collections.emptyList();
        return client.getProfiles(server, application);
    }
}
