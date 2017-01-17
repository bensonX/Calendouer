package cn.sealiu.calendouer.bean;

/**
 * Created by liuyang
 * on 2017/1/15.
 */

public class Top250Bean {
    private int count;
    private int start;
    private int total;
    private String title;
    private MovieBaseBean[] subjects;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public MovieBaseBean[] getSubjects() {
        return subjects;
    }

    public void setSubjects(MovieBaseBean[] subjects) {
        this.subjects = subjects;
    }
}
