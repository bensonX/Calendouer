package cn.sealiu.calendouer.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import cn.sealiu.calendouer.R;
import cn.sealiu.calendouer.ThingsDetailActivity;
import cn.sealiu.calendouer.model.Thing;
import cn.sealiu.calendouer.until.DBHelper;
import cn.sealiu.calendouer.until.ThingsContract;

/**
 * Created by liuyang
 * on 2017/3/8.
 */

public class NotificationService extends IntentService {

    final static int NOTIFICATION_ID = 123;
    NotificationManager notificationManager;
    SharedPreferences settingPref;
    Thing thing;

    public NotificationService() {
        super("NotificationService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if (intent == null) {
            return;
        }
        String thingID = intent.getStringExtra("thing_id");

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
                ThingsContract.ThingsEntry.COLUMN_NAME_ID + " = ?", //selection
                new String[]{thingID}, //selectionArgs
                null, //groupBy
                null, //having
                ThingsContract.ThingsEntry.COLUMN_NAME_NOTIFICATION_DATETIME, //orderBy
                "1" //limit
        );

        if (cursor.moveToFirst()) {
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
        }

        cursor.close();

        if (thing != null) {

            notificationManager = (NotificationManager) this.getSystemService(
                    Context.NOTIFICATION_SERVICE
            );

            Intent detailIntent = new Intent(this, ThingsDetailActivity.class);
            detailIntent.putExtra("thing", thing);
            PendingIntent pendingIntentClick = PendingIntent.getActivity(
                    this,
                    0,
                    detailIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );

            Intent doneIntent = new Intent();
            doneIntent.setAction(DoneOrDeleteService.ACTION_DONE);
            doneIntent.putExtra(DoneOrDeleteService.EXTRA_ID, thing.getId());
            PendingIntent pendingIntentDone = PendingIntent.getService(
                    this,
                    0,
                    doneIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );

            Intent deleteIntent = new Intent();
            deleteIntent.setAction(DoneOrDeleteService.ACTION_DELETE);
            deleteIntent.putExtra(DoneOrDeleteService.EXTRA_ID, thing.getId());
            PendingIntent pendingIntentDelete = PendingIntent.getService(
                    this,
                    0,
                    deleteIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(
                            BitmapFactory.decodeResource(
                                    this.getResources(),
                                    R.mipmap.ic_launcher
                            )
                    ).setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                    .setContentTitle(thing.getTitle())
                    .setContentText(thing.getNotification_datetime().substring(0, 16))
                    .setContentIntent(pendingIntentClick)
                    .setAutoCancel(true)
                    .addAction(
                            R.drawable.ic_done_black_24dp,
                            getString(R.string.mark_done),
                            pendingIntentDone
                    ).addAction(
                            R.drawable.ic_delete_black_24dp,
                            getString(R.string.delete),
                            pendingIntentDelete
                    );

            settingPref = PreferenceManager.getDefaultSharedPreferences(this);
            if (settingPref.getBoolean("things_show", true) &&
                    settingPref.getBoolean("things_notification", true)) {

                if (settingPref.getBoolean("things_vibrate", true)) {
                    builder.setVibrate(new long[]{0, 500});
                }

                String soundStr = settingPref.getString(
                        "things_ringtone",
                        "DEFAULT_SOUND"
                );
                Uri soundUri = Uri.parse(soundStr);
                builder.setSound(soundUri);
            }

            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }
}
