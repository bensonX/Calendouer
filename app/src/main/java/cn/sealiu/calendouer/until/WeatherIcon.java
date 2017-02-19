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
        map.put("0", R.drawable.weather01);
        map.put("1", R.drawable.weather01);
        map.put("2", R.drawable.weather01);
        map.put("3", R.drawable.weather01);
        map.put("38", R.drawable.weather01);
        //少云
        map.put("5", R.drawable.weather02);
        map.put("6", R.drawable.weather02);
        //多云
        map.put("7", R.drawable.weather03);
        map.put("8", R.drawable.weather03);
        //阴
        map.put("4", R.drawable.weather04);
        map.put("9", R.drawable.weather04);
        //小雨
        map.put("10", R.drawable.weather09);
        map.put("11", R.drawable.weather09);
        map.put("13", R.drawable.weather09);
        //大雨
        map.put("14", R.drawable.weather10);
        map.put("15", R.drawable.weather10);
        map.put("16", R.drawable.weather10);
        map.put("19", R.drawable.weather10);
        //雷雨
        map.put("17", R.drawable.weather11);
        map.put("18", R.drawable.weather11);
        map.put("12", R.drawable.weather11);
        map.put("35", R.drawable.weather11);
        //雪
        map.put("21", R.drawable.weather13);
        map.put("22", R.drawable.weather13);
        map.put("23", R.drawable.weather13);
        map.put("24", R.drawable.weather13);
        map.put("25", R.drawable.weather13);
        map.put("37", R.drawable.weather13);
        //雾/霾
        map.put("30", R.drawable.weather50);
        map.put("31", R.drawable.weather50);
        //雨夹雪
        map.put("20", R.drawable.weather14);
        //大风
        map.put("34", R.drawable.weather15);
        map.put("36", R.drawable.weather15);
        //风
        map.put("32", R.drawable.weather16);
        map.put("33", R.drawable.weather16);
        //沙尘
        map.put("26", R.drawable.weather17);
        map.put("27", R.drawable.weather17);
        map.put("28", R.drawable.weather17);
        map.put("29", R.drawable.weather17);
        //未知
        map.put("99", R.drawable.weather18);
    }
}
