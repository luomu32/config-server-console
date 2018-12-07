package xyz.luomu32.config.server.console.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.luomu32.config.server.console.entity.ConfigServer;
import xyz.luomu32.config.server.console.repo.ConfigServerRepo;

import java.util.List;
import java.util.Optional;

@Service
public class ConfigServerService {

    @Autowired
    private ClientService clientService;

    @Autowired
    private ConfigServerRepo configServerRepo;

    public ConfigServer get() {
        List<ConfigServer> servers = configServerRepo.findAll();
        if (servers.isEmpty())
            return null;
        return servers.get(0);
    }

    public void delete(Long id) {
        configServerRepo.findById(id).ifPresent(server -> {
            if (!clientService.hasChildren(server))
                configServerRepo.deleteById(id);
        });
    }
}
