package dev.dogeared.ctfdaccounthook.repository;

import dev.dogeared.ctfdaccounthook.model.ApiKey;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiKeyRepository extends JpaRepository<ApiKey, UUID> {

  ApiKey findByHashedKey(String hashedKey);
}
