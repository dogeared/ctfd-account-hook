package io.snyk.devrel.ctfdaccounthook.Exception;

import io.snyk.devrel.ctfdaccounthook.model.CtfdApiError;

public class CtfdApiException extends RuntimeException {

    private CtfdApiError ctfdApiError;
    public CtfdApiException(CtfdApiError ctfdApiError) {
        super();
        this.ctfdApiError = ctfdApiError;
    }

    public CtfdApiError getCtfdApiError() {
        return ctfdApiError;
    }
}
