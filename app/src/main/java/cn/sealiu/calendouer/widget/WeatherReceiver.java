package cn.sealiu.calendouer.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class WeatherReceiver extends BroadcastReceiver {

    public static final String UPDATE_WEATHER_ACTION = "cn.sealiu.calendouer.UPDATE_WEATHER";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (UPDATE_WEATHER_ACTION.equals(intent.getAction())) {
            Log.d("AlarmMgrWeather",
                    "receiver received action: 'cn.sealiu.calendouer.UPDATE_WEATHER'");
            context.startService(new Intent(context, WeatherService.class));

        }
    }
}
