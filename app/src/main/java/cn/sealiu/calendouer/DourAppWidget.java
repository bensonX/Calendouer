package cn.sealiu.calendouer;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.RemoteViews;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import cn.sealiu.calendouer.until.LunarCalendar;
import cn.sealiu.calendouer.until.MovieContract;
import cn.sealiu.calendouer.until.MovieDBHelper;

public class DourAppWidget extends AppWidgetProvider {

    void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                         int appWidgetId) {
        String[] weeks = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        String[] lunar_months = {"一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月",
                "九月", "十月", "冬月", "腊月"};
        String[] days = {"初一", "初二", "初三", "初四", "初五", "初六", "初七", "初八", "初九",
                "初十", "十一", "十二", "十三", "十四", "十五", "十六", "十七", "十八", "十九", "二十",
                "廿一", "廿二", "廿三", "廿四", "廿五", "廿六", "廿七", "廿八", "廿九", "三十"
        };

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int week = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        int[] lunar = LunarCalendar.solarToLunar(year, month, day);

        //CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.dour_app_widget);
        views.setTextViewText(R.id.month_year, year + "年 " + month + "月 " + day + "日");
        views.setTextViewText(R.id.week_day, weeks[week]);

        CharSequence lunarText = String.format(
                context.getString(R.string.lunar_date),
                lunar_months[lunar[1] - 1],
                days[lunar[2] - 1]
        );

        views.setTextViewText(R.id.lunar_date, lunarText);

        //movie
        MovieDBHelper dbHelper = new MovieDBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + MovieContract.MovieEntry.TABLE_NAME, null);
        boolean isEmpty = !cursor.moveToFirst();
        cursor.close();

        if (!isEmpty) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            String datePref = sharedPref.getString("DATE", "null");
            String idPref = sharedPref.getString("ID", "null");
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            if (!datePref.equals(df.format(new Date()))) {

                db = dbHelper.getWritableDatabase();
                db.delete(
                        MovieContract.MovieEntry.TABLE_NAME,
                        MovieContract.MovieEntry.COLUMN_NAME_ID + "=?",
                        new String[]{idPref}
                );
            }
            views.setViewVisibility(R.id.init_movie_btn, View.GONE);
            views.setViewVisibility(R.id.movie_holder, View.VISIBLE);

            db = dbHelper.getReadableDatabase();
            String[] projection = {
                    MovieContract.MovieEntry.COLUMN_NAME_ID,
                    MovieContract.MovieEntry.COLUMN_NAME_TITLE,
                    MovieContract.MovieEntry.COLUMN_NAME_AVERAGE,
                    MovieContract.MovieEntry.COLUMN_NAME_ALT,
            };

            cursor = db.query(
                    MovieContract.MovieEntry.TABLE_NAME,
                    projection,
                    null,
                    null,
                    null,
                    null,
                    null,
                    "1"
            );

            String id = "null";
            if (cursor.moveToFirst()) {
                id = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_NAME_ID));
                String title = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_NAME_TITLE));
                float average = cursor.getFloat(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_NAME_AVERAGE));
                String alt = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_NAME_ALT));

                views.setTextViewText(R.id.rating__average, Float.toString(average));
                views.setTextViewText(R.id.movie_title, context.getString(
                        R.string.movie_recommended) +
                        "\n" +
                        title
                );

                Intent doubanIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(alt));
                PendingIntent pendingIntent1 = PendingIntent.getActivity(context, 0, doubanIntent, 0);
                views.setOnClickPendingIntent(R.id.movie_title, pendingIntent1);

                Intent openAppIntent = new Intent(context, MainActivity.class);
                PendingIntent pendingIntent2 = PendingIntent.getActivity(context, 0, openAppIntent, 0);
                views.setOnClickPendingIntent(R.id.calendar_holder, pendingIntent2);
            }

            sharedPref.edit().putString("DATE", df.format(new Date())).apply();
            sharedPref.edit().putString("ID", id).apply();

        } else {
            views.setViewVisibility(R.id.init_movie_btn, View.VISIBLE);
            views.setViewVisibility(R.id.movie_holder, View.GONE);

            Intent openAppIntent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, openAppIntent, 0);
            views.setOnClickPendingIntent(R.id.init_movie_btn, pendingIntent);
        }


        // Instruct the widget manager to update the widget
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
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

