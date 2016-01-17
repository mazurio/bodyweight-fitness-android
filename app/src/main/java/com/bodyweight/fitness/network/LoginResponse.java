package com.bodyweight.fitness.network;

public class LoginResponse {
    public boolean login;
    public String identityId;
    public String token;

    public LoginResponse(boolean login, String identityId, String token) {
        this.login = login;
        this.identityId = identityId;
        this.token = token;
    }

    public void setLogin(boolean login) {
        this.login = login;
    }

    public boolean getLogin() {
        return login;
    }

    public void setIdentityId(String identityId) {
        this.identityId = identityId;
    }

    public String getIdentityId() {
        return identityId;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
