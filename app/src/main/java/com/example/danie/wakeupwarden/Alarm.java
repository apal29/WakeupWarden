package com.example.danie.wakeupwarden;

public class Alarm {
    private String id;
    private String title;
    private Boolean enable;
    //time is set as a string
    //can be modified for other types
    private String time;


    public Alarm(String id, String title, String time, Boolean enable) {
        this.id = id;
        this.time = time;
        this.title = title;
        this.enable = enable;


    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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


