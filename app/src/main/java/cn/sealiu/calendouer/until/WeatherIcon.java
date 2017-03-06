package cn.sealiu.calendouer.until;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.sealiu.calendouer.R;

/**
 * Created by liuyang
 * on 2017/2/19.
 */

public class WeatherIcon {
    public Map<String, Integer> map;
    private List<String> sunny = new ArrayList<>();
    private List<String> cloud = new ArrayList<>();
    private List<String> rain = new ArrayList<>();
    private List<String> snow = new ArrayList<>();
    private List<String> wind_sand = new ArrayList<>();

    public WeatherIcon() {
        map = new HashMap<>();
        //晴
        map.put("0", R.mipmap.sunny);
        map.put("1", R.mipmap.clear);
        map.put("2", R.mipmap.sunny);
        map.put("3", R.mipmap.clear);
        map.put("38", R.mipmap.sunny);

        sunny.add("0");
        sunny.add("1");
        sunny.add("2");
        sunny.add("3");
        sunny.add("38");

        //少云
        map.put("4", R.mipmap.cloudy);
        map.put("5", R.mipmap.partly_cloudy_day);
        map.put("6", R.mipmap.partly_cloudy_night);
        //多云
        map.put("7", R.mipmap.mostly_cloudy_day);
        map.put("8", R.mipmap.mostly_cloudy_night);
        //阴
        map.put("9", R.mipmap.overcast);

        cloud.add("4");
        cloud.add("5");
        cloud.add("6");
        cloud.add("7");
        cloud.add("8");
        cloud.add("9");

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

        rain.add("10");
        rain.add("11");
        rain.add("12");
        rain.add("13");
        rain.add("14");
        rain.add("15");
        rain.add("16");
        rain.add("17");
        rain.add("18");
        rain.add("19");
        rain.add("34");
        rain.add("35");
        rain.add("36");

        //雪
        map.put("20", R.mipmap.sleet);
        map.put("21", R.mipmap.snow);
        map.put("22", R.mipmap.snow);
        map.put("23", R.mipmap.snow);
        map.put("24", R.mipmap.snow);
        map.put("25", R.mipmap.snow);
        map.put("37", R.mipmap.snow);

        snow.add("20");
        snow.add("21");
        snow.add("22");
        snow.add("23");
        snow.add("24");
        snow.add("25");
        snow.add("37");

        //沙尘
        map.put("26", R.mipmap.wind);
        map.put("27", R.mipmap.wind);
        map.put("28", R.mipmap.wind);
        map.put("29", R.mipmap.wind);
        //雾/霾
        map.put("30", R.mipmap.mist);
        map.put("31", R.mipmap.mist);
        //风
        map.put("32", R.mipmap.wind);
        map.put("33", R.mipmap.wind);


        wind_sand.add("26");
        wind_sand.add("27");
        wind_sand.add("28");
        wind_sand.add("29");
        wind_sand.add("30");
        wind_sand.add("31");
        wind_sand.add("32");
        wind_sand.add("33");

        //未知
        map.put("99", R.mipmap.sunny);
    }

    public int getWeather(String pos) {
        if (sunny.contains(pos)) {
            return 1;
        } else if (cloud.contains(pos)) {
            return 2;
        } else if (rain.contains(pos)) {
            return 3;
        } else if (snow.contains(pos)) {
            return 4;
        } else if (wind_sand.contains(pos)) {
            return 5;
        } else {
            return 0;
        }
    }
}
