package cn.sealiu.calendouer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

/**
 * Created by liuyang
 * on 2016/11/28.
 */
public class UpdateWeatherReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPref = context.getSharedPreferences("calendouer", Context.MODE_PRIVATE);
        sharedPref.edit().putString("weather_json", "").apply();
    }
}
