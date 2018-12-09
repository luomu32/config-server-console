package xyz.luomu32.config.server.console.web.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@ResponseStatus(UNAUTHORIZED)
public class UserNotAuthenticationException extends RuntimeException {
}
