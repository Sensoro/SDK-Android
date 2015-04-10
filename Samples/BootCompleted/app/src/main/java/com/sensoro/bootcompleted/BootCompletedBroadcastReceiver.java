package com.sensoro.bootcompleted;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Sensoro on 15/3/11.
 */
public class BootCompletedBroadcastReceiver extends BroadcastReceiver {
    static final String BOOT_COMPLETED_ACTION = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(BOOT_COMPLETED_ACTION)) {

            /**
             * Start Sensoro SDK in Activity with boot.
             */
//            Intent bootCompletedActivityIntent = new Intent(context, MainActivity.class);
//            bootCompletedActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(bootCompletedActivityIntent);

            /**
             * Startan Sensoro SDK in Service with boot.
             */
            Intent bootCompletedSerivceIntent = new Intent(context, MyService.class);
            context.startService(bootCompletedSerivceIntent);
        }
    }
}
