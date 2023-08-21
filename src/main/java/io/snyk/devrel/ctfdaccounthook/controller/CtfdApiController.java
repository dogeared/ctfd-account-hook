package io.snyk.devrel.ctfdaccounthook.controller;

import io.snyk.devrel.ctfdaccounthook.Exception.CtfdApiException;
import io.snyk.devrel.ctfdaccounthook.model.CreateCtfdUserRequest;
import io.snyk.devrel.ctfdaccounthook.service.AliasService;
import io.snyk.devrel.ctfdaccounthook.service.CtfdApiService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
public class CtfdApiController {
    private final CtfdApiService ctfdApiService;
    private final AliasService aliasService;

    public CtfdApiController(CtfdApiService ctfdApiService, AliasService aliasService) {
        this.ctfdApiService = ctfdApiService;
        this.aliasService = aliasService;

    }

    @GetMapping("/api/v1/challenges")
    public Mono<Map> getChallenges() {
        return ctfdApiService.getChallenges();
    }

    @GetMapping("/api/v1/users")
    public Mono<Map> getUsers() {
        return ctfdApiService.getUsers();
    }

    @PostMapping("/api/v1/users")
    public Object createUser(@RequestBody CreateCtfdUserRequest req, HttpServletResponse res) {
        String alias = aliasService.getAlias();
        try {
            return ctfdApiService.createUser(req.getEmail(), alias);
        } catch (CtfdApiException exception) {
            res.setStatus(HttpStatus.BAD_REQUEST.value());
            return exception.getCtfdApiError();
        }
    }
}
