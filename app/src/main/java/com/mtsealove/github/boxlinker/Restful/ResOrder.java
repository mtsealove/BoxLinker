package com.mtsealove.github.boxlinker.Restful;

import java.io.Serializable;

public class ResOrder {
    String OrderID, StdPhone, StdName, StdAddr, DstPhone, DstName, DstAddr, PayMethod;
    int Size;
    double Weight;
    double Latitude, Longitude;
    String Message;
    String StatusName;

    public ResOrder(String orderID, String stdPhone, String stdName, String stdAddr, String dstPhone, String dstName, String dstAddr, String payMethod, int size, double weight, double latitude, double longitude, String message, String statusName) {
        OrderID = orderID;
        StdPhone = stdPhone;
        StdName = stdName;
        StdAddr = stdAddr;
        DstPhone = dstPhone;
        DstName = dstName;
        DstAddr = dstAddr;
        PayMethod = payMethod;
        Size = size;
        Weight = weight;
        Latitude = latitude;
        Longitude = longitude;
        Message = message;
        StatusName = statusName;
    }

    public String getOrderID() {
        return OrderID;
    }

    public void setOrderID(String orderID) {
        OrderID = orderID;
    }

    public String getStdPhone() {
        return StdPhone;
    }

    public void setStdPhone(String stdPhone) {
        StdPhone = stdPhone;
    }

    public String getStdName() {
        return StdName;
    }

    public void setStdName(String stdName) {
        StdName = stdName;
    }

    public String getStdAddr() {
        return StdAddr;
    }

    public void setStdAddr(String stdAddr) {
        StdAddr = stdAddr;
    }

    public String getDstPhone() {
        return DstPhone;
    }

    public void setDstPhone(String dstPhone) {
        DstPhone = dstPhone;
    }

    public String getDstName() {
        return DstName;
    }

    public void setDstName(String dstName) {
        DstName = dstName;
    }

    public String getDstAddr() {
        return DstAddr;
    }

    public void setDstAddr(String dstAddr) {
        DstAddr = dstAddr;
    }

    public String getPayMethod() {
        return PayMethod;
    }

    public void setPayMethod(String payMethod) {
        PayMethod = payMethod;
    }

    public int getSize() {
        return Size;
    }

    public void setSize(int size) {
        Size = size;
    }

    public double getWeight() {
        return Weight;
    }

    public void setWeight(double weight) {
        Weight = weight;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getStatusName() {
        return StatusName;
    }

    public void setStatusName(String statusName) {
        StatusName = statusName;
    }

    @Override
    public String toString() {
        return "ResOrder{" +
                "OrderID='" + OrderID + '\'' +
                ", StdPhone='" + StdPhone + '\'' +
                ", StdName='" + StdName + '\'' +
                ", StdAddr='" + StdAddr + '\'' +
                ", DstPhone='" + DstPhone + '\'' +
                ", DstName='" + DstName + '\'' +
                ", DstAddr='" + DstAddr + '\'' +
                ", PayMethod='" + PayMethod + '\'' +
                ", Size=" + Size +
                ", Weight=" + Weight +
                ", Latitude=" + Latitude +
                ", Longitude=" + Longitude +
                ", Message='" + Message + '\'' +
                ", StatusName='" + StatusName + '\'' +
                '}';
    }
}
