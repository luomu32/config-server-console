package xyz.luomu32.config.server.console.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorEventType;
import org.apache.curator.retry.RetryOneTime;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import xyz.luomu32.config.server.console.entity.ConfigServer;
import xyz.luomu32.config.server.console.entity.ConfigServerType;
import xyz.luomu32.config.server.console.service.Client;
import xyz.luomu32.config.server.console.web.exception.ServiceException;
import xyz.luomu32.config.server.console.web.exception.ServiceExceptionEnum;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class ZooKeeperClient implements Client {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZooKeeperClient.class);

//    private CuratorFramework client;

    private ConcurrentHashMap<Long, CuratorFramework> clientMaps = new ConcurrentHashMap<>();
//    private ConcurrentHashMap<CuratorFramework, Map<String, String>> configs = new ConcurrentHashMap<>();

    private CuratorFramework getClient(ConfigServer configServer) {

        return clientMaps.computeIfAbsent(configServer.getId(), k -> {
                    CuratorFramework client = CuratorFrameworkFactory
                            .builder()
                            .retryPolicy(new RetryOneTime(1000))
                            .connectString(configServer.getUrl())
                            .namespace(configServer.getPrefix())
                            .build();

                    client.start();
                    client.getCuratorListenable().addListener((client1, event) -> {
//                        Map<String, String> config = configs.getOrDefault(client1, load(client1));
                        if (event.getType() == CuratorEventType.SET_DATA) {
                            LOGGER.info("编辑:{},{},{}", event.getName(), event.getPath(), new String(event.getData()));
                        } else if (event.getType() == CuratorEventType.DELETE) {
                            LOGGER.info("删除:{},{},{}", event.getName(), event.getPath(), new String(event.getData()));
                        } else if (event.getType() == CuratorEventType.CREATE) {
                            LOGGER.info("新增:{},{},{}", event.getName(), event.getPath(), new String(event.getData()));
                        }
                    });
                    return client;
                }
        );
    }

    @Override
    public ConfigServerType getType() {
        return ConfigServerType.ZOOKEEPER;
    }

    @Override
    public void add(ConfigServer server, String application, String profile, String key, String value) {
        CuratorFramework client = this.getClient(server);
        String path;
        if (null != profile)
            path = application + "," + profile;
        else
            path = application;
        try {

            client.create().forPath("/" + path + "/" + key, value.getBytes("utf-8"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(ConfigServer server, String application, Optional<String> profile, String key, String newValue) {
        CuratorFramework client = this.getClient(server);
        String path;
        if (profile.isPresent())
            path = application + "," + profile.get();
        else path = application;
        try {
            client.setData().forPath("/" + path + "/" + key, newValue.getBytes("utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(ConfigServer server, String application, String profile, String key) {
        CuratorFramework client = this.getClient(server);
        String path;
        if (null == profile)
            path = application;
        else
            path = application + "," + profile;
        try {
            client.delete().forPath("/" + path + "/" + key);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String findValue(ConfigServer server, String application, String profile, String key) {
        CuratorFramework client = this.getClient(server);
        String path;
        if (null != profile)
            path = application + "," + profile;
        else
            path = application;

        try {
            return new String(client.getData().forPath("/" + path + "/" + key));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Map<String, String> findAll(ConfigServer server, String application, String profile) {
        CuratorFramework client = this.getClient(server);
        return load(client, application, profile);
//        return configs.computeIfAbsent(client, k -> {
//            return load(client);
//        });
    }

    private Map<String, String> load(CuratorFramework client, String application, String profile) {
        Map<String, String> result = new HashMap<>();
        String root;
        if (application.equalsIgnoreCase("default"))
            root = "application";
        else {
            root = application;
            if (profile != null)
                root += "," + profile;
        }
        try {
            String finalRoot = root;
            client
                    .getChildren()
                    .forPath("/" + root)
                    .forEach(child -> {
                        try {
                            result.put(child, new String(client.getData().forPath("/" + finalRoot + "/" + child)));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();

        }
        return result;
    }

    @Override
    public boolean hasChildren(ConfigServer server) {
        CuratorFramework client = this.getClient(server);
        try {
            return !client.getChildren().forPath("/").isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<String> getApplicationsWithProfile(ConfigServer server) {
        CuratorFramework client = this.getClient(server);
        try {
            return client
                    .getChildren()
                    .forPath("/");
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public void addApplication(ConfigServer server, String application, String profile) {
        CuratorFramework client = this.getClient(server);
        String path;
        if (profile == null)
            path = application;
        else
            path = application + "," + profile;
        try {
            client.create().forPath("/" + path);
        } catch (Exception e) {
            if (e instanceof KeeperException.NodeExistsException) {
                throw new ServiceException(ServiceExceptionEnum.CONFIG_EXISTED);
            }
            e.printStackTrace();
        }
    }

    @Override
    public List<String> getProfiles(ConfigServer server, String application) {
        CuratorFramework client = this.getClient(server);
        try {
            return client.getChildren().forPath("/")
                    .stream()
                    .filter(p -> p.startsWith(application) && !p.equals(application))
                    .map(s -> s.substring(s.indexOf(",") + 1, s.length()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public void deleteApplication(ConfigServer server, String application, String profile) {
        CuratorFramework client = this.getClient(server);
        if (profile == null) {
            try {
                client.getChildren().forPath("/").stream().filter(c -> c.startsWith(application)).forEach(c -> {
                    this.delete("/" + c, client);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else
            this.delete("/" + application + "," + profile, client);
    }


    private void delete(String path, CuratorFramework client) {
        try {
            client.delete().forPath(path);
        } catch (Exception e) {
            if (e instanceof KeeperException.NoNodeException) {
                throw new ServiceException(ServiceExceptionEnum.CONFIG_NOT_EXISTED);
            }
            e.printStackTrace();
        }
    }

}
