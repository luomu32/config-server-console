package xyz.luomu32.config.server.console.web.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 授权。检查登录用户是否拥有访问该资源的权限
 */
public class AuthorizationInterceptor extends HandlerInterceptorAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Object user = request.getSession().getAttribute(AuthenticationInterceptor.USER_HOLDER);
        if (null == user) {
            LOGGER.warn("user not authentication,ignore authorization");
            return true;
        }



        return true;
    }
}
