package io.snyk.devrel.ctfdaccounthook.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CtfdCreateUserResponse implements CtfdResponse {

    @JsonProperty("data")
    private CtfdUser user;
    private String success;

    public CtfdUser getUser() {
        return user;
    }

    public void setUser(CtfdUser user) {
        this.user = user;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }
}
