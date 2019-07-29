package com.allyants.notifyme;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.Calendar;

import static com.allyants.notifyme.Notification.NotificationEntry.TABLE_NAME;

public class ActionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String notificationId = intent.getStringExtra("_id");
        String rrule = intent.getStringExtra("rrule");
        long dstart = intent.getLongExtra("dstart",Calendar.getInstance().getTimeInMillis());
        int index = intent.getIntExtra("index",-1);
        String action = intent.getStringExtra("action");
        try {
            Intent tent = Intent.parseUri(action, 0);
            context.startActivity(tent);
        }catch (Exception e){
            e.printStackTrace();
        }

        if(intent.getBooleanExtra("collapse",true)) {
            Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            context.sendBroadcast(it);
        }

        if(intent.getBooleanExtra("dismiss",true)){
            DeletePendingIntent.DeleteNotification(context,notificationId,rrule,dstart);
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(Integer.parseInt(notificationId));
        }
    }
}

