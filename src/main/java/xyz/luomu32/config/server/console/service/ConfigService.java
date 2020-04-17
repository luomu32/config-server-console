package xyz.luomu32.config.server.console.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.luomu32.config.server.console.entity.ConfigServer;
import xyz.luomu32.config.server.console.repo.UserApplicationRepo;
import xyz.luomu32.config.server.console.web.exception.ServiceException;
import xyz.luomu32.config.server.console.web.exception.ServiceExceptionEnum;
import xyz.luomu32.config.server.console.web.request.UserPrincipal;

@Service
public class ConfigService {

    @Autowired
    private ConfigServerService configServerService;
    @Autowired
    private UserApplicationRepo userApplicationRepo;
    @Autowired
    private ClientService clientService;

    @Transactional
    public void deleteApplication(String applicationName, String profileName, UserPrincipal currentUser) {
        ConfigServer server = configServerService.get();
        if (null == server) {
            throw new ServiceException(ServiceExceptionEnum.CONFIG_SERVER_NOT_FOUND);
        }
        clientService.deleteApplication(server, applicationName, profileName);
        userApplicationRepo.deleteByUserIdAndApplication(currentUser.getId(), applicationName);
    }
}
