package xyz.luomu32.config.server.console.web.expansion;

import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

import java.util.Locale;

public class YamlViewResolver implements ViewResolver {
    @Override
    public View resolveViewName(String viewName, Locale locale) throws Exception {
        return new YamlDownLoadView(viewName);
    }
}
