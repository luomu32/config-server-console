package xyz.luomu32.config.server.console.service;

import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import xyz.luomu32.config.server.console.entity.*;
import xyz.luomu32.config.server.console.repo.*;
import xyz.luomu32.config.server.console.web.request.MenuWithAction;
import xyz.luomu32.config.server.console.web.request.MenusAndActions;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleService {

    private final RoleRepo roleRepo;
    private final UserRepo userRepo;
    private final RoleMenuPermissionRepo roleMenuPermissionRepo;
    private final RoleActionPermissionRepo roleActionPermissionRepo;
    private final MenuRepo menuRepo;

    public RoleService(RoleRepo roleRepo,
                       UserRepo userRepo,
                       RoleMenuPermissionRepo roleMenuPermissionRepo,
                       RoleActionPermissionRepo roleActionPermissionRepo,
                       MenuRepo menuRepo) {
        this.roleRepo = roleRepo;
        this.userRepo = userRepo;
        this.roleMenuPermissionRepo = roleMenuPermissionRepo;
        this.roleActionPermissionRepo = roleActionPermissionRepo;
        this.menuRepo = menuRepo;
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


    public MenusAndActions menuWithActions(Long id) {

        MenusAndActions menusAndActions = new MenusAndActions();
        menusAndActions.setMenus(roleMenuPermissionRepo.findByRoleId(id)
                .stream()
                .map(RoleMenuPermission::getMenuId)
                .collect(Collectors.toList()));
        menusAndActions.setActions(roleActionPermissionRepo.findByRoleId(id)
                .stream()
                .map(RoleActionPermission::getMenuActionId)
                .collect(Collectors.toList()));
        return menusAndActions;
    }

}
