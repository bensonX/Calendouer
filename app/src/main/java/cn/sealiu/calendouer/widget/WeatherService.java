package cn.sealiu.calendouer.widget;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cn.sealiu.calendouer.CalendouerActivity;

public class WeatherService extends IntentService {
    public static final String WEATHER_UPDATED = "cn.sealiu.calendouer.WEATHER_UPDATED";
    private static final String TAG = "WeatherService";
    DateFormat df_hm, df_ymd_hms;
    private SharedPreferences sharedPref;

    public WeatherService() {
        super("WeatherService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPref = this.getSharedPreferences("calendouer", Context.MODE_PRIVATE);
        df_hm = new SimpleDateFormat("HH:mm", Locale.getDefault());
        df_ymd_hms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String lat = sharedPref.getString("Latitude", "");
        String lng = sharedPref.getString("Longitude", "");

        if (!lat.equals("") && !lng.equals("")) {
            Log.d(TAG, "lat: " + lat + "lng: " + lng);
            String apiStr = "https://api.thinkpage.cn/v3/weather/daily.json?key=txyws41isbyqnma5&" +
                    "location=" + lat + ":" + lng + "&language=zh-Hans&unit=c";
            new GetWeather().execute(apiStr);
        } else {
            Log.d(TAG, "lat and lng is empty");
            sendBroadcast(false);
        }
    }

    private void sendBroadcast(boolean status) {
        Intent intentResult = new Intent(WEATHER_UPDATED);
        intentResult.putExtra("status", status);
        sendBroadcast(intentResult);
    }

    private class GetWeather extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            return CalendouerActivity.doInBackground(params[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null && !s.equals("")) {
                sharedPref.edit().putString("weather_json", s).apply();
                sharedPref.edit().putString("update_time", df_hm.format(new Date())).apply();
                sharedPref.edit().putString("update_datetime", df_ymd_hms.format(new Date())).apply();
                sendBroadcast(true);
            } else {
                sendBroadcast(false);
            }
        }
    }
}
