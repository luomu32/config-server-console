package xyz.luomu32.config.server.console;

import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.Parser;
import org.springframework.format.Printer;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import xyz.luomu32.config.server.console.web.expansion.PropertiesDownloadViewResolver;
import xyz.luomu32.config.server.console.web.expansion.YamlViewResolver;
import xyz.luomu32.config.server.console.web.interceptor.AuthenticationInterceptor;
import xyz.luomu32.config.server.console.web.interceptor.ResponseStatusInterceptor;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class WebConfig extends WebMvcConfigurationSupport {

    public final static MediaType APPLICATION_PROPERTY = new MediaType("application", "property");

    public final static MediaType APPLICATION_YML = new MediaType("application", "yml");

    @Autowired
    private MessageSource messageSource;

    @Override
    protected void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**");
    }


    @Override
    protected void addFormatters(FormatterRegistry registry) {
        registry.addFormatterForFieldType(
                LocalDate.class,
                (Printer<LocalDate>) (date, Locale) -> date.toString(),
                (Parser<LocalDate>) (text, locale) -> LocalDate.parse(text, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }

    @Override
    protected void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.favorParameter(false);
        configurer.favorPathExtension(true);
        configurer.ignoreAcceptHeader(true);
        configurer.useRegisteredExtensionsOnly(true);
        configurer.mediaType("properties", APPLICATION_PROPERTY);
        configurer.mediaType("yml", APPLICATION_YML);
    }

    //
    @Bean
    ContentNegotiatingViewResolver contentNegotiatingViewResolver(@Autowired ContentNegotiationManager contentNegotiationManager) {
        ContentNegotiatingViewResolver resolver = new ContentNegotiatingViewResolver();
        List<ViewResolver> viewResolvers = new ArrayList<>();
        viewResolvers.add(new PropertiesDownloadViewResolver());
        viewResolvers.add(new YamlViewResolver());
        resolver.setViewResolvers(viewResolvers);
        resolver.setContentNegotiationManager(contentNegotiationManager);
        resolver.setUseNotAcceptableStatusCode(true);
        return resolver;
    }


    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        int pos = 0;
        for (HttpMessageConverter converter : converters) {
            if (converter instanceof MappingJackson2HttpMessageConverter) {
                break;
            }
            pos++;
        }
        converters.remove(pos);

        //DateFormat 对LocalDateTime等无效
        //SerializationFeature.WRITE_DATES_AS_TIMESTAMPS设置后，LocalDateTime会被序列化成[year,month,day,hour,minus,sec]这种的时间戳格式
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder()
                .serializerByType(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .serializerByType(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .dateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        converters.add(new MappingJackson2HttpMessageConverter(builder.build()));
    }

    @Override
    public Validator getValidator() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.setValidationMessageSource(messageSource);
        return validator;
    }

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthenticationInterceptor()).excludePathPatterns("/auth", "/error");
//        registry.addInterceptor(new AuthorizationInterceptor()).excludePathPatterns("/auth");
        registry.addInterceptor(new ResponseStatusInterceptor());
    }

    @Override
    protected void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new PageableHandlerMethodArgumentResolver());
    }
}

