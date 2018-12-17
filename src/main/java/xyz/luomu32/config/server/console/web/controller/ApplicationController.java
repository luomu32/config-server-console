package xyz.luomu32.config.server.console.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.luomu32.config.server.console.entity.ConfigServer;
import xyz.luomu32.config.server.console.pojo.Application;
import xyz.luomu32.config.server.console.service.ClientService;
import xyz.luomu32.config.server.console.service.ConfigServerService;
import xyz.luomu32.config.server.console.web.exception.ServiceException;
import xyz.luomu32.config.server.console.web.exception.ServiceExceptionEnum;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("server/application")
public class ApplicationController {

    @Autowired
    private ConfigServerService configServerService;
    @Autowired
    private ClientService clientService;

    @GetMapping
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

    @PostMapping
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

    @DeleteMapping("{application}")
    public void delete(@PathVariable String application) {
        ConfigServer server = configServerService.get();
        if (null == server) {
            throw new ServiceException(ServiceExceptionEnum.CONFIG_SERVER_NOT_FOUND);
        }
        clientService.deleteApplication(server, application, null);
    }

//    @Deprecated
//    @GetMapping("{id}/{application}/profiles")
//    public Set<String> getProfiles(@PathVariable Long id, @PathVariable String application) {
//        Optional<ConfigServer> configServer = configServerRepo.findById(id);
//        if (!configServer.isPresent())
//            return Collections.emptySet();
//
//        Set<String> profiles = new HashSet<>();
//        clientService.getApplicationsWithProfile(configServer.get()).stream().filter(a -> a.startsWith(application)).forEach(a -> {
//            if (a.indexOf(",") > 0)
//                profiles.add(a.substring(a.indexOf(",") + 1, a.length()));
//        });
//        return profiles;
//    }
}
