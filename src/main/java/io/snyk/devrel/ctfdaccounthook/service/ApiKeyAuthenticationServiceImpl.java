package io.snyk.devrel.ctfdaccounthook.service;

import io.snyk.devrel.ctfdaccounthook.model.ApiKeyAuthentication;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Service;

@Service
public class ApiKeyAuthenticationServiceImpl implements ApiKeyAuthenticationService {


    @Value("#{ @environment['api.auth.header-name'] }")
    private String apiAuthHeaderName;

    @Value("#{ @environment['api.auth.token'] }")
    private String apiAuthToken;

    @Override
    public Authentication getAuthentication(HttpServletRequest req) {
        String apiKey = req.getHeader(apiAuthHeaderName);
        if (apiKey == null || !apiKey.equals(apiAuthToken)) {
            throw new BadCredentialsException("Invalid API Key");
        }

        return new ApiKeyAuthentication(apiKey, AuthorityUtils.NO_AUTHORITIES);
    }
}
