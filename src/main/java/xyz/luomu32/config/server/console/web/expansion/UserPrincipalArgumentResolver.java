package xyz.luomu32.config.server.console.web.expansion;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import xyz.luomu32.config.server.console.pojo.UserPrincipal;
import xyz.luomu32.config.server.console.web.interceptor.AuthenticationInterceptor;

import javax.servlet.http.HttpSession;

public class UserPrincipalArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(UserPrincipal.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        return ((HttpSession) webRequest.getSessionMutex()).getAttribute(AuthenticationInterceptor.USER_HOLDER);
    }
}
