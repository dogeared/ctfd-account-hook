package dev.dogeared.ctfdaccounthook.service;

import dev.dogeared.ctfdaccounthook.Exception.CtfdApiException;
import dev.dogeared.ctfdaccounthook.model.CtfdCreateUserRequest;
import dev.dogeared.ctfdaccounthook.model.CtfdUser;
import dev.dogeared.ctfdaccounthook.model.CtfdUserResponse;
import dev.dogeared.ctfdaccounthook.model.CtfdUserPaginatedResponse;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface CtfdApiService {
    CtfdUserResponse createUser(CtfdCreateUserRequest req, String alias) throws CtfdApiException;
    CtfdUserPaginatedResponse getUsersByAffiliation(String affiliation, Integer page);

    CtfdUser getUserByName(String name);

    void updateAndEmail(SseEmitter emitter, String affiliation);

    @Async
        // TODO - gross - heroku workaround

    void emitterHeartBeat(SseEmitter emitter);

    CtfdUserResponse updateUser(CtfdUser ctfdUser);
    CtfdUser updatePassword(CtfdUser ctfdUser);
    CtfdUserResponse emailUser(CtfdUser ctfdUser);
}
