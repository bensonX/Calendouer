package cn.sealiu.calendouer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import cn.sealiu.calendouer.service.NotificationService;

/**
 * Created by liuyang
 * on 2017/3/8.
 */

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, NotificationService.class);
        String thingID = intent.getStringExtra("thing_id");
        serviceIntent.putExtra("thing_id", thingID);
        context.startService(serviceIntent);
    }
}
