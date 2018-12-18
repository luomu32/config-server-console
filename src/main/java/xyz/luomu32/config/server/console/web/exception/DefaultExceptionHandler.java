package xyz.luomu32.config.server.console.web.exception;

import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import xyz.luomu32.config.server.console.pojo.ApiResponse;

import java.util.Locale;

import static org.apache.zookeeper.KeeperException.Code.NODEEXISTS;

@RestControllerAdvice
public class DefaultExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultExceptionHandler.class);

    @Autowired
    private MessageSource messageSource;

    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UserNotAuthenticationException.class)
    public void userNotAuthenticationExceptionHandle(UserNotAuthenticationException e) {

    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ApiResponse bindExceptionHandle(BindException e) {
        return new ApiResponse(e.getFieldError().getDefaultMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ApiResponse methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        String message = messageSource.getMessage("parameter.invalid",
                new Object[]{e.getValue(), e.getRequiredType().getName()},
                LocaleContextHolder.getLocale());
        return new ApiResponse(message);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ApiResponse missingServletRequestParameterException(MissingServletRequestParameterException e) {
        String message = messageSource.getMessage("parameter.missing", new Object[]{e.getParameterName()}, LocaleContextHolder.getLocale());
        return new ApiResponse(message);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler
    public void noHandlerFoundExceptionHandle(NoHandlerFoundException e) {

    }

    @ExceptionHandler
    public ApiResponse serviceExceptionHandle(ServiceException e) {
        String message = messageSource.getMessage(e.getMessage(), e.getParams(), LocaleContextHolder.getLocale());
        return new ApiResponse(e.getCode(), message);
    }

    @ExceptionHandler
    public ApiResponse runtimeExceptionHandle(RuntimeException e) {
        Throwable cause = e.getCause();
        if (null != cause && (cause instanceof KeeperException)) {
            if (((KeeperException) cause).code() == NODEEXISTS) {
                return new ApiResponse("配置已存在");
            }
        }

        LOGGER.error("", e);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage(e.getMessage(), new Object[0], "error", locale);
        return new ApiResponse(message);
    }
}
