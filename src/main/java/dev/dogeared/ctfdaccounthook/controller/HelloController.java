package dev.dogeared.ctfdaccounthook.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello-world")
    public String test() {
        return "HELLO WORLD!";
    }
}
