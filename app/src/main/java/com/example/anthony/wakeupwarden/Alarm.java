package com.example.danie.wakeupwarden;


public class Alarm {
    private long id;
    private String title;
    private Boolean enable;
    private String time; //(yyyy-mm-dd hh:mm:ss)

    public Alarm(long id, String title, String time, Boolean enable) {
        this.id = id;
        this.time = time;
        this.title = title;
        this.enable = enable;


    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public String getTitle() {
        return title;
    }

    public Boolean getEnable() {
        return enable;
    }


}
