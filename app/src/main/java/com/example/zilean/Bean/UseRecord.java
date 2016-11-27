package com.example.zilean.Bean;

/**
 * Created by 王跃_ on 2016/11/25.
 */

public class UseRecord {
    private int id;
    private String user;
    private int time;
    private String date;
    private String begin;
    private String end;
    private int state;
    private String cancelReason;

    public UseRecord() {
    }

    public UseRecord(int id, String user, int time, String date, String begin, String end, int state, String cancelReason) {
        this.id = id;
        this.user = user;
        this.time = time;
        this.date = date;
        this.begin = begin;
        this.end = end;
        this.state = state;
        this.cancelReason = cancelReason;
    }

    @Override
    public String toString() {
        return "UseRecord{" +
                "id=" + id +
                ", user='" + user + '\'' +
                ", time=" + time +
                ", date='" + date + '\'' +
                ", begin='" + begin + '\'' +
                ", end='" + end + '\'' +
                ", state=" + state +
                ", cancelReason='" + cancelReason + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBegin() {
        return begin;
    }

    public void setBegin(String begin) {
        this.begin = begin;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }
}
