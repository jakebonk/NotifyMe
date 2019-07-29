package com.allyants.notifyme;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by jbonk on 6/19/2018.
 */

public class Notification {

    private Notification(){}

    public static class NotificationEntry implements BaseColumns{
        public static final String TABLE_NAME = "notification";
        public static final String NOTIFICATION_TITLE_TEXT = "title";
        public static final String NOTIFICATION_TIME = "time";
        public static final String NOTIFICATION_DSTART = "dstart";
        public static final String NOTIFICATION_RRULE = "rrule";
        public static final String NOTIFICATION_ACTIONS = "actions";
        public static final String NOTIFICATION_CUSTOM_ID = "custom_id";
        public static final String NOTIFICATION_ACTIONS_TEXT = "actions_text";
        public static final String NOTIFICATION_ACTIONS_DISMISS = "actions_dismiss";
        public static final String NOTIFICATION_ACTIONS_COLLAPSE = "actions_collapse";
        public static final String NOTIFICATION_COLOR = "color";
        public static final String NOTIFICATION_LED_COLOR = "led_color";
        public static final String NOTIFICATION_SMALL_ICON = "small_icon";
        public static final String NOTIFICATION_LARGE_ICON = "large_icon";
        public static final String NOTIFICATION_CONTENT_TEXT = "content";
    }

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + NotificationEntry.TABLE_NAME + " (" +
                    NotificationEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    NotificationEntry.NOTIFICATION_TITLE_TEXT + " TEXT," +
                    NotificationEntry.NOTIFICATION_TIME + " LONG," +
                    NotificationEntry.NOTIFICATION_DSTART + " LONG," +
                    NotificationEntry.NOTIFICATION_ACTIONS + " TEXT," +
                    NotificationEntry.NOTIFICATION_ACTIONS_TEXT + " TEXT," +
                    NotificationEntry.NOTIFICATION_COLOR + " INTEGER," +
                    NotificationEntry.NOTIFICATION_ACTIONS_DISMISS + " TEXT," +
                    NotificationEntry.NOTIFICATION_RRULE + " TEXT," +
                    NotificationEntry.NOTIFICATION_LED_COLOR + " INTEGER," +
                    NotificationEntry.NOTIFICATION_CUSTOM_ID + " TEXT," +
                    NotificationEntry.NOTIFICATION_SMALL_ICON + " TEXT," +
                    NotificationEntry.NOTIFICATION_LARGE_ICON + " TEXT," +
                    NotificationEntry.NOTIFICATION_ACTIONS_COLLAPSE + " TEXT," +
                    NotificationEntry.NOTIFICATION_CONTENT_TEXT + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + NotificationEntry.TABLE_NAME;


    public static class NotificationDBHelper extends SQLiteOpenHelper {
        // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 2;
        public static final String DATABASE_NAME = "Notification.db";

        public NotificationDBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }
}
