package dev.dogeared.ctfdaccounthook.controller;

import dev.dogeared.ctfdaccounthook.Exception.CtfdApiException;
import dev.dogeared.ctfdaccounthook.annotation.LogExecutionTime;
import dev.dogeared.ctfdaccounthook.model.CtfdApiErrorResponse;
import dev.dogeared.ctfdaccounthook.model.CtfdCreateUserRequest;
import dev.dogeared.ctfdaccounthook.model.CtfdResponse;
import dev.dogeared.ctfdaccounthook.model.CtfdUpdateAndEmailResponse;
import dev.dogeared.ctfdaccounthook.model.CtfdUser;
import dev.dogeared.ctfdaccounthook.model.CtfdUserPaginatedResponse;
import dev.dogeared.ctfdaccounthook.service.AliasService;
import dev.dogeared.ctfdaccounthook.service.CtfdApiService;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CtfdApiController {

    @Value("#{ @environment['alias.retries'] }")
    private Integer aliasRetries;
    private final CtfdApiService ctfdApiService;
    private final AliasService aliasService;

    private static final Logger log = LoggerFactory.getLogger(CtfdApiController.class);

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
                return ctfdApiService.createUser(req, alias);
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

    @GetMapping("/api/v1/users")
    public CtfdResponse getUsersByAffiliation(
        @RequestParam(required = false) String affiliation, @RequestParam(required = false) Integer page,
        HttpServletResponse res
    ) {
        try {
            return ctfdApiService.getUsersByAffiliation(affiliation, page);
        } catch (CtfdApiException e) {
            res.setStatus(HttpStatus.BAD_REQUEST.value());
            return e.getCtfdApiError();
        }
    }

    @PostMapping("/api/v1/update-and-email/{affiliation}")
    @LogExecutionTime
    public CtfdResponse updateAndEmailUsers(@PathVariable String affiliation, HttpServletResponse res) {
        Integer page = 1;
        int processed = 0;
        do {
            try {
                CtfdUserPaginatedResponse ctfdUserResponse =
                    ctfdApiService.getUsersByAffiliation(affiliation, page);
                for (CtfdUser ctfdUser : ctfdUserResponse.getData()) {
                    log.debug("Processing user id: {}, name: {}", ctfdUser.getId(), ctfdUser.getName());
                    ctfdUser = ctfdApiService.updatePassword(ctfdUser);
                    log.debug("Password updated for user id: {}", ctfdUser.getId());
                    ctfdApiService.emailUser(ctfdUser);
                    log.debug("Email sent for user id: {}", ctfdUser.getId());
                }
                page = ctfdUserResponse.getMeta().getPagination().getNext();
                processed += ctfdUserResponse.getData().length;
            } catch (CtfdApiException e) {
                res.setStatus(HttpStatus.BAD_REQUEST.value());
                return e.getCtfdApiError();
            }
        } while (page != null);
        return new CtfdUpdateAndEmailResponse(processed);
    }
}
