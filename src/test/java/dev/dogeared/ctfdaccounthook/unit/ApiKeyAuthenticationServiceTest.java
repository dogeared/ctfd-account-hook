package dev.dogeared.ctfdaccounthook.unit;

import dev.dogeared.ctfdaccounthook.model.entity.ApiKey;
import dev.dogeared.ctfdaccounthook.service.ApiKeyAuthenticationServiceImpl;
import dev.dogeared.ctfdaccounthook.service.ApiKeyService;
import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ApiKeyAuthenticationServiceTest {

  @Mock
  private ApiKeyService apiKeyService;

  @Mock
  private HttpServletRequest request;

  @InjectMocks
  private ApiKeyAuthenticationServiceImpl apiKeyAuthenticationService;

  private final String VALID_API_KEY = "validApiKey";
  private final String HASHED_API_KEY = "$2a$12$L0ye9vq/mAarfqjEPkmNFeTWA4suvGisrgiDYbYpdAUyRvN/oh.1G";

  @BeforeEach
  void setUp() throws NoSuchFieldException, IllegalAccessException {
    Field field = ApiKeyAuthenticationServiceImpl.class.getDeclaredField("apiAuthHeaderName");
    field.setAccessible(true);
    field.set(apiKeyAuthenticationService, "api-key-header");
  }

  @Test
  void testMissingApiKey() {
    when(request.getHeader("api-key-header")).thenReturn(null);

    assertThrows(BadCredentialsException.class,
        () -> apiKeyAuthenticationService.getAuthentication(request), "API Key is missing");
  }

  @Test
  void testInvalidOrExpiredApiKey() {
    when(request.getHeader("api-key-header")).thenReturn(VALID_API_KEY);
    when(apiKeyService.validateApiKey(VALID_API_KEY)).thenReturn(null);

    assertThrows(BadCredentialsException.class,
        () -> apiKeyAuthenticationService.getAuthentication(request), "Invalid or expired API Key");
  }

  @Test
  void testInvalidApiKey() {
    when(request.getHeader("api-key-header")).thenReturn("wrongApiKey");
    when(apiKeyService.validateApiKey("wrongApiKey"))
          .thenThrow(new BadCredentialsException("Invalid API Key"));

    assertThrows(BadCredentialsException.class,
        () -> apiKeyAuthenticationService.getAuthentication(request), "Invalid API Key");
  }

  @Test
  void testValidApiKey() {
    ApiKey apiKey = new ApiKey();
    apiKey.setHashedKey(HASHED_API_KEY);

    when(request.getHeader("api-key-header")).thenReturn(VALID_API_KEY);
    when(apiKeyService.validateApiKey(VALID_API_KEY)).thenReturn(apiKey);

    Authentication authentication = apiKeyAuthenticationService.getAuthentication(request);

    assertNotNull(authentication);
    assertTrue(authentication.isAuthenticated());
    assertEquals(VALID_API_KEY, authentication.getPrincipal());
  }
}
