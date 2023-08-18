package io.snyk.devrel.ctfdaccounthook.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CtfdApiController {

    @GetMapping("/test")
    public String test() {
        return "SUCCESS!";
    }
}
