package xyz.luomu32.config.server.console.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.luomu32.config.server.console.entity.DeleteFlagEnum;
import xyz.luomu32.config.server.console.entity.User;
import xyz.luomu32.config.server.console.web.exception.ServiceException;
import xyz.luomu32.config.server.console.web.exception.ServiceExceptionEnum;
import xyz.luomu32.config.server.console.web.interceptor.AuthenticationInterceptor;
import xyz.luomu32.config.server.console.pojo.UserPrincipal;
import xyz.luomu32.config.server.console.pojo.UserPojo;
import xyz.luomu32.config.server.console.repo.UserRepo;

import javax.servlet.http.HttpSession;

@RestController
public class AuthController {

    @Autowired
    private UserRepo userRepo;

    @PostMapping("auth")
    public void auth(@Validated(UserPojo.AuthValid.class) UserPojo user, HttpSession session) {

        //get user from anywhere

        User dbUser = userRepo.findByUsernameAndDeleted(user.getName(), DeleteFlagEnum.UN_DELETED)
                .orElseThrow(() -> new ServiceException(ServiceExceptionEnum.USER_NOT_FOUND));
        if (!dbUser.getPassword().equals(user.getPassword()))
            throw new ServiceException(ServiceExceptionEnum.USER_PASSWORD_NOT_MATCH);


        UserPrincipal userPrincipal = new UserPrincipal();
        userPrincipal.setUsername(dbUser.getUsername());
        userPrincipal.setId(dbUser.getId());
        userPrincipal.setRoleId(dbUser.getRole().getId());
        userPrincipal.setRoleName(dbUser.getRole().getName());
        session.setAttribute(AuthenticationInterceptor.USER_HOLDER, userPrincipal);
    }

    @PostMapping("sing-out")
    public void signOut(HttpSession session) {

        session.removeAttribute(AuthenticationInterceptor.USER_HOLDER);
    }

}
