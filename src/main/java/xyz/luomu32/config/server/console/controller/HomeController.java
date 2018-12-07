package xyz.luomu32.config.server.console.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("success")
    public void notRespContent() {

    }


    @ResponseStatus(HttpStatus.FORBIDDEN)
    @GetMapping("forbidden")
    public void notAuen() {

    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @GetMapping("server-error")
    public void serverError(){

    }
}
