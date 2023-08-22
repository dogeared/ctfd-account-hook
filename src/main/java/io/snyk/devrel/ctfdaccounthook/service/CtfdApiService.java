package io.snyk.devrel.ctfdaccounthook.service;

import io.snyk.devrel.ctfdaccounthook.Exception.CtfdApiException;
import io.snyk.devrel.ctfdaccounthook.model.CtfdCreateUserRequest;
import io.snyk.devrel.ctfdaccounthook.model.CtfdCreateUserResponse;

public interface CtfdApiService {
    CtfdCreateUserResponse createUser(CtfdCreateUserRequest req, String alias) throws CtfdApiException;
}
