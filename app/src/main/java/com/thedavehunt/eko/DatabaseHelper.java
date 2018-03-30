package com.thedavehunt.eko;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "message.db";

    public static final String MSG_TABLE_NAME = "messages";
    public static final String messageID = "ID";
    public static final String messageData = "Data";
    public static final String messageTime = "Timestamp";
    public static final String messageSenderID = "SenderID";


    public static final String CONT_TABLE_NAME = "contacts";
    public static final String  fromToken= "fromToken";
    public static final String  fromID= "fromID";
    public static final String  fromName= "fromName";




    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + MSG_TABLE_NAME +
                " ( " + messageID +" INTEGER PRIMARY KEY AUTOINCREMENT," +
                " " + messageData +" TEXT, "+
                messageTime +" TEXT, " +
                messageSenderID + " TEXT)");

        sqLiteDatabase.execSQL("create table " + CONT_TABLE_NAME +
                " ( " + fromID +" TEXT PRIMARY KEY," +
                " " + fromToken +" TEXT, "+
                fromName +" TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public boolean insertData(String messageData, String messageTime, String messageSenderID){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(this.messageData,messageData);
        contentValues.put(this.messageTime,messageTime);
        contentValues.put(this.messageSenderID,messageSenderID);
        db.insert(MSG_TABLE_NAME,null,contentValues);
        return  true;
    }

    public boolean insertContact(String ID, String fromToken, String fromName){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(this.fromID,ID);
        contentValues.put(this.fromToken,fromToken);
        contentValues.put(this.fromName,fromName);
        db.insert(CONT_TABLE_NAME,null,contentValues);
        return  true;
    }

    public Cursor retrieveContacts(SQLiteDatabase db){
        String[] columns = {fromID,fromToken,fromName};

        Cursor cursor = db.query(CONT_TABLE_NAME,columns,null,null,null,null,null);

        return cursor;
    }
}
