package dev.dogeared.ctfdaccounthook.model;

import java.util.Date;
import java.util.UUID;

public class ApiKey {

  private UUID id;
  private String hashedKey;
  private Date expirationDate;
  private boolean isRevoked;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
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
