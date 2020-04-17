package xyz.luomu32.config.server.console.web.interceptor;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import xyz.luomu32.config.server.console.entity.Menu;
import xyz.luomu32.config.server.console.entity.MenuAction;
import xyz.luomu32.config.server.console.entity.PermissionHttpMethod;
import xyz.luomu32.config.server.console.repo.MenuActionRepo;
import xyz.luomu32.config.server.console.repo.MenuRepo;
import xyz.luomu32.config.server.console.web.exception.UserAccessDenyException;
import xyz.luomu32.config.server.console.web.request.UserPrincipal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
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

        Set<UrlAndMethod> protectedResources = new HashSet<>();
        loadProtectedUrlFromMenu(protectedResources, menuRepo.findByFatherIdIsNull()
                .stream().collect(Collectors.toSet()));

        protectedResources.addAll(menuActionRepo.findAll()
                .stream()
                .map(action ->
                        new UrlAndMethod(action.getUrl(), action.getHttpMethod().name())
                )
                .collect(Collectors.toSet()));

        //check resource is need to protect
        if (!protectedResources.contains(new UrlAndMethod(pattern, request.getMethod().toUpperCase()))) {
            return true;
        }

        Set<UrlAndMethod> menuUrls = new HashSet<>();
        loadProtectedUrlFromMenu(menuUrls, user.getPermission().getMenus()
                .stream().collect(Collectors.toSet()));

        if (menuUrls.stream().filter(m -> m.getUrl().equalsIgnoreCase(pattern)).count() != 0)
            return true;

        if (user.getPermission().getActions().stream().filter(a -> {
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

    private void loadProtectedUrlFromMenu(Set<UrlAndMethod> urls, Set<Menu> menus) {

        for (Menu menu : menus) {
            if (null == menu)
                continue;

            if (menu.getChildren().isEmpty()) {
                if (StringUtils.isEmpty(menu.getServerUrl()))
                    continue;

                urls.add(new UrlAndMethod(menu.getServerUrl(), "GET"));
            } else {
                loadProtectedUrlFromMenu(urls, menu.getChildren());
            }
        }
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
