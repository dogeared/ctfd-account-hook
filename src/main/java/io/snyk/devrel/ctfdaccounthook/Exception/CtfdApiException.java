package io.snyk.devrel.ctfdaccounthook.Exception;

import io.snyk.devrel.ctfdaccounthook.model.CtfdApiErrorResponse;

public class CtfdApiException extends RuntimeException {

    private CtfdApiErrorResponse ctfdApiErrorResponse;
    public CtfdApiException(CtfdApiErrorResponse ctfdApiErrorResponse) {
        super();
        this.ctfdApiErrorResponse = ctfdApiErrorResponse;
    }

    public CtfdApiErrorResponse getCtfdApiError() {
        return ctfdApiErrorResponse;
    }
}
