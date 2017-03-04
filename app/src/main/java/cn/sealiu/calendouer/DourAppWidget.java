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
import android.view.View;
import android.widget.RemoteViews;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.sealiu.calendouer.until.DBHelper;
import cn.sealiu.calendouer.until.LunarCalendar;
import cn.sealiu.calendouer.until.MovieContract;

public class DourAppWidget extends AppWidgetProvider {

    void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                         int appWidgetId) {

        List<String> list = LunarCalendar.getLunarCalendarStr(new Date());

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.dour_app_widget);
        views.setTextViewText(
                R.id.month_year,
                list.get(5) + "年 " + list.get(7) + "月 " + list.get(8) + "日");
        views.setTextViewText(R.id.week_day, list.get(4));

        CharSequence lunarText = String.format(
                context.getString(R.string.lunar_date),
                list.get(1),
                list.get(2)
        );

        views.setTextViewText(R.id.lunar_date, lunarText);

        //movie

        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + MovieContract.MovieEntry.TABLE_NAME, null);
        boolean isEmpty = !cursor.moveToFirst();
        cursor.close();

        if (!isEmpty) {
            SharedPreferences sharedPref = context.getSharedPreferences("calendouer", Context.MODE_PRIVATE);
            String datePref = sharedPref.getString("DATE", "null");
            String idPref = sharedPref.getString("ID", "null");
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            views.setViewVisibility(R.id.init_movie_btn, View.GONE);
            views.setViewVisibility(R.id.movie_holder, View.VISIBLE);

            if (!datePref.equals(df.format(new Date()))) {

                db = dbHelper.getWritableDatabase();
                db.delete(
                        MovieContract.MovieEntry.TABLE_NAME,
                        MovieContract.MovieEntry.COLUMN_NAME_ID + "=?",
                        new String[]{idPref}
                );
            } else {
                db = dbHelper.getReadableDatabase();
                String[] projection = {
                        MovieContract.MovieEntry.COLUMN_NAME_ID,
                        MovieContract.MovieEntry.COLUMN_NAME_TITLE,
                        MovieContract.MovieEntry.COLUMN_NAME_AVERAGE,
                        MovieContract.MovieEntry.COLUMN_NAME_ALT,
                };

                cursor = db.query(
                        MovieContract.MovieEntry.TABLE_NAME, //table
                        projection, //columns
                        MovieContract.MovieEntry.COLUMN_NAME_ID + " = ?", //selection
                        new String[]{idPref}, //selectionArgs
                        null, //groupBy
                        null, //having
                        null, //orderBy
                        "1" //limit
                );

                if (cursor.moveToFirst()) {
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
            }

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

