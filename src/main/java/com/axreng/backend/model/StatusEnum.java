package com.axreng.backend.model;

public enum StatusEnum {

    ACTIVE("active"),
    DONE("done");

    private final String text;

    StatusEnum(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

}
