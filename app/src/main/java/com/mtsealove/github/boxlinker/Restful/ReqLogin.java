package com.mtsealove.github.boxlinker.Restful;

public class ReqLogin {
    String Phone, Password, Token;

    public ReqLogin(String phone, String password, String token) {
        Phone = phone;
        Password = password;
        Token = token;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }
}
