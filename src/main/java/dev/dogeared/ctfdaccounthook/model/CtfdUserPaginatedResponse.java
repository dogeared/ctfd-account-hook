package dev.dogeared.ctfdaccounthook.model;

public class CtfdUserPaginatedResponse implements CtfdResponse {
    private CtfdUser[] data;
    private CtfdMeta meta;
    private String success;

    public CtfdUser[] getData() {
        return data;
    }

    public void setData(CtfdUser[] data) {
        this.data = data;
    }

    public CtfdMeta getMeta() {
        return meta;
    }

    public void setMeta(CtfdMeta meta) {
        this.meta = meta;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }
}
