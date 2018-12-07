package xyz.luomu32.config.server.console.service;

import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import xyz.luomu32.config.server.console.entity.DeleteFlagEnum;
import xyz.luomu32.config.server.console.entity.Role;
import xyz.luomu32.config.server.console.entity.User;
import xyz.luomu32.config.server.console.repo.RoleRepo;
import xyz.luomu32.config.server.console.repo.UserRepo;

@Service
public class RoleService {

    private final RoleRepo roleRepo;
    private final UserRepo userRepo;

    public RoleService(RoleRepo roleRepo, UserRepo userRepo) {
        this.roleRepo = roleRepo;
        this.userRepo = userRepo;
    }

    public void delete(Long roleId) {

        User query = new User();
        Role role = new Role();
        role.setId(roleId);
        query.setRole(role);
        query.setDeleted(DeleteFlagEnum.UN_DELETED);
        if (userRepo.count(Example.of(query)) != 0)
            throw new RuntimeException("user.exited.under.role");

        roleRepo.deleteById(roleId);
    }
}
