package xyz.luomu32.config.server.console.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import xyz.luomu32.config.server.console.entity.*;
import xyz.luomu32.config.server.console.repo.*;
import xyz.luomu32.config.server.console.service.AuthorityService;
import xyz.luomu32.config.server.console.service.RoleService;
import xyz.luomu32.config.server.console.web.exception.ServiceException;
import xyz.luomu32.config.server.console.web.exception.ServiceExceptionEnum;
import xyz.luomu32.config.server.console.web.interceptor.AuthenticationInterceptor;
import xyz.luomu32.config.server.console.web.response.Permission;
import xyz.luomu32.config.server.console.web.request.UserPrincipal;
import xyz.luomu32.config.server.console.web.request.UserPojo;

import javax.servlet.http.HttpSession;

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
    private AuthorityService authorityService;

    @Autowired
    private RoleService roleService;

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

        UserPrincipal userPrincipal = new UserPrincipal();
        userPrincipal.setUsername(dbUser.getUsername());
        userPrincipal.setId(dbUser.getId());
        userPrincipal.setRoleId(dbUser.getRole().getId());
        userPrincipal.setRoleName(dbUser.getRole().getName());
        userPrincipal.setPermission(roleService.getPermissions(dbUser.getRole().getId()));
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

    @GetMapping("permissions")
    public Permission menuWithActions() {

        Permission permission = new Permission();
        permission.setMenus(menuRepo.findByFatherIdIsNull());
        permission.setActions(menuActionRepo.findAll());
        return permission;
    }
}
