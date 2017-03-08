package cn.sealiu.calendouer.model;

import java.io.Serializable;

/**
 * Created by liuyang
 * on 2017/3/5.
 */

public class Thing implements Serializable {
    private String id;
    private String title;
    private String datetime;
    private String notification_datetime;
    private int time_advance;
    private int done;
    private int request_code;


    public Thing(String id, String title, String datetime, String notification_datetime,
                 int time_advance, int done, int request_code) {
        this.id = id;
        this.title = title;
        this.datetime = datetime;
        this.notification_datetime = notification_datetime;
        this.time_advance = time_advance;
        this.done = done;
        this.request_code = request_code;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getNotification_datetime() {
        return notification_datetime;
    }

    public void setNotification_datetime(String notification_datetime) {
        this.notification_datetime = notification_datetime;
    }

    public int getTime_advance() {
        return time_advance;
    }

    public void setTime_advance(int time_advance) {
        this.time_advance = time_advance;
    }

    public int getDone() {
        return done;
    }

    public void setDone(int done) {
        this.done = done;
    }

    public int getRequest_code() {
        return request_code;
    }

    public void setRequest_code(int request_code) {
        this.request_code = request_code;
    }
}
