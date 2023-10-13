package dev.dogeared.ctfdaccounthook.repository;

import dev.dogeared.ctfdaccounthook.model.entity.ApiKey;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiKeyRepository extends JpaRepository<ApiKey, UUID> {

  ApiKey findByHashedKey(String hashedKey);
}
