package com.allyants.notifyme;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import static android.provider.BaseColumns._ID;
import static com.allyants.notifyme.Notification.NotificationEntry.NOTIFICATION_ACTIONS;
import static com.allyants.notifyme.Notification.NotificationEntry.NOTIFICATION_ACTIONS_COLLAPSE;
import static com.allyants.notifyme.Notification.NotificationEntry.NOTIFICATION_ACTIONS_DISMISS;
import static com.allyants.notifyme.Notification.NotificationEntry.NOTIFICATION_ACTIONS_TEXT;
import static com.allyants.notifyme.Notification.NotificationEntry.NOTIFICATION_COLOR;
import static com.allyants.notifyme.Notification.NotificationEntry.NOTIFICATION_CONTENT_TEXT;
import static com.allyants.notifyme.Notification.NotificationEntry.NOTIFICATION_LARGE_ICON;
import static com.allyants.notifyme.Notification.NotificationEntry.NOTIFICATION_LED_COLOR;
import static com.allyants.notifyme.Notification.NotificationEntry.NOTIFICATION_SMALL_ICON;
import static com.allyants.notifyme.Notification.NotificationEntry.NOTIFICATION_TITLE_TEXT;
import static com.allyants.notifyme.Notification.NotificationEntry.TABLE_NAME;

/**
 * Created by jbonk on 6/16/2018.
 */

public class NotificationPublisher extends BroadcastReceiver {

    public static String NOTIFICATION_ID = "notification_id";

    @Override
    public void onReceive(final Context context, Intent intent) {
        String notificationId = intent.getStringExtra(NOTIFICATION_ID);
        com.allyants.notifyme.Notification.NotificationDBHelper mDbHelper = new com.allyants.notifyme.Notification.NotificationDBHelper(context);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        Cursor data = db.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE "+_ID+" = "+notificationId,null);
        data.moveToFirst();
        String title = data.getString(data.getColumnIndex(NOTIFICATION_TITLE_TEXT));
        String content = data.getString(data.getColumnIndex(NOTIFICATION_CONTENT_TEXT));
        String str_actions = data.getString(data.getColumnIndex(NOTIFICATION_ACTIONS));
        String str_actions_text = data.getString(data.getColumnIndex(NOTIFICATION_ACTIONS_TEXT));
        String str_actions_dismiss = data.getString(data.getColumnIndex(NOTIFICATION_ACTIONS_DISMISS));
        String str_actions_collapse = data.getString(data.getColumnIndex(NOTIFICATION_ACTIONS_COLLAPSE));
        int led_color = data.getInt(data.getColumnIndex(NOTIFICATION_LED_COLOR));
        int small_icon = data.getInt(data.getColumnIndex(NOTIFICATION_SMALL_ICON));
        int large_icon = data.getInt(data.getColumnIndex(NOTIFICATION_LARGE_ICON));
        int color = data.getInt(data.getColumnIndex(NOTIFICATION_COLOR));
        String[] actions = NotifyMe.convertStringToArray(str_actions);
        String[] actions_text = NotifyMe.convertStringToArray(str_actions_text);
        String[] actions_dismiss = NotifyMe.convertStringToArray(str_actions_dismiss);
        String[] actions_collapse = NotifyMe.convertStringToArray(str_actions_collapse);
        data.close();
        db.close();

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context,notificationId);
        if(small_icon != -1) {
            mBuilder.setSmallIcon(small_icon);
        }else{
            mBuilder.setSmallIcon(R.drawable.ic_check_circle);
        }
        if(large_icon != -1) {
            Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), large_icon);
            mBuilder.setLargeIcon(largeIcon);
        }
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(content);
        mBuilder.setColor(color);
        mBuilder.setVibrate(new long[] { 1000,1000,1000 });
        for (int i = 0; i < actions.length; i++) {
            try {
                Intent tent = new Intent(context,ActionReceiver.class);
                tent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                tent.putExtra("_id",notificationId);
                tent.putExtra("index",i);
                tent.putExtra("action",actions[i]);
                tent.putExtra("collapse",Boolean.parseBoolean(actions_collapse[i]));
                tent.putExtra("dismiss",Boolean.parseBoolean(actions_dismiss[i]));
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context,Integer.parseInt(notificationId)*3+i,tent,PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.addAction(R.drawable.ic_check_circle,actions_text[i],pendingIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(uri);
        Intent deleteIntent = new Intent(context,DeletePendingIntent.class);
        deleteIntent.putExtra("_id",notificationId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,Integer.parseInt(notificationId),deleteIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setDeleteIntent(pendingIntent);
        Notification notification = mBuilder.build();

        notification.ledARGB = led_color;
        notification.flags = Notification.FLAG_SHOW_LIGHTS;
        notification.ledOnMS = 300;
        notification.ledOffMS = 1000;
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel nc = new NotificationChannel(notificationId,notificationId,NotificationManager.IMPORTANCE_HIGH);
            nc.enableLights(true);
            nc.setLightColor(led_color);
            mNotificationManager.createNotificationChannel(nc);
        }
        mNotificationManager.notify(Integer.parseInt(notificationId), notification);
    }


}