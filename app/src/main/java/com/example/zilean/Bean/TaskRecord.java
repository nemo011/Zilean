package com.example.zilean.Bean;

/**
 * Created by 王跃_ on 2016/11/25.
 */

public class TaskRecord {
    private int id;
    private String user;
    private String time;
    private String date;
    private String content;
    private int state;

    public TaskRecord() {
    }

    public TaskRecord(int id, String user, String time, String date, String content, int state) {
        this.id = id;
        this.user = user;
        this.time = time;
        this.date = date;
        this.content = content;
        this.state = state;
    }

    @Override
    public String toString() {
        return "TaskRecord{" +
                "id=" + id +
                ", user='" + user + '\'' +
                ", time='" + time + '\'' +
                ", date='" + date + '\'' +
                ", content='" + content + '\'' +
                ", state=" + state +
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
