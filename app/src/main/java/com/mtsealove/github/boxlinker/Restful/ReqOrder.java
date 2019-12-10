package com.mtsealove.github.boxlinker.Restful;

import java.io.Serializable;

public class ReqOrder implements Serializable {
    String MemberID, StdPhone, StdName, StdAddr, DstPhone, DstName, DstAddr, PayMethod, Msg;
    int Size, Price;
    double Weight, Latitude, Longitude;

    public ReqOrder(String memberID, String stdPhone, String stdName, String stdAddr, String dstPhone, String dstName, String dstAddr, String payMethod, int size, double weight, String msg, int price) {
        MemberID = memberID;
        StdPhone = stdPhone;
        StdName = stdName;
        StdAddr = stdAddr;
        DstPhone = dstPhone;
        DstName = dstName;
        DstAddr = dstAddr;
        PayMethod = payMethod;
        Size = size;
        Weight = weight;
        this.Msg = msg;
        this.Price = price;
    }

    public String getMemberID() {
        return MemberID;
    }

    public void setMemberID(String memberID) {
        MemberID = memberID;
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

    public String getMsg() {
        return Msg;
    }

    public void setMsg(String msg) {
        Msg = msg;
    }

    public int getPrice() {
        return Price;
    }

    public void setPrice(int price) {
        Price = price;
    }
}
