package com.appsnipp.loginsamples.models.auth;

public class LoginResponse {
    private String username;
    private boolean success;
    private String error;

    public LoginResponse(String username, boolean success, String error) {
        this.username = username;
        this.success = success;
        this.error = error;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
