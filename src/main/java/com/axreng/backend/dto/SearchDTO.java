package com.axreng.backend.dto;

public class SearchDTO {

    private String keyword;

    public SearchDTO() {
    }

    public SearchDTO(String keyword) {
        this.keyword = keyword;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

}
