package com.edis.eschool.notification;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.edis.eschool.pojo.Notifications;
import com.edis.eschool.sql.DatabaseHelper;
import com.edis.eschool.user.UserDao;

public class NotificationDao {
    public static final String TABLE_NOTIFICATION = "notification";

    public static final String IDNOTIFICATION = "IDNOTIFICATION";
    public static final String TITRENOTIFICATION = "TITRENOTIFICATION";
    public static final String MESSAGENOTIFICATION = "MESSAGENOTIFICATION";
    public static final String IMAGENOTIFICATION = "IMAGENOTIFICATION";
    public static final String TYPENOTIFICATION = "TYPENOTIFICATION";
    public static final String DATENOTIFICATION = "DATENOTIFICATION";
    public static final String NOTIFICATIONLU = "NOTIFICATIONLU";

    DatabaseHelper databaseHelper;
    public NotificationDao(Context context){
        databaseHelper = DatabaseHelper.getInstance(context);
    }

    public Notifications insert(String  titre, String message, int imagenotification, String typenotification, String datenotification, int notificationlue) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TITRENOTIFICATION, titre);
        contentValues.put(MESSAGENOTIFICATION, message);
        contentValues.put(IMAGENOTIFICATION, imagenotification);
        contentValues.put(TYPENOTIFICATION, typenotification);
        contentValues.put(DATENOTIFICATION, datenotification);
        contentValues.put(NOTIFICATIONLU, notificationlue);
        long result = db.insert(TABLE_NOTIFICATION, null, contentValues);
        if (result == -1)
            return null;
        else
            return getNotification(result);
    }

    public Notifications getNotification(long id) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NOTIFICATION + " where " + IDNOTIFICATION + "='" + id + "'", null);
        Notifications notifications=new Notifications();
        while (res.moveToNext()) {
            notifications.setIdnotification(res.getInt(0));
            notifications.setTitre(res.getString(1));
            notifications.setMessage(res.getString(2));
            notifications.setImage(res.getInt(3));
            notifications.setType(res.getString(4));
            notifications.setDate(res.getString(5));
            notifications.setLu(res.getInt(6));
        }
        return notifications;
    }

    public Cursor getAllNewNotification(int lastId) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NOTIFICATION + " where " + IDNOTIFICATION + ">'" + lastId + "'", null);
        return res;
    }

    public boolean updcheckNotification(int idnotification) {
        String  idnotif= String.valueOf(idnotification);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NOTIFICATIONLU, 1);
        int result = db.update(TABLE_NOTIFICATION, contentValues, "" + IDNOTIFICATION + " = ? ", new String[]{idnotif});
        return true;
    }


    public Cursor getAllNotification() {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " +
                NotificationDao.TABLE_NOTIFICATION, null);
        return res;
    }

    ////  delete notification
    public void deleteNotifiction(int idnotification) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        db.execSQL("delete from " + NotificationDao.TABLE_NOTIFICATION + " where " +
                NotificationDao.IDNOTIFICATION + "='" + idnotification + "'");

    }
}
