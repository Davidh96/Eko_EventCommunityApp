package com.thedavehunt.eko;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "message.db";

    public static final String MSG_TABLE_NAME = "messages";
    public static final String messageID = "ID";
    public static final String messageData = "Data";
    public static final String messageTime = "Timestamp";
    public static final String messageSenderID = "SenderID";
    public static  final String messageType = "MessageType";


    public static final String CONT_TABLE_NAME = "contacts";
    public static final String  fromToken= "fromToken";
    public static final String  fromID= "fromID";
    public static final String  fromName= "fromName";

    public static final String  TOKEN_TABLE_NAME = "fcmToken";
    public static final String  tokenID= "TokenID";
    public static final String  tokenValue= "TokenValue";


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
                messageSenderID + " TEXT, "+
                messageType +" TEXT)");

        sqLiteDatabase.execSQL("create table " + CONT_TABLE_NAME +
                " ( " + fromID +" TEXT PRIMARY KEY," +
                " " + fromToken +" TEXT, "+
                fromName +" TEXT)");

        sqLiteDatabase.execSQL("create table " + TOKEN_TABLE_NAME +
                " ( " + tokenID +" TEXT PRIMARY KEY," +
                " " + tokenValue +" TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public boolean insertToken(String ID, String token){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(this.tokenID,ID);
        contentValues.put(this.tokenValue,token);
        db.insert(TOKEN_TABLE_NAME,null,contentValues);
        Log.d("Inserted data","data was inserted");
        return  true;
    }

    public boolean insertData(String messageData, String messageTime, String messageSenderID, String messageType){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(this.messageData,messageData);
        contentValues.put(this.messageTime,messageTime);
        contentValues.put(this.messageSenderID,messageSenderID);
        contentValues.put(this.messageType,messageType);
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

    public boolean addContact(String contactID){
        databaseManager dbm = new databaseManager();
        dbm.getContactToken(contactID);


        return true;
    }

    public Cursor retrieveContacts(SQLiteDatabase db){
        String[] columns = {fromID,fromToken,fromName};

        Cursor cursor = db.query(CONT_TABLE_NAME,columns,null,null,null,null,null);

        return cursor;
    }

    public Cursor retrieveContact(SQLiteDatabase db,String contactID){
        String[] columns = {fromID,fromToken,fromName};

        Cursor cursor = db.query(CONT_TABLE_NAME,columns,"fromID = " + "'" + contactID + "'",null,null,null,null);

        return cursor;
    }

    public Cursor retrieveChat(SQLiteDatabase db, String contactID){
        String[] columns = {messageData,messageTime,messageSenderID,messageType};

        Cursor cursor = db.query(MSG_TABLE_NAME,columns,"SenderID = " + "'" + contactID + "'",null,null,null,null);

        return cursor;
    }

    public String retrieveToken(SQLiteDatabase db, String myID){
        String[] columns = {tokenID,tokenValue};
        String myToken="";

        Cursor cursor = db.query(TOKEN_TABLE_NAME,columns,"TokenID = " + "'" + myID + "'",null,null,null,null);

        while(cursor.moveToNext()){
            myToken = cursor.getString(cursor.getColumnIndex("TokenValue"));
        }

        return myToken;
    }
}
