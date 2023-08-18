package io.snyk.devrel.ctfdaccounthook.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

public interface ApiKeyAuthenticationService {
    Authentication getAuthentication(HttpServletRequest req);
}
