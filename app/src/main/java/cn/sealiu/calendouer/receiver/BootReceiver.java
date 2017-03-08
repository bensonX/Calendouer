package cn.sealiu.calendouer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import cn.sealiu.calendouer.service.SystemBootService;

/**
 * Created by liuyang
 * on 2017/3/8.
 */

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, SystemBootService.class));
    }
}
