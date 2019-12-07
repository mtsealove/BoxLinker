package com.mtsealove.github.boxlinker.Restful;

public class ReqSignUp {
    String Phone, Name, Password;

    public ReqSignUp(String phone, String name, String password) {
        Phone = phone;
        Name = name;
        Password = password;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}
