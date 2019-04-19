package com.edis.eschool.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.edis.eschool.ClasseGlobal;
import com.edis.eschool.notification.NotificationDao;
import com.edis.eschool.pojo.Eleve;
import com.edis.eschool.pojo.Notifications;
import com.edis.eschool.pojo.Student;
import com.edis.eschool.pojo.User;
import com.edis.eschool.student.StudentDao;
import com.edis.eschool.user.UserDao;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";

    private static DatabaseHelper sInstance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    public static final String DATABASE_NAME = "eschool.db";

    ///////////////////////////////////////////
    public static final String COL_A = "ID";
    public static final String COL_B = "CONNECT";

    ///////////////////////

    /////////////////////////////

    //  TABLE USER
    public static final String COL_USER_ID = "ID";
    public static final String COL_USER_VISITE = "VISITE";
    public static final String COL_USER_TOKEN = "TOKEN";

    //  colonne  Eleve
    public static final String COL_21 = "IDELEVE";
    public static final String COL_22 = "MATRICULE";
    public static final String COL_23 = "IDPARENT";
    public static final String COL_24 = "GENRE";
    public static final String COL_25 = "NOMELEVE";
    public static final String COL_26 = "PRENOMELEVE";


    //  colonne  personne
    public static final String COL_31 = "IDUSER";
    public static final String COL_32 = "NOM";
    public static final String COL_33 = "PRENOM";
    public static final String COL_34 = "NUMPHONE";
    public static final String COL_35 = "PASSWORD";
    public static final String COL_36 = "ACTIF";
    public static final String COL_37 = "TEMPS";
    public static final String COL_38 = "TYPE";


    //  colonne notification


    Context context;

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + NotificationDao.TABLE_NOTIFICATION +
                " (" + NotificationDao.IDNOTIFICATION + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                NotificationDao.TITRENOTIFICATION + " TEXT," +
                NotificationDao.MESSAGENOTIFICATION + " TEXT," +
                NotificationDao.IMAGENOTIFICATION + " INTEGER," +
                NotificationDao.TYPENOTIFICATION + " TEXT," +
                NotificationDao.DATENOTIFICATION + " TEXT," +
                NotificationDao.NOTIFICATIONLU + " INTEGER)");

        db.execSQL("create table " + StudentDao.TABLE_STUDENT +
                " (" + StudentDao.COL_STUDENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                StudentDao.COL_STUDENT_FIRSTNAME + " TEXT," +
                StudentDao.COL_STUDENT_LASTNAME + " TEXT," +
                StudentDao.COL_STUDENT_SEXE + " TEXT," +
                StudentDao.COL_STUDENT_CLASSE + " TEXT," +
                StudentDao.COL_STUDENT_ETABLISSEMENT + " TEXT)");

        db.execSQL("create table " + UserDao.TABLE_USER + " (" + COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_USER_VISITE + " INTEGER," + COL_USER_TOKEN + " TEXT)");
        db.execSQL("create table " + UserDao.TABLE_NAME1 + " (" + COL_21 + " INTEGER," + COL_22 + " TEXT," +
                COL_23 + " INTEGER," + COL_24 + " TEXT," + COL_25 + " TEXT," + COL_26 + " TEXT)");
        //    db.execSQL("create table " + TABLE_NAME2 + " (" + COL_31 + " INTEGER," + COL_32 + " TEXT," + COL_33 + " TEXT," + COL_34 + " TEXT," + COL_35 + " TEXT," + COL_36 + " TEXT," + COL_37 + " TEXT," + COL_38 + " TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + NotificationDao.TABLE_NOTIFICATION);
        db.execSQL("DROP TABLE IF EXISTS " + UserDao.TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + StudentDao.TABLE_STUDENT);
        onCreate(db);
    }
}
