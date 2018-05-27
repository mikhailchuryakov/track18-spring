package ru.track.prefork;

import java.io.Serializable;

public class Message implements Serializable {
    private long ts;
    private String data;

    public Message(long ts, String data) {
        this.ts = ts;
        this.data = data;
    }

    public long getTs() {
        return ts;
    }

    public String getData() {
        return data;

    }

    public void setData(String data) {
        this.data = data;
    }
}
