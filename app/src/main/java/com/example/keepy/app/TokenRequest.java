package com.example.keepy.app;

public class TokenRequest {
    private String parentId;
    private String token;

    public TokenRequest(String parentId, String token) {
        this.parentId = parentId;
        this.token = token;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}