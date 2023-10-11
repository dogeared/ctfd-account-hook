package dev.dogeared.ctfdaccounthook.service;

import dev.dogeared.ctfdaccounthook.model.ApiKey;

public interface ApiKeyService {

  ApiKey generateApiKey(int expirationDays);
  ApiKey generateApiKey(String rawApiKey, int expirationDays);
  ApiKey validateApiKey(String hashedKey);
}
