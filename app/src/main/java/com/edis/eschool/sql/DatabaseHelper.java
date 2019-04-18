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


    public void delete(String table_name) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(table_name, null, null);
    }

    public void deleteEcole(String idecole) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + UserDao.TABLE_NAME1 + " where " + COL_21 + "='" + idecole + "'");

    }

    public void deleteNetoyage(int visiste) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + UserDao.TABLE_USER + " where " + COL_USER_VISITE + "='" + visiste + "'");

    }


    public boolean isertVisite(int visite, Context context) {
        ClasseGlobal global = (ClasseGlobal) context;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_USER_VISITE, visite);
        contentValues.put(COL_USER_TOKEN, global.getToken());
        long result = db.insert(UserDao.TABLE_USER, null, contentValues);
        if (result == -1)
            return false;
        else
            return true;
    }


    // tuto
    public boolean updateDatatuto(String token, String identifiant) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        Log.e("tokenfibases", token);
        contentValues.put(COL_USER_TOKEN, token);
        Log.e("tokenfibasesG", token);
        int result = db.update(UserDao.TABLE_USER, contentValues, "" + COL_USER_ID + " = ? ", new String[]{identifiant});
        Log.e("tokenfibasesgg", String.valueOf(result));
        return true;
    }

    public String getSavedToken(String identifiant) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + UserDao.TABLE_USER + " where " +
                COL_USER_ID + "='" + identifiant + "'", null);
        if (res.moveToFirst()) {
            return res.getString(res.getColumnIndex(COL_USER_TOKEN));
        }
        Log.e(TAG, "TOKEN NOT SET");
        return new String("TOKEN NOT SET");
    }

 /*   public boolean isertConnexion(String conne){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_B,conne);
        long result = db.insert(TABLE_NAME2,null ,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }
    */
/*public boolean isertConnexion(int idconne,String conne){
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues initialValues = new ContentValues();
    initialValues.put(COL_A,idconne);
    initialValues.put(COL_B,conne);
    int id = (int) db.insertWithOnConflict(TABLE_NAME2, null, initialValues, SQLiteDatabase.CONFLICT_IGNORE);
    Log.e("validation",String.valueOf(id )+" id insertion");
    if (id == -1) {
        db.update(TABLE_NAME2, initialValues, COL_A+"=?", new String[] {"1"});  // number 1 is the _id here, update to variable for your code
        Log.e("validation","true 1");
        return true;
    }else{
        if(id>0){
            Log.e("validation","true 2");
            return true;
        } else{
            Log.e("validation","false");
            return false;
        }
    }

}
*/


    public boolean insertOrUpdate(int idEcole, String idParent, String Numphone, String NomEcole, String Chemin, String Serveur) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues initialValues = new ContentValues();
        Log.e("info", String.valueOf(idEcole));
        initialValues.put(COL_21, idEcole);
        if (idParent != "0") {
            initialValues.put(COL_22, idParent);
        }
        if (Numphone != null) {
            initialValues.put(COL_23, Numphone);
        }
        if (NomEcole != null) {
            initialValues.put(COL_24, NomEcole);
        }
        if (Chemin != null) {
            initialValues.put(COL_25, Chemin);
        }
        if (Serveur != null) {
            initialValues.put(COL_26, Serveur);
        }
        int id = (int) db.insertWithOnConflict(UserDao.TABLE_NAME1, null, initialValues, SQLiteDatabase.CONFLICT_IGNORE);
        Log.e("validation", String.valueOf(id) + " id insertion");
        if (id == -1) {
            db.update(UserDao.TABLE_NAME1, initialValues, COL_21 + "=?", new String[]{"1"});  // number 1 is the _id here, update to variable for your code
            Log.e("validation", "true 1");
            return true;
        } else {
            if (id > 0) {
                Log.e("validation", "true 2");
                return true;
            } else {
                Log.e("validation", "false");
                return false;
            }
        }

    }


    ///////////////////////////
/*public Cursor getAllconnexion() {
    SQLiteDatabase db = this.getWritableDatabase();
    Cursor res = db.rawQuery("select * from "+TABLE_NAME2,null);
    return res;
}*/

    public Cursor getAllVisite() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + UserDao.TABLE_USER, null);
        return res;
    }

    public Cursor getAllShool() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + UserDao.TABLE_NAME1, null);
        return res;
    }

    public Cursor getAllSchoolID(int IDSchool) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + UserDao.TABLE_NAME1 + " where " + COL_21 + "='" + IDSchool + "'", null);
        return res;
    }

    public boolean getAllSchoolID(String Chemin, String Serveur) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + UserDao.TABLE_NAME1 + " where " + COL_25 + "='" + Chemin + "' and " + COL_26 + "='" + Serveur + "'", null);
        if (res.getCount() > 0) {
            return true;
        } else {
            return false;
        }
    }


    public boolean getElevepariculier(Eleve eleve) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + UserDao.TABLE_NAME1 + " where " + COL_21 + "='" + eleve.getIdeleve() + "' and " + COL_25 + "='" + eleve.getNom() + "'", null);
        if (res.getCount() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public Cursor gettuto(String idoperateur) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + UserDao.TABLE_USER + " where " + COL_USER_ID + "='" + idoperateur + "'", null);
        return res;
    }


    //////
   /* public boolean updateData(String NOM_OPERATEUR,int champ) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_31,champ);
        db.update(TABLE_NAME1, contentValues, ""+COL_21+" = ? ",new String[] { NOM_OPERATEUR });
        return true;
    }*/

}
