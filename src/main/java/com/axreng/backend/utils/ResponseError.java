package com.axreng.backend.utils;

public class ResponseError {

    private String status;
    private String message;


    public ResponseError(String status, String message, String... args) {
        this.status = status;
        this.message = String.format(message, args);
    }

    public ResponseError(String status, Exception e) {
        this.status = status;
        this.message = e.getMessage();
    }

    public String getMessage() {
        return this.message;
    }

    public String getStatus() {
        return this.status;
    }

}
