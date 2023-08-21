package io.snyk.devrel.ctfdaccounthook.controller;

import io.snyk.devrel.ctfdaccounthook.Exception.CtfdApiException;
import io.snyk.devrel.ctfdaccounthook.model.CtfdApiErrorResponse;
import io.snyk.devrel.ctfdaccounthook.model.CtfdCreateUserRequest;
import io.snyk.devrel.ctfdaccounthook.model.CtfdResponse;
import io.snyk.devrel.ctfdaccounthook.service.AliasService;
import io.snyk.devrel.ctfdaccounthook.service.CtfdApiService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CtfdApiController {

    @Value("#{ @environment['alias.retries'] }")
    private int aliasRetries;
    private final CtfdApiService ctfdApiService;
    private final AliasService aliasService;

    public CtfdApiController(CtfdApiService ctfdApiService, AliasService aliasService) {
        this.ctfdApiService = ctfdApiService;
        this.aliasService = aliasService;

    }

    @PostMapping("/api/v1/users")
    public CtfdResponse createUser(@RequestBody CtfdCreateUserRequest req, HttpServletResponse res) {
        int retryNum = 0;
        do {
            String alias = aliasService.getAlias();
            try {
                return ctfdApiService.createUser(req.getEmail(), alias);
            } catch (CtfdApiException e) {
                // check to see if the issue is NAME or EMAIL related
                // if name, try again
                if (e.getCtfdApiError().getErrors().getEmail() != null) {
                    res.setStatus(HttpStatus.BAD_REQUEST.value());
                    return e.getCtfdApiError();
                }
            }
        } while (retryNum++ < aliasRetries);
        res.setStatus(HttpStatus.BAD_REQUEST.value());
        CtfdApiErrorResponse ctfdApiErrorResponse = new CtfdApiErrorResponse();
        ctfdApiErrorResponse.getErrors().setName(
            new String[]{"Attempted to find a unique alias " + (aliasRetries+1) + " times and failed."}
        );
        return ctfdApiErrorResponse;
    }
}
