package xyz.luomu32.config.server.console.service;

import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import xyz.luomu32.config.server.console.entity.*;
import xyz.luomu32.config.server.console.repo.*;
import xyz.luomu32.config.server.console.web.exception.ServiceException;
import xyz.luomu32.config.server.console.web.exception.ServiceExceptionEnum;
import xyz.luomu32.config.server.console.web.response.Permission;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RoleService {

    private final RoleRepo roleRepo;
    private final UserRepo userRepo;
    private final RoleMenuPermissionRepo roleMenuPermissionRepo;
    private final RoleActionPermissionRepo roleActionPermissionRepo;
    private final MenuRepo menuRepo;
    private final MenuActionRepo menuActionRepo;

    public RoleService(RoleRepo roleRepo,
                       UserRepo userRepo,
                       RoleMenuPermissionRepo roleMenuPermissionRepo,
                       RoleActionPermissionRepo roleActionPermissionRepo,
                       MenuRepo menuRepo,
                       MenuActionRepo menuActionRepo) {
        this.roleRepo = roleRepo;
        this.userRepo = userRepo;
        this.roleMenuPermissionRepo = roleMenuPermissionRepo;
        this.roleActionPermissionRepo = roleActionPermissionRepo;
        this.menuRepo = menuRepo;
        this.menuActionRepo = menuActionRepo;
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


    public Permission getPermissions(Long id) {
        Permission rolePermission = new Permission();

        rolePermission.setActions(
                roleActionPermissionRepo.findByRoleId(id)
                        .stream()
                        .map(ra ->
                                menuActionRepo.findById(ra.getMenuActionId())
                                        .orElseThrow(() -> new ServiceException(ServiceExceptionEnum.MENU_ACTION_NOT_FOUND))
                        ).collect(Collectors.toList()));

        List<Menu> menus = roleMenuPermissionRepo.findByRoleId(id)
                .stream()
                .map(rm ->
                        menuRepo.findById(rm.getMenuId())
                                .orElseThrow(() -> new ServiceException(ServiceExceptionEnum.MENU_NOT_FOUND))
                )
                .collect(Collectors.toList());
        List<Menu> noChildrenMenu = new ArrayList<>();
        List<Menu> haveChildrenMenu = new ArrayList<>();
//
        for (Menu menu : menus) {
            if (menu.getFatherId() == null)
                noChildrenMenu.add(menu);
            else
                haveChildrenMenu.add(menu);
        }
//
        Map<Long, List<Menu>> menuMap = haveChildrenMenu.stream().collect(Collectors.groupingBy(Menu::getFatherId));

        noChildrenMenu.addAll(haveChildrenMenu
                .stream()
                .filter(menu -> menu.getFatherId() != null)
                .map(menu -> menu.getFatherId())
                .collect(Collectors.toSet())
                .stream().map(menuId -> {
                            Menu father = menuRepo.findById(menuId).orElseThrow(() -> new ServiceException(ServiceExceptionEnum.MENU_NOT_FOUND));
                            Set<Menu> children = new HashSet<>();
                            children.addAll(menuMap.get(menuId));
                            father.setChildren(children);
                            return father;
                        }
                )
                .collect(Collectors.toList()));


        rolePermission.setMenus(
                noChildrenMenu
        );


        return rolePermission;
    }

}
