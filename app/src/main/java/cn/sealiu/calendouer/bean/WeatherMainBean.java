package cn.sealiu.calendouer.bean;

/**
 * Created by liuyang
 * on 2017/1/16.
 */

public class WeatherMainBean {

    private float temp;
    private int humidity;
    private float temp_min;
    private float temp_max;

    public float getTemp() {
        return temp;
    }

    public void setTemp(float temp) {
        this.temp = temp;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public float getTemp_min() {
        return temp_min;
    }

    public void setTemp_min(float temp_min) {
        this.temp_min = temp_min;
    }

    public float getTemp_max() {
        return temp_max;
    }

    public void setTemp_max(float temp_max) {
        this.temp_max = temp_max;
    }
}
