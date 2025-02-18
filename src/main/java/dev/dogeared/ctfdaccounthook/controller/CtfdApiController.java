package dev.dogeared.ctfdaccounthook.controller;

import dev.dogeared.ctfdaccounthook.Exception.CtfdApiException;
import dev.dogeared.ctfdaccounthook.model.CtfdApiErrorResponse;
import dev.dogeared.ctfdaccounthook.model.CtfdCreateUserRequest;
import dev.dogeared.ctfdaccounthook.model.CtfdResponse;
import dev.dogeared.ctfdaccounthook.model.CtfdUser;
import dev.dogeared.ctfdaccounthook.model.CtfdUserPaginatedResponse;
import dev.dogeared.ctfdaccounthook.model.CtfdUserResponse;
import dev.dogeared.ctfdaccounthook.service.AliasService;
import dev.dogeared.ctfdaccounthook.service.CtfdApiService;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.text.MessageFormat;
import java.util.Optional;

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
                    log.debug("createUser: email already used: {}", req.getEmail());
                    res.setStatus(HttpStatus.BAD_REQUEST.value());
                    return e.getCtfdApiError();
                }
            }
        } while (retryNum++ < aliasRetries);
        String msg = MessageFormat.format("createUser: Attempted to find a unique alias {0} times for email: {1} and failed.", aliasRetries+1, req.getEmail());
        log.debug(msg);
        res.setStatus(HttpStatus.BAD_REQUEST.value());
        CtfdApiErrorResponse ctfdApiErrorResponse = new CtfdApiErrorResponse();
        ctfdApiErrorResponse.getErrors().setName(new String[]{msg});
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
    public SseEmitter updateAndEmailUsers(
        @PathVariable String affiliation,
        @RequestParam(required = false) Optional<String> newAffiliation,
        HttpServletResponse res
    ) {
        // TODO - should probs be another env var setting
        SseEmitter emitter = new SseEmitter(1000*60*60*24L);
        ctfdApiService.emitterHeartBeat(emitter);
        ctfdApiService.updateAndEmail(emitter, affiliation, newAffiliation);
        return emitter;
    }

    @PostMapping("/api/v1/email-creds/{name}")
    public CtfdResponse emailCreds(
        @PathVariable String name, @RequestParam(required = false) boolean showCreds, HttpServletResponse res
    ) {
        try {
            CtfdUser user = ctfdApiService.getUserByName(name);
            user = ctfdApiService.updatePassword(user);
            CtfdUserResponse ret = ctfdApiService.emailUser(user);
            if (showCreds) {
                ret.setUser(user);
            }
            return ret;
        } catch (CtfdApiException e) {
            res.setStatus(HttpStatus.BAD_REQUEST.value());
            return e.getCtfdApiError();
        }
    }
}
