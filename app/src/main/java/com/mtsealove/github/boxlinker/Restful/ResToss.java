package com.mtsealove.github.boxlinker.Restful;

public class ResToss {
    Success success;

    public ResToss(Success success) {
        this.success = success;
    }

    public Success getSuccess() {
        return success;
    }

    public void setSuccess(Success success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "ResToss{" +
                "success=" + success.toString() +
                '}';
    }
}
