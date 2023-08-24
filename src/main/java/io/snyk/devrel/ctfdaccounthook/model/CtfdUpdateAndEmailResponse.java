package io.snyk.devrel.ctfdaccounthook.model;

public class CtfdUpdateAndEmailResponse implements CtfdResponse {

    private Integer usersProcessed;

    public CtfdUpdateAndEmailResponse(){}

    public CtfdUpdateAndEmailResponse(Integer usersProcessed) {
        this.usersProcessed = usersProcessed;
    }

    public Integer getUsersProcessed() {
        return usersProcessed;
    }

    public void setUsersProcessed(Integer usersProcessed) {
        this.usersProcessed = usersProcessed;
    }
}
