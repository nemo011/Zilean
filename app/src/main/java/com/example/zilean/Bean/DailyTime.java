package com.example.zilean.Bean;

/**
 * Created by 王跃_ on 2016/11/25.
 */

public class DailyTime {
    private int id;
    private String user;
    private int time;
    private String date;

    public DailyTime() {
    }

    public DailyTime(int id, String user, int time, String date) {
        this.id = id;
        this.user = user;
        this.time = time;
        this.date = date;
    }

    @Override
    public String toString() {
        return "DailyTime{" +
                "id=" + id +
                ", user='" + user + '\'' +
                ", time=" + time +
                ", date='" + date + '\'' +
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
}
