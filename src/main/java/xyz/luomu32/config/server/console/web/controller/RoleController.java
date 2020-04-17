package xyz.luomu32.config.server.console.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import xyz.luomu32.config.server.console.entity.Role;
import xyz.luomu32.config.server.console.service.AuthorityService;
import xyz.luomu32.config.server.console.web.exception.ServiceException;
import xyz.luomu32.config.server.console.web.exception.ServiceExceptionEnum;
import xyz.luomu32.config.server.console.web.interceptor.AuthenticationInterceptor;
import xyz.luomu32.config.server.console.web.request.UserPrincipal;
import xyz.luomu32.config.server.console.web.response.Permission;
import xyz.luomu32.config.server.console.web.request.RolePojo;
import xyz.luomu32.config.server.console.repo.RoleRepo;
import xyz.luomu32.config.server.console.service.RoleService;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("role")
public class RoleController {

    @Autowired
    private RoleRepo roleRepo;
    @Autowired
    private RoleService roleService;

    @Autowired
    private AuthorityService authorityService;


    @GetMapping()
    public List<Role> getAllRoles(@RequestParam(required = false) String name) {
        Role query = new Role();
        query.setName(name);
        return roleRepo.findAll(Example.of(query));
    }

    @PostMapping
    public void create(@RequestParam String name) {
        Role r = new Role();
        r.setName(name);
        r.setCreatedDatetime(LocalDateTime.now());
        r.setLastModifyDatetime(r.getCreatedDatetime());
        roleRepo.save(r);
    }

    @GetMapping("{id}")
    public Role getRoleById(@PathVariable Long id) {
        return roleRepo.findById(id).orElse(null);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Long id) {
        roleService.delete(id);
    }

    @PutMapping("{id}")
    public void update(@PathVariable Long id,
                       @RequestParam String name) {
        Role role = roleRepo.findById(id).orElseThrow(() -> new ServiceException(ServiceExceptionEnum.ROLE_NOT_FOUND));

        role.setName(name);
        role.setLastModifyDatetime(LocalDateTime.now());
        roleRepo.save(role);
    }

    @GetMapping("{id}/permissions")
    public Permission permissions(@PathVariable Long id) {
        return roleService.getPermissions(id);
    }

    /**
     * 授权
     */
    @PutMapping("{id}/grant-permission")
    public Permission authorization(
            @PathVariable Long id,
            @RequestParam List<Long> menus,
            @RequestParam List<Long> actions, UserPrincipal currentUser, HttpSession session) {

        authorityService.grant(id, menus, actions);
        Permission permission = roleService.getPermissions(id);

        currentUser.setPermission(permission);
        session.setAttribute(AuthenticationInterceptor.USER_HOLDER, currentUser);
        return permission;
    }
}
