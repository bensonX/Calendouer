package cn.sealiu.calendouer.bean;

/**
 * Created by liuyang
 * on 2017/2/18.
 */

public class XzResultsBean {
    private XzLocationBean location;
    private XzWeatherBean[] daily;
    private String last_update;

    public XzLocationBean getLocation() {
        return location;
    }

    public void setLocation(XzLocationBean location) {
        this.location = location;
    }

    public XzWeatherBean[] getDaily() {
        return daily;
    }

    public void setDaily(XzWeatherBean[] daily) {
        this.daily = daily;
    }

    public String getLast_update() {
        return last_update;
    }

    public void setLast_update(String last_update) {
        this.last_update = last_update;
    }
}
