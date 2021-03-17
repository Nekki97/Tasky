package com.example.tasky;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DB_Adapter {
    DB_Helper myhelper;
    public DB_Adapter(Context context) {
        myhelper = new DB_Helper(context);
    }

    public long insertData(String name, String harddl, String start_date) {
        SQLiteDatabase dbb = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DB_Helper.NAME, name);
        contentValues.put(DB_Helper.HARDDL, harddl);
        contentValues.put(DB_Helper.START_DATE, start_date);
        return dbb.insert(DB_Helper.TABLE_NAME, null , contentValues);
    }

    public String getData() {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] columns = {DB_Helper.UID, DB_Helper.NAME, DB_Helper.HARDDL, DB_Helper.START_DATE};
        @SuppressLint("Recycle") Cursor cursor =db.query(DB_Helper.TABLE_NAME,columns,null,null,null,null,null);
        StringBuilder buffer= new StringBuilder();
        while (cursor.moveToNext()) {
            int cid =cursor.getInt(cursor.getColumnIndex(DB_Helper.UID));
            String name = cursor.getString(cursor.getColumnIndex(DB_Helper.NAME));
            String harddl = cursor.getString(cursor.getColumnIndex(DB_Helper.HARDDL));
            String start_date = cursor.getString(cursor.getColumnIndex(DB_Helper.START_DATE));
            buffer.append(cid).append("   ").append(name).append("   ").append(harddl).append("   ").append(start_date).append(" \n");
        }
        return buffer.toString();
    }

    public void delete(String UID) {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] whereArgs = {UID};

        db.delete(DB_Helper.TABLE_NAME, DB_Helper.UID + " = ?", whereArgs);
    }

    public int updateValue(String type, String oldVal , String newVal) {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        int count = 0;
        String[] whereArgs;
        switch (type) {
            case "Name":
                contentValues.put(DB_Helper.NAME,newVal);
                whereArgs = new String[]{oldVal};
                count = db.update(DB_Helper.TABLE_NAME,contentValues, DB_Helper.NAME+" = ?",whereArgs );
                break;
            case "HardDL":
                contentValues.put(DB_Helper.HARDDL,newVal);
                whereArgs = new String[]{oldVal};
                count = db.update(DB_Helper.TABLE_NAME,contentValues, DB_Helper.HARDDL+" = ?",whereArgs );
                break;
            case "Start_Date":
                contentValues.put(DB_Helper.START_DATE,newVal);
                whereArgs = new String[]{oldVal};
                count = db.update(DB_Helper.TABLE_NAME,contentValues, DB_Helper.START_DATE+" = ?",whereArgs );
                break;
        }
        return count;
    }

    static class DB_Helper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "myDatabase";    // Database Name
        private static final String TABLE_NAME = "myTable";   // Table Name
        private static final int DATABASE_Version = 1;    // Database Version

        private static final String UID= "_id";     // Column I (Primary Key)
        private static final String NAME = "Name";    //Column II
        private static final String HARDDL= "_5";    // Column III
        private static final String START_DATE = "_22_09_2021";

        private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +
                " ("+UID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
                +NAME+"         VARCHAR(255), "
                +HARDDL+"       VARCHAR(255),"
                +START_DATE+"   VARCHAR(255));";

        private static final String DROP_TABLE ="DROP TABLE IF EXISTS "+TABLE_NAME;
        private final Context context;

        public DB_Helper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_Version);
            this.context=context;
            this.getWritableDatabase();
        }

        public void onCreate(SQLiteDatabase db) {

            try {
                db.execSQL(CREATE_TABLE);
            } catch (Exception e) {
                Message.message(context,""+e);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                Message.message(context,"OnUpgrade");
                db.execSQL(DROP_TABLE);
                onCreate(db);
            } catch (Exception e) {
                Message.message(context,""+e);
            }
        }
    }
}