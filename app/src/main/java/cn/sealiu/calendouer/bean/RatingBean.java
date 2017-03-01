package cn.sealiu.calendouer.bean;

/**
 * Created by liuyang
 * on 2017/1/15.
 */
public class RatingBean {
    private int max;
    private float average;
    private String stars;
    private int min;

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public float getAverage() {
        return average;
    }

    public void setAverage(float average) {
        this.average = average;
    }

    public String getStarts() {
        return stars;
    }

    public void setStarts(String stars) {
        this.stars = stars;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }
}
