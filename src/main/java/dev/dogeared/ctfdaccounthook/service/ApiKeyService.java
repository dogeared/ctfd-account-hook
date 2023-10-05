package dev.dogeared.ctfdaccounthook.service;

import dev.dogeared.ctfdaccounthook.model.ApiKey;

public interface ApiKeyService {

  ApiKey generateApiKey();
  ApiKey validateApiKey(String hashedKey);
}
