package io.snyk.devrel.ctfdaccounthook.model;

public class CtfdCreateUserRequest {

    private String email;
    private Boolean notify = false;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getNotify() {
        return notify;
    }

    public void setNotify(Boolean notify) {
        this.notify = notify;
    }
}
