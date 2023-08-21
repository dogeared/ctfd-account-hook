package io.snyk.devrel.ctfdaccounthook.service;

import io.snyk.devrel.ctfdaccounthook.Exception.CtfdApiException;
import io.snyk.devrel.ctfdaccounthook.model.CtfdCreateUserResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface CtfdApiService {
    Mono<Map> getChallenges();
    Mono<Map> getUsers();

    CtfdCreateUserResponse createUser(String email, String alias) throws CtfdApiException;
}
