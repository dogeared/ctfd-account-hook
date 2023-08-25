package dev.dogeared.ctfdaccounthook.service;

import dev.dogeared.ctfdaccounthook.Exception.CtfdApiException;
import dev.dogeared.ctfdaccounthook.model.CtfdCreateUserRequest;
import dev.dogeared.ctfdaccounthook.model.CtfdUser;
import dev.dogeared.ctfdaccounthook.model.CtfdUserResponse;
import dev.dogeared.ctfdaccounthook.model.CtfdUserPaginatedResponse;

public interface CtfdApiService {
    CtfdUserResponse createUser(CtfdCreateUserRequest req, String alias) throws CtfdApiException;
    CtfdUserPaginatedResponse getUsersByAffiliation(String affiliation, Integer page);
    CtfdUserResponse updateUser(CtfdUser ctfdUser);
    CtfdUser updatePassword(CtfdUser ctfdUser);
    CtfdUserResponse emailUser(CtfdUser ctfdUser);
}
