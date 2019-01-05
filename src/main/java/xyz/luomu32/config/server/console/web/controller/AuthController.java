package xyz.luomu32.config.server.console.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import xyz.luomu32.config.server.console.entity.*;
import xyz.luomu32.config.server.console.repo.*;
import xyz.luomu32.config.server.console.service.AuthorityService;
import xyz.luomu32.config.server.console.web.exception.ServiceException;
import xyz.luomu32.config.server.console.web.exception.ServiceExceptionEnum;
import xyz.luomu32.config.server.console.web.interceptor.AuthenticationInterceptor;
import xyz.luomu32.config.server.console.web.request.MenuWithAction;
import xyz.luomu32.config.server.console.web.request.UserPrincipal;
import xyz.luomu32.config.server.console.web.request.UserPojo;

import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class AuthController {

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private RoleRepo roleRepo;
    @Autowired
    private MenuRepo menuRepo;
    @Autowired
    private MenuActionRepo menuActionRepo;
    @Autowired
    private RoleMenuPermissionRepo roleMenuPermissionRepo;
    @Autowired
    private RoleActionPermissionRepo roleActionPermissionRepo;

    @Autowired
    private AuthorityService authorityService;

    /**
     * 认证
     *
     * @param user
     * @param session
     */
    @PostMapping("auth")
    public UserPrincipal authenticate(@Validated(UserPojo.AuthValid.class) UserPojo user, HttpSession session) {
        User dbUser = userRepo.findByUsernameAndDeleted(user.getName(), DeleteFlagEnum.UN_DELETED)
                .orElseThrow(() -> new ServiceException(ServiceExceptionEnum.USER_NOT_FOUND));
        if (!dbUser.getPassword().equals(user.getPassword()))
            throw new ServiceException(ServiceExceptionEnum.USER_PASSWORD_NOT_MATCH);

        List<Menu> menus = roleMenuPermissionRepo
                .findByRoleId(dbUser.getRole().getId())
                .stream()
                .map(ra -> menuRepo.findById(ra.getMenuId()).orElse(null))
                .filter(menu -> null != menu)
                .collect(Collectors.toList());

        List<MenuAction> actions = roleActionPermissionRepo
                .findByRoleId(dbUser.getRole().getId())
                .stream()
                .map(ra -> menuActionRepo.findById(ra.getMenuActionId()).orElse(null))
                .filter(menu -> null != menu)
                .collect(Collectors.toList());

        UserPrincipal userPrincipal = new UserPrincipal();
        userPrincipal.setUsername(dbUser.getUsername());
        userPrincipal.setId(dbUser.getId());
        userPrincipal.setRoleId(dbUser.getRole().getId());
        userPrincipal.setRoleName(dbUser.getRole().getName());
        userPrincipal.setMenus(menus);
        userPrincipal.setActions(actions);
        session.setAttribute(AuthenticationInterceptor.USER_HOLDER, userPrincipal);


//        UserAuthResponse response = new UserAuthResponse();
//        response.setId(dbUser.getId());
//        response.setUsername(dbUser.getUsername());
//        response.setExpireAt(ZonedDateTime.now().plusMinutes(29).plusSeconds(50).toInstant().toEpochMilli());
//        response.setRole(dbUser.getRole());
//        response.setMenus(menus);
        return userPrincipal;
    }

    @PostMapping("sing-out")
    public void signOut(HttpSession session) {

        session.removeAttribute(AuthenticationInterceptor.USER_HOLDER);
    }

    /**
     * 授权
     */
    @PutMapping("grant-permission")
    public void authorization(
            @RequestParam Long roleId,
            @RequestParam List<Long> menus,
            @RequestParam List<Long> actions) {

        authorityService.grant(roleId, menus, actions);
    }

    @GetMapping("menu-with-actions")
    public List<MenuWithAction> menuWithActions() {
        return menuRepo.findAll().stream().map(m -> {
            MenuWithAction menuWithAction = new MenuWithAction();
            menuWithAction.setMenuId(m.getId());
            menuWithAction.setMenuName(m.getTitle());

            menuWithAction.setActions(menuActionRepo.findByMenuId(m.getId()));
            return menuWithAction;
        }).collect(Collectors.toList());
    }
}
