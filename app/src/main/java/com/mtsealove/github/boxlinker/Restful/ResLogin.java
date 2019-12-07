package com.mtsealove.github.boxlinker.Restful;

public class ResLogin {
    String Phone, Name;

    public ResLogin(String phone, String name) {
        Phone = phone;
        Name = name;
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
}
