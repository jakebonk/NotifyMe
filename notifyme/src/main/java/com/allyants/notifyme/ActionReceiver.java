package com.allyants.notifyme;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ActionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        com.allyants.notifyme.Notification.NotificationDBHelper mDbHelper = new com.allyants.notifyme.Notification.NotificationDBHelper(context);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String notificationId = intent.getStringExtra("_id");
        Log.e("id",notificationId);
        int index = intent.getIntExtra("index",-1);
        String action = intent.getStringExtra("action");
        try {
            Intent tent = Intent.parseUri(action, 0);
            context.startActivity(tent);
        }catch (Exception e){
            e.printStackTrace();
        }
        if(intent.getBooleanExtra("dismiss",true)){
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(Integer.parseInt(notificationId));
        }
    }
}
