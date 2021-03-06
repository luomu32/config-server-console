package xyz.luomu32.config.server.console.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.luomu32.config.server.console.entity.ConfigServer;
import xyz.luomu32.config.server.console.service.ConfigService;
import xyz.luomu32.config.server.console.web.request.Application;
import xyz.luomu32.config.server.console.web.request.UserPrincipal;
import xyz.luomu32.config.server.console.repo.UserApplicationRepo;
import xyz.luomu32.config.server.console.service.ClientService;
import xyz.luomu32.config.server.console.service.ConfigServerService;
import xyz.luomu32.config.server.console.web.exception.ServiceException;
import xyz.luomu32.config.server.console.web.exception.ServiceExceptionEnum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("server/application")
public class ApplicationController {

    @Autowired
    private ConfigServerService configServerService;
    @Autowired
    private UserApplicationRepo userApplicationRepo;
    @Autowired
    private ClientService clientService;

    @Autowired
    private ConfigService configService;


    @GetMapping
    public List<Application> getApplications() {
        ConfigServer server = configServerService.get();
        if (null == server)
            return Collections.emptyList();

//        List<String> applicationNames = userApplicationRepo.findByUserId(currentUser.getId())
//                .stream()
//                .map(u -> u.getApplication())
//                .collect(Collectors.toList());

        List<Application> result = new ArrayList<>();

        Map<String/*application name*/, List<String>/*profiles*/> applications = clientService.getApplicationsWithProfile(server)
                .stream()
//                .filter(a -> {
//                    if (a.indexOf(",") == -1) {
//                        return applicationNames.contains(a);
//                    } else {
//                        return applicationNames.contains(a.substring(0, a.indexOf(",")));
//                    }
//                })
                .collect(Collectors.groupingBy(a -> {
                    if (a.indexOf(",") == -1)
                        return a;
                    return a.substring(0, a.indexOf(","));
                }));

        applications.forEach((name, profiles) -> {
            Application application = new Application();
            application.setName(name);
            if (profiles.size() == 1 && name.equals(profiles.get(0))) {
                application.setProfiles(Collections.emptySet());
            } else {
                application.setProfiles(profiles.stream().map(p -> {
                    if (p.equals(name))
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
    public void createApplication(@RequestParam String name,
                                  @RequestParam(required = false) String profile) {
        ConfigServer server = configServerService.get();
        if (null != server) {
            clientService.addApplication(server, name, profile);
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
    public void delete(@PathVariable String application, UserPrincipal currentUser) {
        configService.deleteApplication(application, null, currentUser);
    }

    @PostMapping("{application}/profile")
    public void createProfile(@PathVariable String application,
                              @RequestParam("name") String profileName) {
        ConfigServer server = configServerService.get();
        if (null == server) {
            throw new ServiceException(ServiceExceptionEnum.CONFIG_SERVER_NOT_FOUND);
        }

        clientService.addProfile(server, application, profileName);
    }

    @DeleteMapping("{application}/profile/{profile}")
    public void deleteProfile(@PathVariable String application,
                              @PathVariable String profile) {

        ConfigServer server = configServerService.get();
        if (null == server) {
            throw new ServiceException(ServiceExceptionEnum.CONFIG_SERVER_NOT_FOUND);
        }

        clientService.deleteApplication(server, application, profile);
    }

}
