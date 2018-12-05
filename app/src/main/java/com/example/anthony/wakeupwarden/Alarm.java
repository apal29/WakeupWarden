package com.example.anthony.wakeupwarden;

public class Alarm {
    private String id;
    private String title;
    private Boolean enable;
    //Note: time is set as a string
    private String time;

    //set constructor
    public Alarm(String id, String title, String time, Boolean enable) {
        this.id = id;
        this.time = time;
        this.title = title;
        this.enable = enable;


    }
    // set get and set Alarm functions
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


