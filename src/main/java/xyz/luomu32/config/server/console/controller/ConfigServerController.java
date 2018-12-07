package xyz.luomu32.config.server.console.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xyz.luomu32.config.server.console.entity.ConfigServer;
import xyz.luomu32.config.server.console.pojo.Application;
import xyz.luomu32.config.server.console.pojo.Server;
import xyz.luomu32.config.server.console.repo.ConfigServerRepo;
import xyz.luomu32.config.server.console.service.ClientService;
import xyz.luomu32.config.server.console.service.ConfigServerService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("server")
public class ConfigServerController {

    @Autowired
    private ConfigServerRepo configServerRepo;
    @Autowired
    private ConfigServerService configServerService;
    @Autowired
    private ClientService clientService;

    @PostMapping
    public void create(@RequestBody Server server) {
        ConfigServer configServer = new ConfigServer();
        configServer.setName(server.getName());
        configServer.setPrefix(server.getPrefix());
        configServer.setUrl(server.getUrl());
        configServer.setType(server.getType());
        configServerRepo.save(configServer);
    }

    @PutMapping
    public void update(@RequestBody Server server) {
        ConfigServer configServer = new ConfigServer();
        configServer.setId(server.getId());
        configServer.setName(server.getName());
        configServer.setType(server.getType());
        configServer.setUrl(server.getUrl());
        configServer.setPrefix(server.getPrefix());
        configServerRepo.save(configServer);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Long id) {
        configServerService.delete(id);
    }

    @GetMapping
    public List<ConfigServer> getAll() {
        return configServerRepo.findAll();
    }


    @PostMapping("key")
    public void uploadKey(
//            @RequestParam("filename") String name,
            @RequestPart("file") MultipartFile key) {
        //TODO 文件类型检查
        //TODO 文件大小限定设置
        try {
            Path path = Paths.get(System.getProperty("user.home"), ".config_server_key");
            if (!Files.exists(path)) {
                Files.createDirectory(path);
            }
            Files.write(path.resolve(key.getOriginalFilename()), key.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
//        KeyStore
    }

    @GetMapping("{id}/application")
    @Deprecated
    public Set<String> getApplications(@PathVariable Long id) {
        Optional<ConfigServer> configServer = configServerRepo.findById(id);
        if (!configServer.isPresent())
            return Collections.emptySet();

        Set<String> applications = new HashSet<>();
        clientService.getApplicationsWithProfile(configServer.get()).forEach(a -> {
            if (a.indexOf(",") > 0)
                applications.add(a.substring(0, a.indexOf(",")));
            else
                applications.add(a);
        });
        return applications;
    }

    @GetMapping("application")
    public List<Application> getApplications() {
        ConfigServer server = configServerService.get();
        if (null == server)
            return Collections.emptyList();

        List<Application> result = new ArrayList<>();
        clientService.getApplicationsWithProfile(server)
                .stream()
                .collect(Collectors.groupingBy(a -> {
                    if (a.indexOf(",") == -1)
                        return a;
                    return a.substring(0, a.indexOf(","));
                })).forEach((k, v) -> {

            Application application = new Application();
            application.setName(k);
            if (v.size() == 1 && k.equals(v.get(0))) {
                application.setProfiles(Collections.emptySet());
            } else {
                application.setProfiles(v.stream().map(p -> {
                    if (p.equals(k))
                        return "default";
                    else
                        return p.substring(p.indexOf(",") + 1, p.length());
                }).collect(Collectors.toSet()));
            }
            result.add(application);
        });
        return result;
    }

    @PostMapping("application")
    public void createApplication(@RequestParam String application,
                                  @RequestParam(required = false) String profile) {
        ConfigServer server = configServerService.get();
        if (null != server) {
            clientService.addApplication(server, application, profile);
        }
    }

    @GetMapping("{application}/profile")
    public List<String> getApplicationProfile(@PathVariable String application) {
        ConfigServer server = configServerService.get();
        if (null == server) {
            return Collections.emptyList();
        }

        return clientService.getProfiles(server, application);
    }

    @Deprecated
    @GetMapping("{id}/{application}/profiles")

    public Set<String> getProfiles(@PathVariable Long id, @PathVariable String application) {
        Optional<ConfigServer> configServer = configServerRepo.findById(id);
        if (!configServer.isPresent())
            return Collections.emptySet();

        Set<String> profiles = new HashSet<>();
        clientService.getApplicationsWithProfile(configServer.get()).stream().filter(a -> a.startsWith(application)).forEach(a -> {
            if (a.indexOf(",") > 0)
                profiles.add(a.substring(a.indexOf(",") + 1, a.length()));
        });
        return profiles;
    }

    @GetMapping("{id}")
    public ConfigServer getOne(@PathVariable Long id) {
        return configServerRepo.findById(id).orElse(null);
    }

}
