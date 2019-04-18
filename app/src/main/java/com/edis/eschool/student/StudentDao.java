package com.edis.eschool.student;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.TabLayout;
import android.util.Log;

import com.edis.eschool.notification.NotificationDao;
import com.edis.eschool.pojo.Student;
import com.edis.eschool.sql.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class StudentDao {
    private static final String TAG = "StudentDao";
    public static final String COL_STUDENT_ID = "ID";
    public static final String COL_STUDENT_FIRSTNAME = "FIRSTNAME";
    public static final String COL_STUDENT_LASTNAME = "LASTNAME";
    public static final String COL_STUDENT_CLASSE = "CLASSE";
    public static final String COL_STUDENT_ETABLISSEMENT = "ETABLISSEMENT";
    public static final String COL_STUDENT_SEXE = "SEXE";
    public static final String TABLE_STUDENT = "students";

    public static final String DATENOTIFICATION = "DATENOTIFICATION";
    public static final String NOTIFICATIONLU = "NOTIFICATIONLU";



    DatabaseHelper databaseHelper;
    public StudentDao(Context context){
        databaseHelper = DatabaseHelper.getInstance(context);
    }

    public List<Student> getStudentList(){
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " +
                TABLE_STUDENT, null);

        List<Student> studentList = new ArrayList<>();

        while(res.moveToNext()){
            Student st = new Student(res.getInt(res.getColumnIndex(COL_STUDENT_ID)),
                    res.getString(res.getColumnIndex(COL_STUDENT_FIRSTNAME)),
                    res.getString(res.getColumnIndex(COL_STUDENT_LASTNAME)),
                    res.getString(res.getColumnIndex(COL_STUDENT_SEXE)),
                    res.getString(res.getColumnIndex(COL_STUDENT_CLASSE)),
                    res.getString(res.getColumnIndex(COL_STUDENT_ETABLISSEMENT)));
            studentList.add(st);
        }
        return studentList;
    }

    public boolean insert(Student st){
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_STUDENT_FIRSTNAME, st.getFirstName());
        contentValues.put(COL_STUDENT_LASTNAME, st.getLastName());
        contentValues.put(COL_STUDENT_SEXE, st.getSexe());
        contentValues.put(COL_STUDENT_CLASSE, st.getClasse());
        contentValues.put(COL_STUDENT_ETABLISSEMENT, st.getEtablissement());
        long result = db.insert(TABLE_STUDENT, null, contentValues);
        Log.i(TAG, result + "");
        db.close();
        if (result == -1) {
            return false;
        }else {
            return true;
        }
    }
}
