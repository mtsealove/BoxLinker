package com.mtsealove.github.boxlinker.Restful;

public class ResOrderSm {
    String OrderID, Status;

    public ResOrderSm(String orderID, String status) {
        OrderID = orderID;
        Status = status;
    }

    public String getOrderID() {
        return OrderID;
    }

    public void setOrderID(String orderID) {
        OrderID = orderID;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}
