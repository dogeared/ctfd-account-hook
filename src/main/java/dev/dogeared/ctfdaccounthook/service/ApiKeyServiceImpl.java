package dev.dogeared.ctfdaccounthook.service;

import dev.dogeared.ctfdaccounthook.model.ApiKey;
import dev.dogeared.ctfdaccounthook.repository.ApiKeyRepository;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ApiKeyServiceImpl implements ApiKeyService {

  private final ApiKeyRepository apiKeyRepository;
  private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

  public ApiKeyServiceImpl(ApiKeyRepository apiKeyRepository) {
    this.apiKeyRepository = apiKeyRepository;
  }

  @Override
  public ApiKey generateApiKey(int expirationDays) {
    String rawApiKey = generateRawApiKey();
    return generateApiKey(rawApiKey, expirationDays);
  }

  @Override
  public ApiKey generateApiKey(String rawApiKey, int expirationDays) {
    ApiKey apiKey = new ApiKey();
    apiKey.setId(UUID.randomUUID());
    apiKey.setHashedKey(hashAndSalt(rawApiKey));
    apiKey.setExpirationDate(calculateExpirationDate(expirationDays));
    apiKey.setIsRevoked(false);
    return apiKeyRepository.save(apiKey);
  }

  @Override
  public ApiKey validateApiKey(String rawApiKey) {
    // TODO - as more API Keys are added, this could get slow
    for (ApiKey apiKey : apiKeyRepository.findAll()) {
      if (encoder.matches(rawApiKey, apiKey.getHashedKey())) {
        if (apiKey.getExpirationDate().before(new Date()) || apiKey.isRevoked()) {
          throw new BadCredentialsException("Invalid or expired API Key");
        } else {
          return apiKey;
        }
      }
    }
    throw new BadCredentialsException("Invalid or expired API Key");
  }

  private String generateRawApiKey() {
    return UUID.randomUUID().toString().replace("-", "");
  }

  private String hashAndSalt(String rawApiKey) {
    return encoder.encode(rawApiKey);
  }

  private Date calculateExpirationDate(int days) {
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.DAY_OF_MONTH, days);
    return calendar.getTime();
  }
}
