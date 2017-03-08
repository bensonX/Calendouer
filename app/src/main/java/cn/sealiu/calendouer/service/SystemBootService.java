package cn.sealiu.calendouer.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import cn.sealiu.calendouer.model.Thing;
import cn.sealiu.calendouer.receiver.NotificationReceiver;
import cn.sealiu.calendouer.until.DBHelper;
import cn.sealiu.calendouer.until.ThingsContract;

/**
 * Created by liuyang
 * on 2017/3/8.
 */

public class SystemBootService extends IntentService {

    Thing thing;
    DateFormat df_ymd_hms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    public SystemBootService() {
        super("SystemBootService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                ThingsContract.ThingsEntry.COLUMN_NAME_ID,
                ThingsContract.ThingsEntry.COLUMN_NAME_TITLE,
                ThingsContract.ThingsEntry.COLUMN_NAME_DATETIME,
                ThingsContract.ThingsEntry.COLUMN_NAME_NOTIFICATION_DATETIME,
                ThingsContract.ThingsEntry.COLUMN_NAME_TIME_ADVANCE,
                ThingsContract.ThingsEntry.COLUMN_NAME_DONE,
                ThingsContract.ThingsEntry.COLUMN_NAME_REQUEST_CODE
        };

        Cursor cursor = db.query(
                ThingsContract.ThingsEntry.TABLE_NAME, //table
                projection, //columns
                ThingsContract.ThingsEntry.COLUMN_NAME_DONE + " = ?", //selection
                new String[]{"0"}, //selectionArgs
                null, //groupBy
                null, //having
                null, //orderBy
                null //limit
        );

        if (cursor.moveToFirst()) {
            Date now = new Date();

            do {
                String id = cursor.getString(cursor.getColumnIndex(ThingsContract.ThingsEntry.COLUMN_NAME_ID));
                String title = cursor.getString(cursor.getColumnIndex(ThingsContract.ThingsEntry.COLUMN_NAME_TITLE));
                String datetime = cursor.getString(cursor.getColumnIndex(ThingsContract.ThingsEntry.COLUMN_NAME_DATETIME));
                String notification_datetime = cursor.getString(
                        cursor.getColumnIndex(ThingsContract.ThingsEntry.COLUMN_NAME_NOTIFICATION_DATETIME)
                );
                int time_advance = cursor.getInt(cursor.getColumnIndex(ThingsContract.ThingsEntry.COLUMN_NAME_TIME_ADVANCE));
                int done = cursor.getInt(cursor.getColumnIndex(ThingsContract.ThingsEntry.COLUMN_NAME_DONE));
                int request_code = cursor.getInt(cursor.getColumnIndex(ThingsContract.ThingsEntry.COLUMN_NAME_REQUEST_CODE));

                thing = new Thing(
                        id,
                        title,
                        datetime,
                        notification_datetime,
                        time_advance,
                        done,
                        request_code
                );

                Date notify_time = new Date();
                try {
                    notify_time = df_ymd_hms.parse(thing.getNotification_datetime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (notify_time.after(now)) {
                    setThingAlarm(thing.getId(), notify_time, thing.getRequest_code());
                }

            } while (cursor.moveToNext());
        }

        cursor.close();
    }

    private void setThingAlarm(String thingId, Date notifyTime, int requestCode) {
        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra("thing_id", thingId);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(notifyTime);

        AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);

        PendingIntent alarmIntent = PendingIntent.getBroadcast(
                getApplicationContext(),
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        alarmMgr.set(
                AlarmManager.RTC,
                calendar.getTimeInMillis(),
                alarmIntent
        );
    }
}
