package xyz.luomu32.config.server.console.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.luomu32.config.server.console.entity.User;
import xyz.luomu32.config.server.console.web.interceptor.AuthenticationInterceptor;
import xyz.luomu32.config.server.console.pojo.LoginedUser;
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

        User dbUser = userRepo.findByUsername(user.getName())
                .orElseThrow(() -> new RuntimeException("user.not.found"));
        if (!dbUser.getPassword().equals(user.getPassword()))
            throw new RuntimeException("user.password.not.match");


        LoginedUser loginedUser = new LoginedUser();
        loginedUser.setUsername(dbUser.getUsername());
        loginedUser.setId(dbUser.getId());
        loginedUser.setRoleId(dbUser.getRole().getId());
        loginedUser.setRoleName(dbUser.getRole().getName());
        session.setAttribute(AuthenticationInterceptor.USER_HOLDER, loginedUser);
    }

    @PostMapping("sing-out")
    public void signOut(HttpSession session) {

        session.removeAttribute(AuthenticationInterceptor.USER_HOLDER);
    }

}
