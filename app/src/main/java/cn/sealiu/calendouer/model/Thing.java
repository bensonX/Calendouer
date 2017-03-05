package cn.sealiu.calendouer.model;

/**
 * Created by liuyang
 * on 2017/3/5.
 */

public class Thing {
    private String id;
    private String title;
    private String datetime;
    private String notification_datetime;
    private String time_advance;
    private int done;


    public Thing(String id, String title, String datetime, String notification_datetime, String time_advance, int done) {
        this.id = id;
        this.title = title;
        this.datetime = datetime;
        this.notification_datetime = notification_datetime;
        this.time_advance = time_advance;
        this.done = done;
    }

    public Thing(String id, String title, String notification_datetime) {
        this.id = id;
        this.title = title;
        this.notification_datetime = notification_datetime;
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

    public String getTime_advance() {
        return time_advance;
    }

    public void setTime_advance(String time_advance) {
        this.time_advance = time_advance;
    }

    public int getDone() {
        return done;
    }

    public void setDone(int done) {
        this.done = done;
    }
}
