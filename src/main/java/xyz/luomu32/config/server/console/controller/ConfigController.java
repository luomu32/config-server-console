package xyz.luomu32.config.server.console.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import xyz.luomu32.config.server.console.entity.ConfigServer;
import xyz.luomu32.config.server.console.entity.Log;
import xyz.luomu32.config.server.console.entity.LogChangeType;
import xyz.luomu32.config.server.console.interceptor.AuthenticationInterceptor;
import xyz.luomu32.config.server.console.pojo.Config;
import xyz.luomu32.config.server.console.pojo.LoginedUser;
import xyz.luomu32.config.server.console.repo.ConfigServerRepo;
import xyz.luomu32.config.server.console.repo.LogRepo;
import xyz.luomu32.config.server.console.service.ClientService;
import xyz.luomu32.config.server.console.service.ConfigServerService;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("config/{application}")
public class ConfigController {

    @Autowired
    private ClientService clientService;

    @Autowired
    private ConfigServerRepo configServerRepo;

    @Autowired
    private ConfigServerService configServerService;

    @Autowired
    private LogRepo logRepo;

    @GetMapping("all")
    public Map<String, String> getAll(
            @PathVariable String application,
            @RequestParam(required = false) String profile) {
        ConfigServer configServer = configServerService.get();
        if (null == configServer)
            return Collections.emptyMap();

        return clientService.findAll(configServer, application, profile);
    }


    @GetMapping
    public String get(@PathVariable String application,
                      @RequestParam String key,
                      @RequestParam(required = false) String profile) {
        ConfigServer configServer = configServerService.get();
        if (null == configServer)
            return null;

        return clientService.findValue(configServer, application, profile, key);
    }

    //put form类型的好像不行
    @PutMapping
    public void update(@PathVariable String application,
                       @RequestBody Config config, HttpSession session) {
        ConfigServer configServer = configServerService.get();
        if (null == configServer)
            return;

        String oldValue = clientService.findValue(configServer, application, config.getProfile(), config.getKey());
        if (oldValue.equals(config.getValue()))
            return;
        clientService.update(configServer, application, Optional.ofNullable(config.getProfile()), config.getKey(), config.getValue());
        LoginedUser currentUser = (LoginedUser) session.getAttribute(AuthenticationInterceptor.USER_HOLDER);
        Log log = new Log();
        log.setApplication(application);
        log.setProfile(config.getProfile());
        log.setConfigKey(config.getKey());
        log.setChangeType(LogChangeType.UPDATE_CONFIG);
        log.setContent("修改前：" + oldValue + "。修改后：" + config.getValue());
        log.setCreatedDatetime(LocalDateTime.now());
        log.setOperatorId(currentUser.getId());
        log.setOperatorName(currentUser.getUsername());
        logRepo.save(log);
    }

    @PostMapping
    public void create(@PathVariable String application,
                       @RequestParam String key,
                       @RequestParam String value,
                       @RequestParam(required = false) String profile,
                       HttpSession session) {

        ConfigServer configServer = configServerService.get();
//                .orElseThrow(() -> new RuntimeException("config.server.not.found"));

        clientService.add(configServer, application, profile, key, value);

        LoginedUser currentUser = (LoginedUser) session.getAttribute(AuthenticationInterceptor.USER_HOLDER);
        Log log = new Log();
        log.setApplication(application);
        log.setProfile(profile);
        log.setConfigKey(key);
        log.setChangeType(LogChangeType.ADD_CONFIG);
        log.setContent(value);
        log.setCreatedDatetime(LocalDateTime.now());
        log.setOperatorId(currentUser.getId());
        log.setOperatorName(currentUser.getUsername());
        logRepo.save(log);
    }

    @DeleteMapping
    public void delete(@PathVariable String application,
                       @RequestParam String key,
                       @RequestParam(required = false) String profile, HttpSession session) {
        ConfigServer configServer = configServerService.get();
        if (null == configServer)
            return;
        String oldValue = clientService.findValue(configServer, application, profile, key);
        clientService.delete(configServer, application, profile, key);
        LoginedUser currentUser = (LoginedUser) session.getAttribute(AuthenticationInterceptor.USER_HOLDER);
        Log log = new Log();
        log.setApplication(application);
        log.setProfile(profile);
        log.setConfigKey(key);
        log.setChangeType(LogChangeType.DELETE_CONFIG);
        log.setContent(oldValue);
        log.setCreatedDatetime(LocalDateTime.now());
        log.setOperatorId(currentUser.getId());
        log.setOperatorName(currentUser.getUsername());
        logRepo.save(log);
    }
}