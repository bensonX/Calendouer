package cn.sealiu.calendouer;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import cn.sealiu.calendouer.receiver.NotificationReceiver;

/**
 * Created by liuyang
 * on 2017/3/8.
 */

public class CalendouerActivity extends AppCompatActivity {

    final static int STAR = 5;
    final static int MAX_COUNT = 100;
    final static int LOCATION_PERM = 100;
    final static int THINGS_MAX_LINE = 5;

    final static int WEATHER_REQUEST_CODE = 114;
    final static int ADD_THINGS_CODE = 200;
    final static int DETAIL_THINGS_CODE = 201;
    final static int MODIFY_THINGS_CODE = 202;
    DateFormat df_ymd, df_hm, df_ymd_hms;

    SharedPreferences sharedPref, settingPref;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = this.getSharedPreferences("calendouer", Context.MODE_PRIVATE);
        settingPref = PreferenceManager.getDefaultSharedPreferences(this);

        df_ymd = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        df_hm = new SimpleDateFormat("HH:mm", Locale.getDefault());
        df_ymd_hms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    }

    void displaySnackBar(View view, String text, String actionName, View.OnClickListener action) {
        Snackbar snackbar = Snackbar.make(view, text, Snackbar.LENGTH_LONG)
                .setAction(actionName, action);
        View v = snackbar.getView();
        ((TextView) v.findViewById(android.support.design.R.id.snackbar_text)).setTextColor(ContextCompat.getColor(this, R.color.textOrIcons));
        ((TextView) v.findViewById(android.support.design.R.id.snackbar_action)).setTextColor(ContextCompat.getColor(this, R.color.colorAccent));

        snackbar.show();
    }

    void setCustomTheme(
            int color,
            int colorDark,
            FloatingActionButton fab,
            CollapsingToolbarLayout collapsingToolbarLayout) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fab.setBackgroundTintList(ColorStateList.valueOf(color));
            collapsingToolbarLayout.setContentScrimColor(color);
            collapsingToolbarLayout.setBackgroundColor(color);
            this.getWindow().setNavigationBarColor(color);
            this.getWindow().setStatusBarColor(colorDark);
        }
    }

    void setCustomTheme(
            int color,
            int colorDark,
            CollapsingToolbarLayout collapsingToolbarLayout) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            collapsingToolbarLayout.setContentScrimColor(color);
            collapsingToolbarLayout.setBackgroundColor(color);
            this.getWindow().setNavigationBarColor(color);
            this.getWindow().setStatusBarColor(colorDark);
        }
    }

    boolean checkLocationPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    void setAlarm(Intent intent, int requestCode, long triggerAtMillis) {
        AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);

        PendingIntent alarmIntent = PendingIntent.getBroadcast(
                getApplicationContext(),
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        alarmMgr.set(
                AlarmManager.RTC,
                triggerAtMillis,
                alarmIntent
        );
    }

    void cancelAlarm(Intent intent, int requestCode) {
        AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);

        PendingIntent alarmIntent = PendingIntent.getBroadcast(
                getApplicationContext(),
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        alarmMgr.cancel(alarmIntent);
    }

    void setThingAlarm(String thingId, String time, int request_code) {
        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra("thing_id", thingId);

        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(df_ymd_hms.parse(time));
            setAlarm(intent, request_code, calendar.getTimeInMillis());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    void cancelThingAlarm(String thingId, int request_code) {
        //cancel old notification alarm
        Intent intent = new Intent(getApplicationContext(), NotificationReceiver.class);
        intent.putExtra("thing_id", thingId);

        cancelAlarm(intent, request_code);
    }

    float getScreenWidthInPd() {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = getResources().getDisplayMetrics().density;

        return outMetrics.widthPixels / density;
    }

    float getProgressOfYear() {
        Calendar calendar = Calendar.getInstance();
        long now = calendar.getTimeInMillis();

        calendar.set(Calendar.DAY_OF_YEAR, 1);
        long start = calendar.getTimeInMillis();

        calendar.add(Calendar.YEAR, 1);
        long end = calendar.getTimeInMillis();

        return Math.round(1000 * (now - start) / (end - start)) / 10f;
    }

    float getProgressOfDay() {
        Calendar calendar = Calendar.getInstance();
        long now = calendar.getTimeInMillis();

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long start = calendar.getTimeInMillis();

        calendar.add(Calendar.DAY_OF_MONTH, 1);
        long end = calendar.getTimeInMillis();

        return Math.round(1000 * (now - start) / (end - start)) / 10f;
    }

    void setProgressInPd(View view) {
        float scale = getResources().getDisplayMetrics().density;
        float screenWidth = getScreenWidthInPd();

        float p = getProgressOfDay() / 100f;
        if (p <= 0.2) {
            view.setBackgroundColor(ContextCompat.getColor(this, R.color.navyGrayDark));
        } else if (p <= 0.4) {
            view.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        } else if (p <= 0.6) {
            view.setBackgroundColor(ContextCompat.getColor(this, R.color.blueSkyDark));
        } else if (p <= 0.8) {
            view.setBackgroundColor(ContextCompat.getColor(this, R.color.orangeDark));
        } else {
            view.setBackgroundColor(ContextCompat.getColor(this, R.color.tomatoDark));
        }

        int widthInPx = (int) (Math.round(p * screenWidth) * scale);

        view.requestLayout();
        view.getLayoutParams().width = widthInPx;
    }
}

