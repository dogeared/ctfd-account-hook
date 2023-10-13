package dev.dogeared.ctfdaccounthook.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.Date;
import java.util.UUID;

@Entity
public class ApiKey {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;
  private String description;
  private String hashedKey;
  private Date expirationDate;
  private boolean isRevoked;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getHashedKey() {
    return hashedKey;
  }

  public void setHashedKey(String hashedKey) {
    this.hashedKey = hashedKey;
  }

  public Date getExpirationDate() {
    return expirationDate;
  }

  public void setExpirationDate(Date expirationDate) {
    this.expirationDate = expirationDate;
  }

  public boolean isRevoked() {
    return isRevoked;
  }

  public void setIsRevoked(boolean revoked) {
    isRevoked = revoked;
  }
}
