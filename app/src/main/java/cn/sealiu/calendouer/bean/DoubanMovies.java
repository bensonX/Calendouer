package cn.sealiu.calendouer.bean;

/**
 * Created by liuyang
 * on 2017/4/17.
 */

public class DoubanMovies {
    private int count;
    private int start;
    private int total;
    private String title;
    private MovieBean[] subjects;

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

    public MovieBean[] getSubjects() {
        return subjects;
    }

    public void setSubjects(MovieBean[] subjects) {
        this.subjects = subjects;
    }
}
