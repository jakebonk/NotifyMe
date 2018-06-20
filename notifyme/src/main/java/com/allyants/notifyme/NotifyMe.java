package com.allyants.notifyme;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.StringRes;
import android.support.annotation.UiThread;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

import static android.content.Context.ALARM_SERVICE;
import static com.allyants.notifyme.Notification.NotificationEntry.NOTIFICATION_ACTIONS;
import static com.allyants.notifyme.Notification.NotificationEntry.NOTIFICATION_ACTIONS_COLLAPSE;
import static com.allyants.notifyme.Notification.NotificationEntry.NOTIFICATION_ACTIONS_DISMISS;
import static com.allyants.notifyme.Notification.NotificationEntry.NOTIFICATION_ACTIONS_TEXT;
import static com.allyants.notifyme.Notification.NotificationEntry.NOTIFICATION_COLOR;
import static com.allyants.notifyme.Notification.NotificationEntry.NOTIFICATION_CONTENT_TEXT;
import static com.allyants.notifyme.Notification.NotificationEntry.NOTIFICATION_CUSTOM_ID;
import static com.allyants.notifyme.Notification.NotificationEntry.NOTIFICATION_LARGE_ICON;
import static com.allyants.notifyme.Notification.NotificationEntry.NOTIFICATION_SMALL_ICON;
import static com.allyants.notifyme.Notification.NotificationEntry.NOTIFICATION_LED_COLOR;
import static com.allyants.notifyme.Notification.NotificationEntry.NOTIFICATION_TIME;
import static com.allyants.notifyme.Notification.NotificationEntry.NOTIFICATION_TITLE_TEXT;
import static com.allyants.notifyme.Notification.NotificationEntry.TABLE_NAME;
import static com.allyants.notifyme.NotificationPublisher.NOTIFICATION_ID;

/**
 * Created by jbonk on 6/16/2018.
 */

public class NotifyMe {

    protected final Builder builder;
    private static String strSeparator = "__,__";


    protected NotifyMe(Builder builder){
        this.builder = builder;
        Calendar cal = Calendar.getInstance();
        cal.setTime(builder.time);
        cal.add(Calendar.MILLISECOND,builder.delay);
        Notification.NotificationDBHelper mDbHelper = new Notification.NotificationDBHelper(builder.context);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        if(builder.title == null)
            builder.title("");
        if(builder.content == null)
            builder.content("");
        if(builder.key == null)
            builder.key = "";
        values.put(NOTIFICATION_TITLE_TEXT,String.valueOf(builder.title));
        values.put(NOTIFICATION_CONTENT_TEXT,String.valueOf(builder.content));
        values.put(NOTIFICATION_TIME,cal.getTimeInMillis());
        values.put(NOTIFICATION_ACTIONS,convertArrayToString(builder.actions));
        values.put(NOTIFICATION_ACTIONS_TEXT,convertArrayToString(builder.actions_text));
        values.put(NOTIFICATION_ACTIONS_DISMISS,convertArrayToString(builder.actions_dismiss));
        values.put(NOTIFICATION_ACTIONS_COLLAPSE,convertArrayToString(builder.actions_collapse));
        values.put(NOTIFICATION_CUSTOM_ID,String.valueOf(builder.key));
        values.put(NOTIFICATION_LED_COLOR,String.valueOf(builder.led_color));
        values.put(NOTIFICATION_COLOR,builder.color);
        values.put(NOTIFICATION_SMALL_ICON,builder.small_icon);
        values.put(NOTIFICATION_LARGE_ICON,builder.large_icon);
        long id = db.insert(Notification.NotificationEntry.TABLE_NAME,null,values);
        db.close();
        scheduleNotification(builder.context,String.valueOf(id),cal.getTimeInMillis());
    }

    public static String convertArrayToString(String[] array){
        String str = "";
        for (int i = 0;i<array.length; i++) {
            str = str+array[i];
            // Do not append comma at the end of last element
            if(i<array.length-1){
                str = str+strSeparator;
            }
        }
        return str;
    }
    public static String[] convertStringToArray(String str){
        String[] arr = str.split(strSeparator);
        return arr;
    }

    public static void cancel(Context context,int notificationId){
        try {
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification.NotificationDBHelper mDbHelper = new Notification.NotificationDBHelper(context);
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            db.delete(TABLE_NAME, com.allyants.notifyme.Notification.NotificationEntry._ID + " = " + notificationId, null);
            db.close();
            mNotificationManager.cancel(notificationId);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void cancel(Context context,String key){
        try {
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification.NotificationDBHelper mDbHelper = new Notification.NotificationDBHelper(context);
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + Notification.NotificationEntry.TABLE_NAME + " WHERE custom_id = ? LIMIT 1", new String[]{key});
            cursor.moveToFirst();
            int notificationId = cursor.getInt(cursor.getColumnIndex(Notification.NotificationEntry._ID));
            db.delete(TABLE_NAME, com.allyants.notifyme.Notification.NotificationEntry._ID + " = " + notificationId, null);
            db.close();
            cursor.close();
            mNotificationManager.cancel(notificationId);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void init(Context context){
        Notification.NotificationDBHelper mDbHelper = new Notification.NotificationDBHelper(context);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+Notification.NotificationEntry.TABLE_NAME+" WHERE 1=1",null);
        while(cursor.moveToNext()){
            Long time = cursor.getLong(cursor.getColumnIndex(NOTIFICATION_TIME));
            int id = cursor.getInt(cursor.getColumnIndex(Notification.NotificationEntry._ID));
            scheduleNotification(context,String.valueOf(id),time);
        }
        cursor.close();
        db.close();
    }

    public final Builder getBuilder(){
        return this.builder;
    }

    private static void scheduleNotification(Context context, String notificationId,Long time) {
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationPublisher.class);
        intent.putExtra(NOTIFICATION_ID,notificationId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,Integer.parseInt(notificationId),intent,PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP,time,pendingIntent);
    }

    public static class Builder{

        protected Context context;
        protected CharSequence title,content,key;
        protected Long id;
        protected int delay = 0;
        protected Date time = new Date();
        protected String[] actions = new String[0];
        protected String[] actions_text = new String[0];
        protected String[] actions_dismiss = new String[0];
        protected String[] actions_collapse = new String[0];
        protected int color = -1;
        protected int led_color = 0;
        protected int small_icon = -1;
        protected int large_icon = -1;

        public Builder(Context context){
            this.context = context;
        }

        public Builder delay(int delay){
            this.delay = delay;
            return this;
        }

        public Builder small_icon(int small_icon){
            this.small_icon = small_icon;
            return this;
        }

        public Builder large_icon(int large_icon){
            this.large_icon = large_icon;
            return this;
        }

        public Builder led_color(int red,int green, int blue,int alpha){
            int color = (alpha & 0xff) << 24 | (red & 0xff) << 16 | (green & 0xff) << 8 | (blue & 0xff);
            this.led_color = color;
            return this;
        }


        public Builder color(int red,int green, int blue,int alpha){
            int color = (alpha & 0xff) << 24 | (red & 0xff) << 16 | (green & 0xff) << 8 | (blue & 0xff);
            this.color = color;
            return this;
        }

        public Builder addAction(Intent intent,String text){
            return addAction(intent,text,true,true);
        }

        public Builder addAction(Intent intent,String text,boolean dismiss){
            return addAction(intent,text,dismiss,true);
        }

        public Builder addAction(Intent intent,String text,boolean dismiss,boolean collapse){
            String[] temp = new String[actions.length+1];
            for (int i = 0; i < this.actions.length; i++) {
                temp[i] = this.actions[i];
            }
            String[] temp_collapse = new String[actions_collapse.length+1];
            for (int i = 0; i < this.actions_collapse.length; i++) {
                temp_collapse[i] = this.actions_collapse[i];
            }
            String[] temp_text = new String[actions_text.length+1];
            for (int i = 0; i < this.actions_text.length; i++) {
                temp_text[i] = this.actions_text[i];
            }
            String[] temp_dismiss = new String[actions_dismiss.length+1];
            for(int i = 0; i < this.actions_dismiss.length;i++){
                temp_dismiss[i] = this.actions_dismiss[i];
            }
            temp_dismiss[actions_dismiss.length] = String.valueOf(dismiss);
            temp_collapse[actions_collapse.length] = String.valueOf(collapse);
            temp_text[actions_text.length] = text;
            temp[actions.length] = intent.toUri(0);
            this.actions_text = temp_text;
            this.actions = temp;
            this.actions_dismiss = temp_dismiss;
            this.actions_collapse = temp_collapse;
            return this;
        }

        public Builder time(Date time){
            this.time = time;
            return this;
        }

        public Builder time(Calendar time){
            this.time = time.getTime();
            return this;
        }

        public Builder title(CharSequence title){
            this.title = title;
            return this;
        }

        public Builder title(@StringRes int title){
            title(this.context.getText(title));
            return this;
        }

        public Builder title(String title){
            this.title = title;
            return this;
        }


        public Builder key(CharSequence key){
            this.key = key;
            return this;
        }

        public Builder key(@StringRes int key){
            key(this.context.getText(key));
            return this;
        }

        public Builder key(String key){
            this.key = key;
            return this;
        }

        public Builder content(CharSequence content){
            this.content = content;
            return this;
        }

        public Builder content(@StringRes int content){
            title(this.context.getText(content));
            return this;
        }

        public Builder content(String content){
            this.content = content;
            return this;
        }

        @UiThread
        public NotifyMe build(){
            return new NotifyMe(this);
        }

    }

}
