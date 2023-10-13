package dev.dogeared.ctfdaccounthook.unit;

import dev.dogeared.ctfdaccounthook.model.entity.ApiKey;
import dev.dogeared.ctfdaccounthook.repository.ApiKeyRepository;
import dev.dogeared.ctfdaccounthook.service.ApiKeyServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Date;
import java.util.List;

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
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    ApiKey storedApiKey = new ApiKey();
    storedApiKey.setHashedKey(encoder.encode("someHashedKey"));
    storedApiKey.setExpirationDate(new Date(System.currentTimeMillis() + 1000000)); // Future date
    storedApiKey.setIsRevoked(false);

    List<ApiKey> apiKeys = List.of(storedApiKey);
    when(apiKeyRepository.findAll()).thenReturn(apiKeys);

    ApiKey result = apiKeyService.validateApiKey("someHashedKey");

    assertEquals(storedApiKey, result);
  }

  @Test
  void testValidateExpiredApiKey() {
    ApiKey storedApiKey = new ApiKey();
    storedApiKey.setExpirationDate(new Date(System.currentTimeMillis() - 1000000)); // Past date
    storedApiKey.setIsRevoked(false);
    List<ApiKey> apiKeys = List.of(storedApiKey);

    when(apiKeyRepository.findAll()).thenReturn(apiKeys);

    assertThrows(BadCredentialsException.class,
        () -> apiKeyService.validateApiKey("someHashedKey"));
  }

  @Test
  void testValidateRevokedApiKey() {
    ApiKey storedApiKey = new ApiKey();
    storedApiKey.setExpirationDate(new Date(System.currentTimeMillis() + 1000000)); // Future date
    storedApiKey.setIsRevoked(true);
    List<ApiKey> apiKeys = List.of(storedApiKey);

    when(apiKeyRepository.findAll()).thenReturn(apiKeys);

    assertThrows(BadCredentialsException.class,
        () -> apiKeyService.validateApiKey("someHashedKey"));
  }

  @Test
  void testValidateNonExistentApiKey() {
    assertThrows(BadCredentialsException.class,
        () -> apiKeyService.validateApiKey("someHashedKey"));
  }
}