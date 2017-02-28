package cn.sealiu.calendouer.until;

import java.util.HashMap;
import java.util.Map;

import cn.sealiu.calendouer.R;

/**
 * Created by liuyang
 * on 2017/2/19.
 */

public class WeatherIcon {
    public Map<String, Integer> map;

    public WeatherIcon() {
        map = new HashMap<>();
        //晴
        map.put("0", R.mipmap.sunny);
        map.put("1", R.mipmap.clear);
        map.put("2", R.mipmap.sunny);
        map.put("3", R.mipmap.clear);
        map.put("38", R.mipmap.sunny);
        //少云
        map.put("5", R.mipmap.partly_cloudy_day);
        map.put("6", R.mipmap.partly_cloudy_night);
        //多云
        map.put("7", R.mipmap.mostly_cloudy_day);
        map.put("8", R.mipmap.mostly_cloudy_night);

        //阴
        map.put("4", R.mipmap.cloudy);
        map.put("9", R.mipmap.overcast);
        //小雨
        map.put("10", R.mipmap.shower);
        map.put("11", R.mipmap.thunder_shower);
        map.put("12", R.mipmap.thunder_shower);
        map.put("13", R.mipmap.light_rain);
        //中雨
        map.put("14", R.mipmap.moderate_rain);
        //大雨
        map.put("15", R.mipmap.heavy_rain);
        map.put("16", R.mipmap.thunder_storm);
        map.put("17", R.mipmap.thunder_storm);
        map.put("18", R.mipmap.thunder_storm);
        map.put("19", R.mipmap.ice_rain);
        map.put("34", R.mipmap.thunder_storm);
        map.put("35", R.mipmap.thunder_storm);
        map.put("36", R.mipmap.thunder_storm);
        //雪
        map.put("20", R.mipmap.sleet);
        map.put("21", R.mipmap.snow);
        map.put("22", R.mipmap.snow);
        map.put("23", R.mipmap.snow);
        map.put("24", R.mipmap.snow);
        map.put("25", R.mipmap.snow);
        map.put("37", R.mipmap.snow);
        //雾/霾
        map.put("30", R.mipmap.mist);
        map.put("31", R.mipmap.mist);
        //风
        map.put("32", R.mipmap.wind);
        map.put("33", R.mipmap.wind);
        //沙尘
        map.put("26", R.mipmap.wind);
        map.put("27", R.mipmap.wind);
        map.put("28", R.mipmap.wind);
        map.put("29", R.mipmap.wind);
        //未知
        map.put("99", R.mipmap.sunny);
    }
}
