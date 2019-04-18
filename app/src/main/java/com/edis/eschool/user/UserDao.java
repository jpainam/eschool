package com.edis.eschool.user;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.edis.eschool.pojo.User;
import com.edis.eschool.sql.DatabaseHelper;

public class UserDao {
    public static final String TABLE_USER = "user";
    public static final String COL_USER_ID = "ID";
    public static final String COL_USER_TOKEN = "TOKEN";
    public static final String TABLE_NAME1 = "shool";
    public Context context;
    DatabaseHelper databaseHelper;
    public UserDao(Context context){
        this.context = context;
        databaseHelper = DatabaseHelper.getInstance(context);
    }
    public User getUser(){
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + UserDao.TABLE_USER, null);
        if (res.moveToFirst()) {
            User user = new User();
            user.setIduser(res.getString(res.getColumnIndex(COL_USER_ID)));
            user.setToken(res.getString(res.getColumnIndex(COL_USER_TOKEN)));
            return user;
        }
        return null;
    }
}
