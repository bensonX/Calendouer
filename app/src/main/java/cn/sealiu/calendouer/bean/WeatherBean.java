package cn.sealiu.calendouer.bean;

/**
 * Created by liuyang
 * on 2017/1/16.
 */

public class WeatherBean {
    private int visibility;
    private String name; //City name
    private long dt; //Time of data calculation, unix, UTC
    private WeatherBaseBean[] weather;
    private WeatherMainBean main;

    public int getVisibility() {
        return visibility;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDt() {
        return dt;
    }

    public void setDt(long dt) {
        this.dt = dt;
    }

    public WeatherBaseBean[] getWeather() {
        return weather;
    }

    public void setWeather(WeatherBaseBean[] weather) {
        this.weather = weather;
    }

    public WeatherMainBean getMain() {
        return main;
    }

    public void setMain(WeatherMainBean main) {
        this.main = main;
    }
}
