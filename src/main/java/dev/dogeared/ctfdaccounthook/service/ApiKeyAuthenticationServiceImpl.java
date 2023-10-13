package dev.dogeared.ctfdaccounthook.service;

import dev.dogeared.ctfdaccounthook.model.entity.ApiKey;
import dev.dogeared.ctfdaccounthook.model.ApiKeyAuthentication;
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

  private final ApiKeyService apiKeyService;

  public ApiKeyAuthenticationServiceImpl(ApiKeyService apiKeyService) {
    this.apiKeyService = apiKeyService;
  }

  @Override
  public Authentication getAuthentication(HttpServletRequest req) {
    String apiKey = req.getHeader(apiAuthHeaderName);

    if (apiKey == null) {
      throw new BadCredentialsException("API Key is missing");
    }

    ApiKey storedKey = apiKeyService.validateApiKey(apiKey);
    if (storedKey == null) {
      throw new BadCredentialsException("Invalid or expired API Key");
    }

    return new ApiKeyAuthentication(apiKey, AuthorityUtils.NO_AUTHORITIES);
  }
}
