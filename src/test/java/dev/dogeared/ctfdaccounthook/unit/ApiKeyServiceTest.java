package dev.dogeared.ctfdaccounthook.unit;

import dev.dogeared.ctfdaccounthook.model.ApiKey;
import dev.dogeared.ctfdaccounthook.repository.ApiKeyRepository;
import dev.dogeared.ctfdaccounthook.service.ApiKeyServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApiKeyServiceTest {

  @Mock
  private ApiKeyRepository apiKeyRepository;

  @InjectMocks
  private ApiKeyServiceImpl apiKeyService;

  @BeforeEach
  void setUp() {
  }

  @Test
  void testGenerateApiKey() {
    ApiKey savedApiKey = new ApiKey();
    when(apiKeyRepository.save(any(ApiKey.class))).thenReturn(savedApiKey);

    ApiKey result = apiKeyService.generateApiKey(30);

    assertEquals(savedApiKey, result);
  }

  @Test
  void testValidateValidApiKey() {
    ApiKey storedApiKey = new ApiKey();
    storedApiKey.setExpirationDate(new Date(System.currentTimeMillis() + 1000000)); // Future date
    storedApiKey.setIsRevoked(false);

    when(apiKeyRepository.findByHashedKey(anyString())).thenReturn(storedApiKey);

    ApiKey result = apiKeyService.validateApiKey("someHashedKey");

    assertEquals(storedApiKey, result);
  }

  @Test
  void testValidateExpiredApiKey() {
    ApiKey storedApiKey = new ApiKey();
    storedApiKey.setExpirationDate(new Date(System.currentTimeMillis() - 1000000)); // Past date
    storedApiKey.setIsRevoked(false);

    when(apiKeyRepository.findByHashedKey(anyString())).thenReturn(storedApiKey);

    assertThrows(BadCredentialsException.class,
        () -> apiKeyService.validateApiKey("someHashedKey"));
  }

  @Test
  void testValidateRevokedApiKey() {
    ApiKey storedApiKey = new ApiKey();
    storedApiKey.setExpirationDate(new Date(System.currentTimeMillis() + 1000000)); // Future date
    storedApiKey.setIsRevoked(true);

    when(apiKeyRepository.findByHashedKey(anyString())).thenReturn(storedApiKey);

    assertThrows(BadCredentialsException.class,
        () -> apiKeyService.validateApiKey("someHashedKey"));
  }

  @Test
  void testValidateNonExistentApiKey() {
    when(apiKeyRepository.findByHashedKey(anyString())).thenReturn(null);

    assertThrows(BadCredentialsException.class,
        () -> apiKeyService.validateApiKey("someHashedKey"));
  }
}