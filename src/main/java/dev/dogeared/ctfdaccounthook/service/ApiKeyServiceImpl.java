package dev.dogeared.ctfdaccounthook.service;

import dev.dogeared.ctfdaccounthook.model.ApiKey;
import dev.dogeared.ctfdaccounthook.repository.ApiKeyRepository;
import java.util.Calendar;
import java.util.Date;
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
  public ApiKey generateApiKey() {
    ApiKey apiKey = new ApiKey();
    apiKey.setId(UUID.randomUUID());
    String rawKey = generateRawApiKey();
    apiKey.setHashedKey(hashAndSalt(rawKey));
    apiKey.setExpirationDate(calculateExpirationDate());
    apiKey.setIsRevoked(false);
    return apiKeyRepository.save(apiKey);
  }

  @Override
  public ApiKey validateApiKey(String hashedKey) {
    ApiKey apiKey = apiKeyRepository.findByHashedKey(hashedKey);
    if (apiKey == null || apiKey.getExpirationDate().before(new Date()) || apiKey.isRevoked()) {
      throw new BadCredentialsException("Invalid or expired API Key");
    }

    return apiKey;
  }

  private String generateRawApiKey() {
    return UUID.randomUUID().toString().replace("-", "");
  }

  private String hashAndSalt(String rawApiKey) {
    return encoder.encode(rawApiKey);
  }

  private Date calculateExpirationDate() {
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.DAY_OF_MONTH, 30); //Adds 30 days to the current date
    return calendar.getTime();
  }
}
