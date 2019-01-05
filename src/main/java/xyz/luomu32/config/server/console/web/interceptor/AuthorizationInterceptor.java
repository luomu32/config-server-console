package xyz.luomu32.config.server.console.web.interceptor;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import xyz.luomu32.config.server.console.entity.PermissionHttpMethod;
import xyz.luomu32.config.server.console.repo.MenuActionRepo;
import xyz.luomu32.config.server.console.repo.MenuRepo;
import xyz.luomu32.config.server.console.web.exception.UserAccessDenyException;
import xyz.luomu32.config.server.console.web.request.UserPrincipal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 授权。检查登录用户是否拥有访问该资源的权限
 */
public class AuthorizationInterceptor extends HandlerInterceptorAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationInterceptor.class);


    private final MenuRepo menuRepo;
    private final MenuActionRepo menuActionRepo;

    public AuthorizationInterceptor(MenuRepo menuRepo, MenuActionRepo menuActionRepo) {
        this.menuRepo = menuRepo;
        this.menuActionRepo = menuActionRepo;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        UserPrincipal user = (UserPrincipal) request.getSession().getAttribute(AuthenticationInterceptor.USER_HOLDER);
        if (null == user) {
            LOGGER.warn("user not authentication,ignore authorization");
            return true;
        }
        String pattern = request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE).toString();

        List<UrlAndMethod> accessControlResources = menuRepo.findAll()
                .stream()
                .filter(menu -> menu.getServerUrl() != null)
                .map(menu -> new UrlAndMethod(menu.getServerUrl(), "GET")).collect(Collectors.toList());
        
        accessControlResources.addAll(menuActionRepo.findAll()
                .stream()
                .map(action ->
                        new UrlAndMethod(action.getUrl(), action.getHttpMethod().name())
                )
                .collect(Collectors.toList()));

        if (!accessControlResources.contains(new UrlAndMethod(pattern, request.getMethod().toUpperCase()))) {
            //this resource do not need access control
            return true;
        }


        if (user.getMenus().stream().filter(m ->
                pattern.equalsIgnoreCase(m.getServerUrl())
        ).count() != 0)
            return true;

        if (user.getActions().stream().filter(a -> {
            if (!a.getUrl().equalsIgnoreCase(pattern))
                return false;
            if (a.getHttpMethod() == PermissionHttpMethod.ALL)
                return true;
            return request.getMethod().equalsIgnoreCase(a.getHttpMethod().name());
        }).count() == 0) {
            throw new UserAccessDenyException();
        }

        return true;
    }

    @Data
    private class UrlAndMethod {
        private String url;
        private String method;

        public UrlAndMethod(String url, String method) {
            this.url = url;
            this.method = method;
        }
    }
}
