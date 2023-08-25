package dev.dogeared.ctfdaccounthook.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

public class CtfdApiErrorResponse implements CtfdResponse {
    private Errors errors = new Errors();

    public Errors getErrors() {
        return errors;
    }

    public void setErrors(Errors errors) {
        this.errors = errors;
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Errors {
        private String[] email;
        private String[] name;

        private String message;

        public String[] getEmail() {
            return email;
        }

        public void setEmail(String[] email) {
            this.email = email;
        }

        public String[] getName() {
            return name;
        }

        public void setName(String[] name) {
            this.name = name;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
