package cn.sealiu.calendouer.widget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.sealiu.calendouer.MainActivity;
import cn.sealiu.calendouer.R;
import cn.sealiu.calendouer.bean.XzBean;
import cn.sealiu.calendouer.bean.XzLocationBean;
import cn.sealiu.calendouer.bean.XzResultsBean;
import cn.sealiu.calendouer.bean.XzWeatherBean;
import cn.sealiu.calendouer.until.LunarCalendar;
import cn.sealiu.calendouer.until.WeatherIcon;

/**
 * Implementation of App Widget functionality.
 */
public class WeatherWidgetProvider extends AppWidgetProvider {

    public static final String UPDATE_WEATHER_ACTION = "cn.sealiu.calendouer.UPDATE_WEATHER";
    public static final String WEATHER_UPDATED = "cn.sealiu.calendouer.WEATHER_UPDATED";

    private static final DateFormat df_md = new SimpleDateFormat("MM/dd", Locale.getDefault());

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        Log.d("AlarmWeather", "hhh");
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.weather_widget);

        // open calendouer
        views.setImageViewResource(R.id.open_calendouer, R.drawable.ic_arrow_forward_24dp);
        views.setOnClickPendingIntent(R.id.open_calendouer, pendingIntent);

        // init calendar
        List<String> calendarList = LunarCalendar.getLunarCalendarStr(new Date());
        views.setTextViewText(R.id.solar_date, String.format(
                context.getString(R.string.solar_date_widget),
                calendarList.get(6) + calendarList.get(8)
        ));
        views.setTextViewText(R.id.lunar_date, String.format(
                context.getString(R.string.lunar_date),
                calendarList.get(1),
                calendarList.get(2)
        ));
        Calendar today = Calendar.getInstance();
        today.add(Calendar.DAY_OF_MONTH, 1);
        String tomorrowStr = df_md.format(today.getTime());
        today.add(Calendar.DAY_OF_MONTH, 1);
        String dayAfterTomorrowStr = df_md.format(today.getTime());

        views.setTextViewText(R.id.tomorrow, tomorrowStr);
        views.setTextViewText(R.id.day_after_tomorrow, dayAfterTomorrowStr);

        // init weather
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
                views.setImageViewResource(R.id.weather_1,
                        icons.map.get(weatherBeans[1].getCode_day()));
                views.setImageViewResource(R.id.weather_2,
                        icons.map.get(weatherBeans[2].getCode_day()));
            } else {//night
                String weather_code = nowWeather.getCode_night();
                views.setImageViewResource(R.id.weather_icon, icons.map.get(weather_code));
                views.setImageViewResource(R.id.weather_1,
                        icons.map.get(weatherBeans[1].getCode_night()));
                views.setImageViewResource(R.id.weather_2,
                        icons.map.get(weatherBeans[2].getCode_night()));
            }
        }

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long intervalWeather = 7200000;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        alarmMgr.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),
                intervalWeather, createWeatherUpdateIntent(context));
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmMgr.cancel(createWeatherUpdateIntent(context));
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

        if (WEATHER_UPDATED.equals(intent.getAction())) {
            for (int appWidgetId : appWidgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId);
            }
        }
    }

    private PendingIntent createWeatherUpdateIntent(Context context) {
        Intent intent = new Intent(UPDATE_WEATHER_ACTION);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }
}

