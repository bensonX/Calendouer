package cn.sealiu.calendouer.widget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import cn.sealiu.calendouer.MainActivity;
import cn.sealiu.calendouer.R;
import cn.sealiu.calendouer.bean.XzBean;
import cn.sealiu.calendouer.bean.XzLocationBean;
import cn.sealiu.calendouer.bean.XzResultsBean;
import cn.sealiu.calendouer.bean.XzWeatherBean;
import cn.sealiu.calendouer.until.WeatherIcon;

/**
 * Implementation of App Widget functionality.
 */
public class ClockWidgetProvider extends AppWidgetProvider {

    public static final String CLOCK_TICK_ACTION = "cn.sealiu.calendouer.CLOCK_TICK";
    public static final String UPDATE_WEATHER_ACTION = "cn.sealiu.calendouer.UPDATE_WEATHER";
    public static final String WEATHER_UPDATED = "cn.sealiu.calendouer.WEATHER_UPDATED";
    private static final int requestCodeTime = 60;
    private static final int requestCodeWeather = 7200;
    private static final DateFormat df_hm = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private static final String[] weeks = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};


    static void updateTime(Context context, AppWidgetManager appWidgetManager,
                           int appWidgetId) {

        Log.d("AlarmMgrTime", Calendar.getInstance().getTime().toString());

        // set repeat alarm
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent updateIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        updateIntent.putExtra("type", "time");

        PendingIntent updatePendingIntent = PendingIntent.getBroadcast(
                context, requestCodeTime, updateIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        long triggerAtTime = SystemClock.elapsedRealtime() + 10000;
        alarmMgr.setRepeating(AlarmManager.RTC, triggerAtTime, 60000, updatePendingIntent);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.clock_widget);
        Calendar now = Calendar.getInstance();

        // time
        views.setTextViewText(R.id.time, df_hm.format(now.getTime()));
        views.setTextViewText(R.id.week_day, weeks[now.get(Calendar.DAY_OF_WEEK) - 1]);

        views.setTextViewText(R.id.today, now.get(Calendar.DAY_OF_MONTH) + "");

        // tomorrow
        now.add(Calendar.DAY_OF_MONTH, 1);
        views.setTextViewText(R.id.tomorrow, now.get(Calendar.DAY_OF_MONTH) + "");

        // day after tomorrow
        now.add(Calendar.DAY_OF_MONTH, 1);
        views.setTextViewText(R.id.day_after_tomorrow, now.get(Calendar.DAY_OF_MONTH) + "");

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private void updateWeather(Context context, AppWidgetManager appWidgetManager,
                               int appWidgetId) {
        Log.d("AlarmMgrWeather", Calendar.getInstance().getTime().toString());

        // set repeat alarm
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent updateIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        updateIntent.putExtra("type", "weather");

        PendingIntent updatePendingIntent = PendingIntent.getBroadcast(
                context, requestCodeWeather, updateIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        long triggerAtTime = SystemClock.elapsedRealtime() + 10000;
        alarmMgr.setRepeating(AlarmManager.RTC, triggerAtTime, 120000, updatePendingIntent);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.clock_widget);

        // open calendouer intent
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        // open calendouer
        views.setOnClickPendingIntent(R.id.open_calendouer, pendingIntent);

        // update weather
        SharedPreferences sharedPref = context.getSharedPreferences(
                "calendouer", Context.MODE_PRIVATE);
        String weather_json = sharedPref.getString("weather_json", "");

        if (weather_json.equals("")) {
            views.setTextViewText(R.id.weather_info, "打开Calendouer，获取天气信息");
            views.setTextViewText(R.id.city, "打开Calendouer，获取位置信息");
        } else {
            XzBean xzBean = new Gson().fromJson(weather_json, XzBean.class);
            XzResultsBean resultsBean = xzBean.getResults()[0];
            XzLocationBean locationBean = resultsBean.getLocation();
            XzWeatherBean[] weatherBeans = resultsBean.getDaily();

            XzWeatherBean nowWeather = weatherBeans[0];

            String weathersText;
            if (nowWeather.getText_day().equals(nowWeather.getText_night())) {
                weathersText = nowWeather.getText_night();
            } else {
                weathersText = String.format(
                        context.getString(R.string.weather_info),
                        nowWeather.getText_day(),
                        nowWeather.getText_night()
                );
            }

            views.setTextViewText(R.id.weather_info, String.format(
                    context.getString(R.string.weather_widget),
                    nowWeather.getHigh(),
                    nowWeather.getLow(),
                    weathersText
            ));
            views.setTextViewText(R.id.city, locationBean.getName());

            WeatherIcon icons = new WeatherIcon();
            if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 18) { //day
                String weather_code = nowWeather.getCode_day();
                views.setImageViewResource(R.id.weather_icon, icons.map.get(weather_code));
            } else {//night
                String weather_code = nowWeather.getCode_night();
                views.setImageViewResource(R.id.weather_icon, icons.map.get(weather_code));
            }
        }

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName widgetComponent = new ComponentName(
                context.getPackageName(),
                this.getClass().getName()
        );
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(widgetComponent);

        if (CLOCK_TICK_ACTION.equals(intent.getAction())) {
            for (int appWidgetId : appWidgetIds) {
                updateTime(context, appWidgetManager, appWidgetId);
            }
        }

        if (WEATHER_UPDATED.equals(intent.getAction())) {
            if (intent.getBooleanExtra("status", false)) {
                for (int appWidgetId : appWidgetIds) {
                    updateWeather(context, appWidgetManager, appWidgetId);
                }
            }
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateTime(context, appWidgetManager, appWidgetId);
            updateWeather(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long intervalClock = 60000;
        long intervalWeather = 7200000;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        alarmMgr.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),
                intervalClock, createClockTickIntent(context));

        alarmMgr.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),
                intervalWeather, createWeatherUpdateIntent(context));
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmMgr.cancel(createClockTickIntent(context));
        alarmMgr.cancel(createWeatherUpdateIntent(context));
        Log.d("AlarmMgr", "canceled");
    }

    private PendingIntent createClockTickIntent(Context context) {
        Intent intent = new Intent(CLOCK_TICK_ACTION);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    private PendingIntent createWeatherUpdateIntent(Context context) {
        Intent intent = new Intent(UPDATE_WEATHER_ACTION);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }
}

