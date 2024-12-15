package com.example.project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "ProjectDb.db";
    private static final int DATABASE_VERSION = 5;

    // --- USER --- //
    public static final String TABLE_USER = "user";
    public static final String USER_ID = "user_id";
    public static final String USER_LOGIN = "user_login";
    public static final String USER_PASS = "user_password";
    // --- EQUIPMENT --- //
    public static final String TABLE_EQUIPMENT = "equipment";
    public static final String EQUIPMENT_ID = "equipment_id";
    public static final String EQUIPMENT_TYPE = "equipment_type";
    // (0 - PC | 1 - Mouse | 2 - KeyBoard | 3 - Webcam)
    public static final String EQUIPMENT_PRICE = "equipment_price";
    public static final String EQUIPMENT_NAME = "equipment_name";
    public static final String EQUIPMENT_IMAGE = "equipment_image";
    // --- EQUIPMENT DESCRIPTION --- //
    public static final String TABLE_EQUIPMENT_DESC = "equipment_desc";
    public static final String DESC_EQUIPMENT_ID = "equipment_id";
    public static final String DESC_LANGUAGE_CODE = "language_code";
    public static final String DESC_DESCRIPTION = "description";
    //
    // --- ORDER --- //
    public static final String TABLE_ORDER = "order_table";
    public static final String ORDER_ID = "order_id";
    public static final String ORDER_PRICE = "order_price";
    public static final String ORDER_DATE = "order_date";
    public static final String ORDER_NAME = "order_name";
    public static final String ORDER_CONNECTION = "order_equipment_id";
    // --- ORDER EQUIPMENT CONNECTION --- //
    public static final String TABLE_ORDER_EQUIPMENT = "order_equipment";
    public static final String ORDER_EQUIPMENT_ID = "order_equipment_id";
    public static final String ORDER_EQUIPMENT_ORDER_ID = "order_id";
    public static final String ORDER_EQUIPMENT_EQUIPMENT_ID = "equipment_id";


    // --- CREATE USER TABLE --- //
    private static final String CREATE_USER_TABLE =
            "CREATE TABLE " + TABLE_USER + " (" +
            USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            USER_LOGIN + " TEXT UNIQUE, " +
            USER_PASS + " TEXT" +
            ")";

    // --- CREATE EQUIPMENT TABLE --- //
    private static final String CREATE_EQUIPMENT_TABLE =
            "CREATE TABLE " + TABLE_EQUIPMENT + " (" +
            EQUIPMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            EQUIPMENT_NAME + " TEXT, " +
            EQUIPMENT_IMAGE + " TEXT, " +
            EQUIPMENT_TYPE + " INTEGER, " +
            EQUIPMENT_PRICE + " REAL" +
            ")";

    // --- CREATE EQUIPMENT DESCRIPTION TABLE --- //
    private static final String CREATE_EQUIPMENT_DESC_TABLE =
            "CREATE TABLE " + TABLE_EQUIPMENT_DESC + " (" +
            DESC_EQUIPMENT_ID + " INTEGER, " +
            DESC_LANGUAGE_CODE + " TEXT, " +
            DESC_DESCRIPTION + " TEXT, " +
            "PRIMARY KEY (" + DESC_EQUIPMENT_ID + ", " + DESC_LANGUAGE_CODE + "), " +
            "FOREIGN KEY (" + DESC_EQUIPMENT_ID + ") REFERENCES " + TABLE_EQUIPMENT + "(" + EQUIPMENT_ID + ")" +
            ")";

    // --- CREATE ORDER TABLE --- //
    private static final String CREATE_ORDER_TABLE =
            "CREATE TABLE " + TABLE_ORDER + " (" +
            ORDER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            ORDER_PRICE + " REAL, " +
            ORDER_DATE + " TEXT, " +
            ORDER_NAME + " TEXT, " +
            ORDER_CONNECTION + " INTEGER, " +
            "FOREIGN KEY(" + ORDER_CONNECTION + ") REFERENCES " + TABLE_ORDER_EQUIPMENT + "(" + ORDER_EQUIPMENT_ID + ")" +
            ")";

    // --- CREATE ORDER EQUIPMENT CONNECTION TABLE --- //
    private static final String CREATE_ORDER_EQUIPMENT_TABLE =
            "CREATE TABLE " + TABLE_ORDER_EQUIPMENT + " (" +
            ORDER_EQUIPMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            ORDER_EQUIPMENT_ORDER_ID + " INTEGER, " +
            ORDER_EQUIPMENT_EQUIPMENT_ID + " INTEGER, " +
            "FOREIGN KEY(" + ORDER_EQUIPMENT_ORDER_ID + ") REFERENCES " + TABLE_ORDER + "(" + ORDER_ID + "), " +
            "FOREIGN KEY(" + ORDER_EQUIPMENT_EQUIPMENT_ID + ") REFERENCES " + TABLE_EQUIPMENT + "(" + EQUIPMENT_ID + ")" +
            ")";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Method for creating database //
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_EQUIPMENT_TABLE);
        db.execSQL(CREATE_EQUIPMENT_DESC_TABLE);
        db.execSQL(CREATE_ORDER_TABLE);
        db.execSQL(CREATE_ORDER_EQUIPMENT_TABLE);
    }

    // Method for upgrading database //
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EQUIPMENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EQUIPMENT_DESC);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDER_EQUIPMENT);
        onCreate(db);
    }

    // --- METHODS FOR INSERTING DATA --- //

    // insert data into user table //
    public boolean insertUser(String login, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_LOGIN, login);
        contentValues.put(USER_PASS, password);
        long result = db.insert(TABLE_USER, null, contentValues);
        return result != -1; 
    }

    // insert data into equipment table //
    public boolean insertEquipment(String name, String image, int type, float price) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(EQUIPMENT_NAME, name);
        contentValues.put(EQUIPMENT_IMAGE, image);
        contentValues.put(EQUIPMENT_TYPE, type);
        contentValues.put(EQUIPMENT_PRICE, price);
        long result = db.insert(TABLE_EQUIPMENT, null, contentValues);
        return result != -1; 
    }

    // insert data into equipment description table //
    public boolean insertEquipmentDescription(int equipmentId, String languageCode, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DESC_EQUIPMENT_ID, equipmentId);
        contentValues.put(DESC_LANGUAGE_CODE, languageCode);
        contentValues.put(DESC_DESCRIPTION, description);
        long result = db.insert(TABLE_EQUIPMENT_DESC, null, contentValues);
        return result != -1; 
    }

    // insert data into order table //
    public boolean insertOrder(float price, String date, String name, int connection) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ORDER_PRICE, price);
        contentValues.put(ORDER_DATE, date);
        contentValues.put(ORDER_NAME, name);
        contentValues.put(ORDER_CONNECTION, connection);
        long result = db.insert(TABLE_ORDER, null, contentValues);
        return result != -1; 
    }

    // insert data into order equipment connection table //
    public boolean insertOrderEquipmentConnection(int orderId, int equipmentId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ORDER_EQUIPMENT_ORDER_ID, orderId);
        contentValues.put(ORDER_EQUIPMENT_EQUIPMENT_ID, equipmentId);
        long result = db.insert(TABLE_ORDER_EQUIPMENT, null, contentValues);
        return result != -1; 
    }
        public Cursor getEquipmentById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        
       
        Cursor cursor = db.rawQuery("SELECT * FROM "+ TABLE_EQUIPMENT +" WHERE equipment_id = ?", new String[]{String.valueOf(id)});
        
        return cursor; // Return the cursor to access the data
    }
    public Cursor getAllEquipment() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_EQUIPMENT, null, null, null, null, null, null);
        return cursor; // Return the cursor to access the data
    }
    public Cursor getEquipmentByName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_EQUIPMENT + " WHERE " + EQUIPMENT_NAME + " = ?", new String[]{name});
        return cursor; // Return the cursor to access the data
    }
    public Cursor getEquipmentWithDescription() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT e.equipment_id, e.equipment_name, e.equipment_price, e.equipment_type, ed.language_code, ed.description " +
                "FROM " + TABLE_EQUIPMENT + " e " +
                "LEFT JOIN " + TABLE_EQUIPMENT_DESC + " ed ON e.equipment_id = ed.equipment_id", null);
        return cursor; 
    }

    public Cursor getEquipmentByType(short type) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_EQUIPMENT + " WHERE " + EQUIPMENT_TYPE + " = ?", new String[]{String.valueOf(type)});
        return cursor; 
    }
    public int checkCred(String login, String pass) {
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor cursor = db.rawQuery("SELECT " + USER_ID + ", " + USER_PASS + " FROM " + TABLE_USER + " WHERE " + USER_LOGIN + " = ?", new String[]{login});
    int idIndex = cursor.getColumnIndex(USER_ID);
    int passIndex = cursor.getColumnIndex(USER_PASS);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                // Login found, check password
                String storedPass = cursor.getString(passIndex);
                if (storedPass.equals(pass)) {
                    return cursor.getInt(idIndex);
                } else {
                    return -2; 
                }
            } else {
                return -1; 
            }
             
        }
        cursor.close();
    return -1; 
    }
    public String getUserLoginById(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + USER_LOGIN + " FROM " + TABLE_USER + " WHERE " + USER_ID + " = ?", new String[]{String.valueOf(id)});
        if (cursor != null && cursor.moveToFirst()) {
            int loginIndex = cursor.getColumnIndex(USER_LOGIN);
            if ( loginIndex != -1){
                return cursor.getString(loginIndex);
            }

        }
        return null; //null - no login found
    }
    public boolean addUser(String login, String pass) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_LOGIN, login);
        contentValues.put(USER_PASS, pass);
        long result = db.insert(TABLE_USER, null, contentValues);
        if (result == -1) {
            return false; 
        } else {
            return true; 
        }
    }
    
    public int getUserIdByLogin(String login) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + USER_ID + " FROM " + TABLE_USER + " WHERE " + USER_LOGIN + " = ?", new String[]{login});
        if (cursor != null && cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(USER_ID);
            return cursor.getInt(idIndex);
        }
        return -1; 
    }

    public boolean addOrder(int userId, float orderPrice, String orderDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        
        String userName = getUserLoginById(userId);
        if (userName == null) {
            return false;
        }
        
        contentValues.put(ORDER_NAME, userName);
        contentValues.put(ORDER_PRICE, orderPrice);
        contentValues.put(ORDER_DATE, orderDate);
        
        long result = db.insert(TABLE_ORDER, null, contentValues);
        return result != -1;
    }


    public boolean updateOrderConnection(int orderId, int connectionId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ORDER_CONNECTION, connectionId);
        
        int result = db.update(TABLE_ORDER, 
            contentValues, 
            ORDER_ID + " = ?", 
            new String[]{String.valueOf(orderId)});
        return result > 0;
    }

    public int getLatestOrderEquipmentId() {
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor cursor = db.rawQuery("SELECT " + ORDER_EQUIPMENT_ID + " FROM " + TABLE_ORDER_EQUIPMENT + 
                               " ORDER BY " + ORDER_EQUIPMENT_ID + " DESC LIMIT 1", null);
    
    if (cursor != null && cursor.moveToFirst()) {
        int idIndex = cursor.getColumnIndex(ORDER_EQUIPMENT_ID);
        if (idIndex != -1) {
            return cursor.getInt(idIndex);
        }
    }
    if (cursor != null) {
        cursor.close();
    }
    return -1;
    }

    public int getLatestOrderId() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + ORDER_ID + " FROM " + TABLE_ORDER + 
                                " ORDER BY " + ORDER_ID + " DESC LIMIT 1", null);
        
        if (cursor != null && cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(ORDER_ID);
            if (idIndex != -1) {
                return cursor.getInt(idIndex);
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return -1;
    }
    public Cursor getAllOrders() {
        SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT o." + ORDER_ID + " AS _id, " +
                "o." + ORDER_ID + ", " +
                "o." + ORDER_DATE + ", " +
                "o." + ORDER_PRICE + ", " +
                "u." + USER_LOGIN + ", " +
                "GROUP_CONCAT(e." + EQUIPMENT_NAME + ", ', ') AS equipment_list " +
                "FROM " + TABLE_ORDER + " o " +
                "LEFT JOIN " + TABLE_USER + " u ON o." + ORDER_NAME + " = u." + USER_LOGIN + " " +
                "LEFT JOIN " + TABLE_ORDER_EQUIPMENT + " oe ON o." + ORDER_ID + " = oe." + ORDER_EQUIPMENT_ORDER_ID + " " +
                "LEFT JOIN " + TABLE_EQUIPMENT + " e ON oe." + ORDER_EQUIPMENT_EQUIPMENT_ID + " = e." + EQUIPMENT_ID + " " +
                "GROUP BY o." + ORDER_ID + " " +
                "ORDER BY o." + ORDER_DATE + " DESC";

        return db.rawQuery(query, null);
    }
} 