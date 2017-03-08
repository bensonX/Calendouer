package cn.sealiu.calendouer.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import cn.sealiu.calendouer.until.DBHelper;
import cn.sealiu.calendouer.until.ThingsContract;

public class DoneOrDeleteService extends IntentService {

    public static final String ACTION_DONE = "cn.sealiu.calendouer.service.action.DONE";
    public static final String ACTION_DELETE = "cn.sealiu.calendouer.service.action.DELETE";

    public static final String EXTRA_ID = "cn.sealiu.calendouer.service.extra.ID";
    private NotificationManager notificationManager;

    public DoneOrDeleteService() {
        super("DoneOrDeleteService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        notificationManager = (NotificationManager) this.getSystemService(
                Context.NOTIFICATION_SERVICE
        );

        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_DONE.equals(action)) {
                final String id = intent.getStringExtra(EXTRA_ID);
                handleActionDone(id);
            } else if (ACTION_DELETE.equals(action)) {
                final String id = intent.getStringExtra(EXTRA_ID);
                handleActionDelete(id);
            }
        }
    }

    private void handleActionDone(String id) {
        Log.d("Things", id);

        ContentValues values = new ContentValues();
        values.put(ThingsContract.ThingsEntry.COLUMN_NAME_DONE, 1);

        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.update(
                ThingsContract.ThingsEntry.TABLE_NAME,
                values,
                ThingsContract.ThingsEntry.COLUMN_NAME_ID + "=?",
                new String[]{id}
        );

        notificationManager.cancel(NotificationService.NOTIFICATION_ID);
    }

    private void handleActionDelete(String id) {
        Log.d("Things", id);

        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete(
                ThingsContract.ThingsEntry.TABLE_NAME,
                ThingsContract.ThingsEntry.COLUMN_NAME_ID + "=?",
                new String[]{id}
        );

        notificationManager.cancel(NotificationService.NOTIFICATION_ID);
    }
}
