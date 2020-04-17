package xyz.luomu32.config.server.console.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xyz.luomu32.config.server.console.entity.ConfigServer;
import xyz.luomu32.config.server.console.web.request.Server;
import xyz.luomu32.config.server.console.repo.ConfigServerRepo;
import xyz.luomu32.config.server.console.service.ClientService;
import xyz.luomu32.config.server.console.service.ConfigServerService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

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
    public void update(@RequestBody @Validated Server server) {
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
        //TODO 文件大小限定设置c
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


    @GetMapping("{id}")
    public ConfigServer getOne(@PathVariable Long id) {
        return configServerRepo.findById(id).orElse(null);
    }

}
