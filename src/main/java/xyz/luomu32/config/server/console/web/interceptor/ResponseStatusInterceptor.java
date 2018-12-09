package xyz.luomu32.config.server.console.web.interceptor;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ResponseStatusInterceptor extends HandlerInterceptorAdapter {

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (httpMethod(request.getMethod()) && null == ex) {
            //TODO 并且处理器返回值为VOID。
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        }
    }

    private boolean httpMethod(String method) {
        return method.equalsIgnoreCase("POST") ||
                method.equalsIgnoreCase("PUT") ||
                method.equalsIgnoreCase("DELETE");
    }
}
