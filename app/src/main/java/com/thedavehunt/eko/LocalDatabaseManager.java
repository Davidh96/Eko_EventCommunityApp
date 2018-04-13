package com.thedavehunt.eko;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONObject;

import static com.facebook.FacebookSdk.getApplicationContext;

public class LocalDatabaseManager extends SQLiteOpenHelper{

    //db name
    public static final String DATABASE_NAME = "message.db";

    //messages table name
    public static final String MSG_TABLE_NAME = "messages";
    //row names for messages table
    public static final String messageID = "ID";
    public static final String messageData = "Data";
    public static final String messageTime = "Timestamp";
    public static final String messageSenderID = "SenderID";
    public static  final String messageType = "MessageType";

    //contacts table name
    public static final String CONT_TABLE_NAME = "contacts";
    //row names for contacts table
    public static final String  fromToken= "contactToken";
    public static final String  fromID= "contactID";
    public static final String  fromName= "contactName";
    public static final String  fromPublicKey="contactPublicKey";

    //row names for token table
    public static final String  TOKEN_TABLE_NAME = "fcmToken";
    public static final String  tokenID= "TokenID";
    public static final String  tokenValue= "TokenValue";


    public LocalDatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //execute sql for message table
        sqLiteDatabase.execSQL("create table " + MSG_TABLE_NAME +
                " ( " + messageID +" INTEGER PRIMARY KEY AUTOINCREMENT," +
                " " + messageData +" TEXT, "+
                messageTime +" TEXT, " +
                messageSenderID + " TEXT, "+
                messageType +" TEXT)");

        //execute sql for contact table
        sqLiteDatabase.execSQL("create table " + CONT_TABLE_NAME +
                " ( " + fromID +" TEXT PRIMARY KEY," +
                " " + fromToken +" TEXT, "+
                fromName +" TEXT,"+
                fromPublicKey +" TEXT)");

        //execute sql for token table
        sqLiteDatabase.execSQL("create table " + TOKEN_TABLE_NAME +
                " ( " + tokenID +" TEXT PRIMARY KEY," +
                " " + tokenValue +" TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public boolean insertToken(String ID, String token){
        //open up database for writing
        SQLiteDatabase db = this.getWritableDatabase();
        //set content values
        ContentValues contentValues = new ContentValues();
        contentValues.put(this.tokenID,ID);
        contentValues.put(this.tokenValue,token);
        //insert data
        db.insert(TOKEN_TABLE_NAME,null,contentValues);
        //close database reference
        this.close();
        return  true;
    }

    //inserts message data into messages table
    public boolean insertData(String messageData, String messageTime, String messageSenderID, String messageType){
        //get writable database
        SQLiteDatabase db = this.getWritableDatabase();
        //set content values
        ContentValues contentValues = new ContentValues();
        contentValues.put(this.messageData,messageData);
        contentValues.put(this.messageTime,messageTime);
        contentValues.put(this.messageSenderID,messageSenderID);
        contentValues.put(this.messageType,messageType);

        //insert data into messages table
        db.insert(MSG_TABLE_NAME,null,contentValues);

        //close database reference
        this.close();
        return  true;
    }

    //inserts contact details into contact table
    public boolean insertContact(ContactDoc contact){
        //get writable database
        SQLiteDatabase db = this.getWritableDatabase();
        //set content values
        ContentValues contentValues = new ContentValues();
        contentValues.put(this.fromID,contact.getContactID());
        contentValues.put(this.fromToken,contact.getContactToken());
        contentValues.put(this.fromName,contact.getContactName());
        contentValues.put(this.fromPublicKey,contact.getContactPublicKey());
        //insert data into contacts table
        db.insert(CONT_TABLE_NAME,null,contentValues);
        //close database reference
        this.close();
        return  true;
    }

    //adds a new contact to database, called when a user manually adds a contact
    public boolean addContact(final String contactID){
        //insert contact into database
        this.insertContact(new ContactDoc(contactID));

        NetworkManager.getInstance(getApplicationContext());
        final String id = contactID;

        //get user details from Firebase
        NetworkManager.getInstance().readUserDetails(contactID,new NetworkManager.SomeCustomListener<JSONObject>()
        {
            @Override
            public void getResult(JSONObject result)
            {
                if (!result.toString().isEmpty())
                {
                    String name="";
                    String token="";
                    String key="";
                    try {
                         name = result.getString("DisplayName");
                         token = result.getString("Token");
                         key = result.getString("PublicKey");
                         Log.d("pubicKey",key);
                    }catch (Exception e){

                    }
                    //create new contact doc with contact details
                    ContactDoc contact = new ContactDoc(token,id,name,key);
                    //update user details
                    updateContact(contact);

                }
            }
        });


        return true;
    }

    //update contact in contacts table
    public boolean updateContact(ContactDoc contact){
        //get writable database
        SQLiteDatabase db = this.getWritableDatabase();
        //set content values
        ContentValues contentValues = new ContentValues();
        contentValues.put(this.fromID,contact.getContactID());
        contentValues.put(this.fromToken,contact.getContactToken());
        contentValues.put(this.fromName,contact.getContactName());
        contentValues.put(this.fromPublicKey,contact.getContactPublicKey());

        //update contact at row where contact id is the one of the contact being updated
        db.update(CONT_TABLE_NAME,contentValues,fromID + "= " + "'" + contact.getContactID() + "'",null);
        //close database reference
        this.close();
        return true;
    }

    //get list of contacts
    public Cursor retrieveContacts(){
        //get readable database
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {fromID,fromToken,fromName,fromPublicKey};
        //cursor containing all contacts and their details
        Cursor cursor = db.query(CONT_TABLE_NAME,columns,null,null,null,null,null);

        return cursor;

    }

    //get a singualr contact
    public Cursor retrieveContact(String contactID){
        //get readable database
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {fromID,fromToken,fromName,fromPublicKey};
        //cursor containing a single contact
        Cursor cursor = db.query(CONT_TABLE_NAME,columns,fromID+ "= " + "'" + contactID + "'",null,null,null,null);

        return cursor;
    }

    //get chat history
    public Cursor retrieveChat(String contactID){
        //get readable database
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {messageData,messageTime,messageSenderID,messageType};

        //cursor containing all messages
        Cursor cursor = db.query(MSG_TABLE_NAME,columns, messageSenderID +"= " + "'" + contactID + "'",null,null,null,null);

        return cursor;
    }

    //get user token
    public String retrieveToken(String myID){

        SQLiteDatabase db = this.getWritableDatabase();
        String[] columns = {tokenID,tokenValue};
        String myToken="";

        Cursor cursor = db.query(TOKEN_TABLE_NAME,columns,tokenID+"= " + "'" + myID + "'",null,null,null,null);

        while(cursor.moveToNext()){
            myToken = cursor.getString(cursor.getColumnIndex("TokenValue"));
        }

        this.close();
        return myToken;
    }

    //get a contacts public key
    public String retrieveContactPublicKey(String contactID){
        //get readable database
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {fromID,fromToken,fromName,fromPublicKey};

        //contact row
        Cursor cursor = db.query(CONT_TABLE_NAME,columns,fromID + "= " + "'" + contactID + "'",null,null,null,null);

        //get the key
        String key= null;
        while(cursor.moveToNext()){
            key = cursor.getString(cursor.getColumnIndex(fromPublicKey));
        }

        //close database reference
        this.close();
        return key;
    }

    //get contact token
    public String retrieveContactToken(String contactID){
        //get readabale database
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {fromID,fromToken,fromName,fromPublicKey};
        String contactToken="";

        //row of contact
        Cursor cursor = db.query(CONT_TABLE_NAME,columns,fromID + "= " + "'" + contactID + "'",null,null,null,null);

        //get contact token
        while(cursor.moveToNext()){
            contactToken = cursor.getString(cursor.getColumnIndex(fromToken));
        }

        //close database reference
        this.close();
        return contactToken;
    }
}
