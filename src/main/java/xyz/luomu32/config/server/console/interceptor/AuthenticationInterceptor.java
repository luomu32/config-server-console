package xyz.luomu32.config.server.console.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import xyz.luomu32.config.server.console.exception.UserNotAuthenticationException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 认证。检查用户是否登录
 */

public class AuthenticationInterceptor extends HandlerInterceptorAdapter {


    public static final String USER_HOLDER = "current_user";

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (request.getSession().getAttribute(USER_HOLDER) == null) {
            LOGGER.info("it's need be authentication for this resource:{}", request.getRequestURI());
            throw new UserNotAuthenticationException();
        }

        return true;
    }
}
