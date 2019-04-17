package com.edis.eschool.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.edis.eschool.ClasseGlobal;
import com.edis.eschool.pojo.Eleve;
import com.edis.eschool.pojo.Notifications;

public class Databases extends SQLiteOpenHelper {
    private static final String TAG = "Databases";

    public static final String DATABASE_NAME = "eschool.db";
    public static final String TABLE_USER = "user";
    public static final String TABLE_NAME1 = "shool";
    public static final String TABLE_NOTIFICATION = "notification";

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
    public static final String IDNOTIFICATION = "IDNOTIFICATION";
    public static final String TITRENOTIFICATION = "TITRENOTIFICATION";
    public static final String MESSAGENOTIFICATION = "MESSAGENOTIFICATION";
    public static final String IMAGENOTIFICATION = "IMAGENOTIFICATION";
    public static final String TYPENOTIFICATION = "TYPENOTIFICATION";
    public static final String DATENOTIFICATION = "DATENOTIFICATION";
    public static final String NOTIFICATIONLU = "NOTIFICATIONLU";



    Context context;

    public Databases(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NOTIFICATION + " (" + IDNOTIFICATION + " INTEGER PRIMARY KEY AUTOINCREMENT," + TITRENOTIFICATION + " TEXT," + MESSAGENOTIFICATION + " TEXT," + IMAGENOTIFICATION + " INTEGER," + TYPENOTIFICATION + " TEXT," + DATENOTIFICATION + " TEXT," + NOTIFICATIONLU + " INTEGER)");
        db.execSQL("create table " + TABLE_USER + " (" + COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL_USER_VISITE + " INTEGER," + COL_USER_TOKEN + " TEXT)");
        db.execSQL("create table " + TABLE_NAME1 + " (" + COL_21 + " INTEGER," + COL_22 + " TEXT," + COL_23 + " INTEGER," + COL_24 + " TEXT," + COL_25 + " TEXT," + COL_26 + " TEXT)");
        //    db.execSQL("create table " + TABLE_NAME2 + " (" + COL_31 + " INTEGER," + COL_32 + " TEXT," + COL_33 + " TEXT," + COL_34 + " TEXT," + COL_35 + " TEXT," + COL_36 + " TEXT," + COL_37 + " TEXT," + COL_38 + " TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME1);
        //  db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME2);
        onCreate(db);
    }
    ////  delete notification
    public void deleteNotifiction(int idnotification) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_NOTIFICATION + " where " + IDNOTIFICATION + "='" + idnotification + "'");

    }

    ////////////////////////





    public void delete(String table_name) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(table_name, null, null);
    }

    public void deleteEcole(String idecole) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_NAME1 + " where " + COL_21 + "='" + idecole + "'");

    }

    public void deleteNetoyage(int visiste) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_USER + " where " + COL_USER_VISITE + "='" + visiste + "'");

    }

    /////////////////////////////////////
    // netoyage des ancien raport
    public void drop() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME1);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATION);
        onCreate(db);
    }



    public Notifications isertNotification(String  titre, String message, int imagenotification, String typenotification, String datenotification, int notificationlue) {
        SQLiteDatabase db = this.getWritableDatabase();
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
        SQLiteDatabase db = this.getWritableDatabase();
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

    public boolean isertVisite(int visite, Context context) {
        ClasseGlobal global = (ClasseGlobal) context;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_USER_VISITE, visite);
        contentValues.put(COL_USER_TOKEN, global.getToken());
        long result = db.insert(TABLE_USER, null, contentValues);
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
        int result = db.update(TABLE_USER, contentValues, "" + COL_USER_ID + " = ? ", new String[]{identifiant});
        Log.e("tokenfibasesgg", String.valueOf(result));
        return true;
    }

    public String getSavedToken(String identifiant){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_USER + " where " +
                COL_USER_ID + "='" + identifiant + "'", null);
        if(res.moveToFirst()) {
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
        int id = (int) db.insertWithOnConflict(TABLE_NAME1, null, initialValues, SQLiteDatabase.CONFLICT_IGNORE);
        Log.e("validation", String.valueOf(id) + " id insertion");
        if (id == -1) {
            db.update(TABLE_NAME1, initialValues, COL_21 + "=?", new String[]{"1"});  // number 1 is the _id here, update to variable for your code
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
    public Cursor getAllNotification() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NOTIFICATION, null);
        return res;
    }
    public Cursor getAllVisite() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_USER, null);
        return res;
    }

    public Cursor getAllShool() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME1, null);
        return res;
    }

    public Cursor getAllSchoolID(int IDSchool) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME1 + " where " + COL_21 + "='" + IDSchool + "'", null);
        return res;
    }

    public boolean getAllSchoolID(String Chemin, String Serveur) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME1 + " where " + COL_25 + "='" + Chemin + "' and " + COL_26 + "='" + Serveur + "'", null);
        if (res.getCount() > 0) {
            return true;
        } else {
            return false;
        }
    }


    public boolean getElevepariculier(Eleve eleve) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME1 + " where " + COL_21 + "='" + eleve.getIdeleve() + "' and " + COL_25 + "='" + eleve.getNom() + "'", null);
        if (res.getCount() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public Cursor gettuto(String idoperateur) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_USER + " where " + COL_USER_ID + "='" + idoperateur + "'", null);
        return res;
    }

    public Cursor getAllNewNotification(int lastId) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NOTIFICATION + " where " + IDNOTIFICATION + ">'" + lastId + "'", null);
        return res;
    }

    public boolean updcheckNotification(int idnotification) {
       String  idnotif= String.valueOf(idnotification);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NOTIFICATIONLU, 1);
        int result = db.update(TABLE_NOTIFICATION, contentValues, "" + IDNOTIFICATION + " = ? ", new String[]{idnotif});
        return true;
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
