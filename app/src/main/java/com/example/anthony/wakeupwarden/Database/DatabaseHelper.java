package com.example.anthony.wakeupwarden.Database;
// DATABASE is similar to the code from Assignment 4
// functions describes the actions

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.example.anthony.wakeupwarden.Alarm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    // All Static variables
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = Config.DATABASE_NAME;

    private Context context = null;
    // Constructor
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }
/*
    public static synchronized DatabaseHelper getInstance(Context context){
        if(databaseHelper==null){
            databaseHelper = new DatabaseHelper(context);
        }
        return databaseHelper;
    }
*/
    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create tables SQL execution
        // course table
        String CREATE_ALARM_TABLE = "CREATE TABLE " + Config.TABLE_ALARM + "("
                + Config.COLUMN_ALARM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Config.COLUMN_ALARM_TITLE + " TEXT, "
                + Config.COLUMN_ALARM_ENABLE + " TEXT, "
                + Config.COLUMN_ALARM_TIME + " TEXT NOT NULL "
                + ")";


        Log.d(TAG,"Table create SQL: " + CREATE_ALARM_TABLE);

        db.execSQL(CREATE_ALARM_TABLE);

        Log.d(TAG,"DB created!");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + Config.TABLE_ALARM);
        // Create tables again
        onCreate(db);
    }

    //function used to insert course to the course table
    public long insertAlarm(Alarm alarm){

        long id = -1;
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();


        ContentValues contentValues = new ContentValues();
        contentValues.put(Config.COLUMN_ALARM_TIME, alarm.getTitle());
        contentValues.put(Config.COLUMN_ALARM_TIME, alarm.getTime());
        contentValues.put(Config.COLUMN_ALARM_ENABLE, alarm.getEnable());


        try {
            id = sqLiteDatabase.insertOrThrow(Config.TABLE_ALARM, null, contentValues);
        } catch (SQLiteException e){
            Log.d(TAG,"Exception: " + e.getMessage());
            Toast.makeText(context, "Operation failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            sqLiteDatabase.close();
        }
        return id;
    }
    //function used to insert Assignment into assignment table

    public List<Alarm> getAllAlarms(){

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        Cursor cursor = null;
        try {

            cursor = sqLiteDatabase.query(Config.TABLE_ALARM, null, null, null, null, null, null, null);

            /**
             // If you want to execute raw query then uncomment below 2 lines. And comment out above line.
             String SELECT_QUERY = String.format("SELECT %s, %s, %s, %s, %s FROM %s", Config.COLUMN_STUDENT_ID, Config.COLUMN_STUDENT_NAME, Config.COLUMN_STUDENT_REGISTRATION, Config.COLUMN_STUDENT_EMAIL, Config.COLUMN_STUDENT_PHONE, Config.TABLE_STUDENT);
             cursor = sqLiteDatabase.rawQuery(SELECT_QUERY, null);
             */

            if(cursor!=null)
                if(cursor.moveToFirst()){
                    List<Alarm> alarmList = new ArrayList<>();
                    do {
                        int id = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_ALARM_ID));
                        String title = cursor.getString(cursor.getColumnIndex(Config.COLUMN_ALARM_TITLE));
                        String time = cursor.getString(cursor.getColumnIndex(Config.COLUMN_ALARM_TIME));
                        int enable = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_ALARM_ENABLE));
                        Boolean enableAlarm;
                        if (enable == 1){
                            enableAlarm = true;
                        }
                        else
                            enableAlarm = false;

                        alarmList.add(new Alarm(id, title, time, enableAlarm));
                    }   while (cursor.moveToNext());

                    return alarmList;
                }
        } catch (Exception e){
            Log.d(TAG,"Exception: " + e.getMessage());
            Toast.makeText(context, "Operation failed", Toast.LENGTH_SHORT).show();
        } finally {
            if(cursor!=null)
                cursor.close();
            sqLiteDatabase.close();
        }

        return Collections.emptyList();
    }

    public Alarm getAlarmById(long Id){

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        Cursor cursor = null;
        Alarm alarm = null;
        try {

           cursor = sqLiteDatabase.rawQuery("select * from " + Config.TABLE_ALARM + " where " + Config.COLUMN_ALARM_ID + "='" + Id + "'" , null);


            if(cursor!=null)
                if(cursor.moveToFirst()){

                    do {
                        int id = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_ALARM_ID));
                        String title = cursor.getString(cursor.getColumnIndex(Config.COLUMN_ALARM_TITLE));
                        String time = cursor.getString(cursor.getColumnIndex(Config.COLUMN_ALARM_TIME));
                        int enable = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_ALARM_ENABLE));
                        Boolean enableAlarm;
                        if (enable == 1){
                            enableAlarm = true;
                        }
                        else
                            enableAlarm = false;

                        alarm = new Alarm(id, title, time, enableAlarm);

                    }   while (cursor.moveToNext());

                    return alarm;
                }
        } catch (Exception e){
            Log.d(TAG,"Exception: " + e.getMessage());
            Toast.makeText(context, "Operation failed", Toast.LENGTH_SHORT).show();
        } finally {
            if(cursor!=null)
                cursor.close();
            sqLiteDatabase.close();
        }

        return alarm;
    }
    // function not used

    public long updateAlarmInfo(Alarm alarm){

        long rowCount = 0;
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Config.COLUMN_ALARM_TITLE, alarm.getTitle());
        contentValues.put(Config.COLUMN_ALARM_TIME, alarm.getTime());
        contentValues.put(Config.COLUMN_ALARM_ENABLE, alarm.getEnable());


        try {
            rowCount = sqLiteDatabase.update(Config.TABLE_ALARM, contentValues,
                    Config.COLUMN_ALARM_ID + " = ? ",
                    new String[] {String.valueOf(alarm.getId())});
        } catch (SQLiteException e){
            Log.d(TAG,"Exception: " + e.getMessage());
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            sqLiteDatabase.close();
        }

        return rowCount;
    }
    // function not used

    public long deleteAlarmByID(String ID) {
        long deletedRowCount = -1;
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        try {
            deletedRowCount = sqLiteDatabase.delete(Config.TABLE_ALARM,
                    Config.COLUMN_ALARM_ID + " = ? ",
                    new String[]{ ID });
        } catch (SQLiteException e){
            Log.d(TAG,"Exception: " + e.getMessage());
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            sqLiteDatabase.close();
        }

        return deletedRowCount;
    }
    // function not used
    public boolean deleteAllCourses(){
        boolean deleteStatus = false;
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        try {
            //for "1" delete() method returns number of deleted rows
            //if you don't want row count just use delete(TABLE_NAME, null, null)
            sqLiteDatabase.delete(Config.TABLE_ALARM, null, null);

            long count = DatabaseUtils.queryNumEntries(sqLiteDatabase, Config.TABLE_ALARM);

            if(count==0)
                deleteStatus = true;

        } catch (SQLiteException e){
            Log.d(TAG,"Exception: " + e.getMessage());
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            sqLiteDatabase.close();
        }

        return deleteStatus;
    }

}