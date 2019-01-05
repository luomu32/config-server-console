package xyz.luomu32.config.server.console.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.luomu32.config.server.console.entity.RoleActionPermission;
import xyz.luomu32.config.server.console.entity.RoleMenuPermission;
import xyz.luomu32.config.server.console.repo.RoleActionPermissionRepo;
import xyz.luomu32.config.server.console.repo.RoleMenuPermissionRepo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthorityService {

    private final RoleMenuPermissionRepo roleMenuPermissionRepo;
    private final RoleActionPermissionRepo roleActionPermissionRepo;

    public AuthorityService(RoleMenuPermissionRepo roleMenuPermissionRepo,
                            RoleActionPermissionRepo roleActionPermissionRepo) {
        this.roleMenuPermissionRepo = roleMenuPermissionRepo;
        this.roleActionPermissionRepo = roleActionPermissionRepo;
    }

    @Transactional
    public void grant(Long roleId, List<Long> menuIds, List<Long> actoinIds) {
        List<RoleMenuPermission> roleMenuPermissions = roleMenuPermissionRepo.findByRoleId(roleId);
        List<Long> roleActionPermissions = roleActionPermissionRepo.findByRoleId(roleId)
                .stream()
                .map(RoleActionPermission::getMenuActionId)
                .collect(Collectors.toList());

        List<Long> deletedMenuPermission = new ArrayList<>();
        List<Long> addMenuPermission = new ArrayList<>();
        menuIds.stream().forEach(m -> {
            boolean isExisted = roleMenuPermissions.stream().filter(rm -> rm.getMenuId().equals(m)).count() > 0;
            if (!isExisted)
                addMenuPermission.add(m);
        });
        roleMenuPermissions.forEach(m -> {
            boolean isExisted = menuIds.contains(m.getMenuId());
            if (!isExisted)
                deletedMenuPermission.add(m.getMenuId());
        });

        deletedMenuPermission.forEach(m -> roleMenuPermissionRepo.deleteByMenuId(m));
        addMenuPermission.forEach(m -> {
            RoleMenuPermission permission = new RoleMenuPermission();
            permission.setMenuId(m);
            permission.setRoleId(roleId);
            roleMenuPermissionRepo.save(permission);
        });

        List<Long> deletedActionPermissions = new ArrayList<>();
        List<Long> addActionPermissions = new ArrayList<>();
        actoinIds.stream().forEach(a -> {
            if (!roleActionPermissions.contains(a))
                addActionPermissions.add(a);
        });
        roleActionPermissions.forEach(a -> {
            if (!actoinIds.contains(a))
                deletedActionPermissions.add(a);
        });
        deletedActionPermissions.forEach(a -> roleActionPermissionRepo.deleteByMenuActionId(a));
        addActionPermissions.forEach(a -> {
            RoleActionPermission permission = new RoleActionPermission();
            permission.setRoleId(roleId);
            permission.setMenuActionId(a);
            roleActionPermissionRepo.save(permission);
        });

    }
}
