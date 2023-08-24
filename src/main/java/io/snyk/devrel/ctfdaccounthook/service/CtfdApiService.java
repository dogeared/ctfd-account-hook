package io.snyk.devrel.ctfdaccounthook.service;

import io.snyk.devrel.ctfdaccounthook.Exception.CtfdApiException;
import io.snyk.devrel.ctfdaccounthook.model.CtfdCreateUserRequest;
import io.snyk.devrel.ctfdaccounthook.model.CtfdUserResponse;
import io.snyk.devrel.ctfdaccounthook.model.CtfdUser;
import io.snyk.devrel.ctfdaccounthook.model.CtfdUserPaginatedResponse;

public interface CtfdApiService {
    CtfdUserResponse createUser(CtfdCreateUserRequest req, String alias) throws CtfdApiException;
    CtfdUserPaginatedResponse getUsersByAffiliation(String affiliation, Integer page);
    CtfdUserResponse updateUser(CtfdUser ctfdUser);
    CtfdUser updatePassword(CtfdUser ctfdUser);
    CtfdUserResponse emailUser(CtfdUser ctfdUser);
}
