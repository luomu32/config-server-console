package xyz.luomu32.config.server.console.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.InputStreamResource;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import xyz.luomu32.config.server.console.entity.ConfigServer;
import xyz.luomu32.config.server.console.entity.Log;
import xyz.luomu32.config.server.console.entity.LogChangeType;
import xyz.luomu32.config.server.console.web.exception.ServiceException;
import xyz.luomu32.config.server.console.web.exception.ServiceExceptionEnum;
import xyz.luomu32.config.server.console.web.request.Config;
import xyz.luomu32.config.server.console.web.request.UserPrincipal;
import xyz.luomu32.config.server.console.repo.ConfigServerRepo;
import xyz.luomu32.config.server.console.repo.LogRepo;
import xyz.luomu32.config.server.console.service.ClientService;
import xyz.luomu32.config.server.console.service.ConfigServerService;
import xyz.luomu32.config.server.console.web.response.BatchResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

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

    @PutMapping("{key:.+}")
    public void update(@PathVariable String application,
                       @PathVariable String key,
                       Config config, UserPrincipal currentUser) {
        ConfigServer configServer = configServerService.get();
        if (null == configServer)
            return;

        String oldValue = clientService.findValue(configServer, application, config.getProfile(), key);
        if (oldValue.equals(config.getValue()))
            return;
        clientService.update(configServer, application, Optional.ofNullable(config.getProfile()), key, config.getValue());
        Log log = new Log();
        log.setApplication(application);
        log.setProfile(config.getProfile());
        log.setConfigKey(key);
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
                       UserPrincipal currentUser) {

        ConfigServer configServer = configServerService.get();
//                .orElseThrow(() -> new RuntimeException("config.server.not.found"));

        boolean result = clientService.add(configServer, application, profile, key, value);

        if (result) {
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
    }

    @DeleteMapping("{key:.+}")
    public void delete(@PathVariable String application,
                       @PathVariable String key,
                       @RequestParam(required = false) String profile,
                       UserPrincipal currentUser) {
        ConfigServer configServer = configServerService.get();
        if (null == configServer)
            return;
        String oldValue = clientService.findValue(configServer, application, profile, key);
        clientService.delete(configServer, application, profile, key);
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

    @PostMapping("export")
    public ModelAndView export(@PathVariable String application,
                               @RequestParam(required = false) String profile,
                               Model model) {
        ConfigServer configServer = configServerService.get();
        if (null == configServer)
            return null;

        Map<String, String> configs = clientService.findAll(configServer, application, profile);
        model.addAllAttributes(configs);

        String filename = application + (null == profile ? "" : ("." + profile));

        return new ModelAndView(filename);
    }

    @PostMapping("import")
    public BatchResponse importing(@PathVariable String application,
                                   @RequestParam(required = false) String profile,
                                   @RequestPart("file") MultipartFile file) {

        String filename = file.getOriginalFilename();
        int pos = filename.lastIndexOf(".");
        String ext = filename.substring(pos + 1, filename.length());

        ConfigServer configServer = configServerService.get();
        if (null == configServer)
            throw new ServiceException(ServiceExceptionEnum.CONFIG_SERVER_NOT_FOUND);


        long successCount = 0, failedCount = 0;

        if (ext.equalsIgnoreCase("properties")) {
            Properties properties = new Properties();
            try {
                properties.load(file.getInputStream());

                for (String k : properties.stringPropertyNames()) {
                    if (clientService.add(configServer, application, profile, k, properties.getProperty(k)))
                        successCount++;
                    else failedCount++;
                }

            } catch (IOException e) {

            }
        } else if (ext.equalsIgnoreCase("yml")) {
            try {
                YamlPropertiesFactoryBean factoryBean = new YamlPropertiesFactoryBean();
                factoryBean.setResources(new InputStreamResource(file.getInputStream()));
                Properties properties = factoryBean.getObject();
                for (String k : properties.stringPropertyNames()) {

                    if (clientService.add(configServer, application, profile, k, properties.getProperty(k)))
                        successCount++;
                    else failedCount++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            throw new RuntimeException("upload.file.type.not.support");
        }

        return new BatchResponse(successCount, failedCount);
    }
}
