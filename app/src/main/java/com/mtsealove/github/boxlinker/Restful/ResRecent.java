package com.mtsealove.github.boxlinker.Restful;

public class ResRecent {
    int Send, Receive;

    public ResRecent(int send, int receive) {
        Send = send;
        Receive = receive;
    }

    public int getSend() {
        return Send;
    }

    public void setSend(int send) {
        Send = send;
    }

    public int getReceive() {
        return Receive;
    }

    public void setReceive(int receive) {
        Receive = receive;
    }
}
