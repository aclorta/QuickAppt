/**
 * Created by kenmorte on 11/13/16.
 */
package com.example.team6.quickappt;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

/*
SQL Tables:

    CREATE TABLE User (
      _id INTEGER,
      username VARCHAR(20),

      PRIMARY KEY (_id)
    );

    CREATE TABLE Login (
      username VARCHAR(20),
      password VARCHAR(20),

      PRIMARY KEY (username),
      FOREIGN KEY (username) REFERENCES User(username)
        ON DELETE CASCADE
        ON UPDATE CASCADE
    );

    CREATE TABLE Patient (
      _id INTEGER NOT NULL,

      name   VARCHAR(50),
      gender CHAR(1),
      age INTEGER,
      phone_number CHAR(10),
      email VARCHAR(20),
      location VARCHAR(50),

      PRIMARY KEY (_id),
      FOREIGN KEY (_id) REFERENCES User(_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
    );

    CREATE TABLE Physician (
      _id INTEGER NOT NULL,

      name   VARCHAR(50),
      gender CHAR(1),
      phone_number CHAR(10),
      email VARCHAR(20),
      location VARCHAR(50),

      PRIMARY KEY (_id),
      FOREIGN KEY (_id) REFERENCES User(_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
    );

    CREATE TABLE Specialization (
      name VARCHAR(50),
      tags VARCHAR(100),

      PRIMARY KEY (name)
    );

    CREATE TABLE PhysicianSpecializations (
      physician_id INTEGER NOT NULL,
      specialization VARCHAR(50),

      PRIMARY KEY (physician_id, specialization),
      FOREIGN KEY (physician_id) REFERENCES Physician(_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
      FOREIGN KEY (specialization) REFERENCES Specialization(name)
        ON DELETE SET NULL
        ON UPDATE CASCADE
    );

    CREATE TABLE Appointment (
      patient_id INTEGER NOT NULL,
      physician_id INTEGER NOT NULL,

      time DATETIME,

      PRIMARY KEY (physician_id, patient_id),
      FOREIGN KEY (physician_id) REFERENCES Physician(physician_id)
        ON DELETE SET NULL
        ON UPDATE CASCADE,
      FOREIGN KEY (patient_id) REFERENCES Patient(patient_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
    );
 */

public class QADBHelper
{
    public static final String KEY_ROWID = "_id",
            KEY_ISBN = "isbn",
            KEY_TITLE = "title",
            KEY_PUBLISHER = "publisher",
            TAG = "DBAdapter";

    // Create SQL command for creating the 'User' table
    private static final String USER_TABLE_NAME = "User",
        USER_TABLE_KEY_ROWID = "_id",
        USER_TABLE_ATTR_USERNAME = "username",
        USER_TABLE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + USER_TABLE_NAME +  " (" +
                "  " + USER_TABLE_KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  " + USER_TABLE_ATTR_USERNAME +  " VARCHAR(20)" +
                ");";

    // Create SQL command for creating the 'Login' table
    private static final String LOGIN_TABLE_NAME = "Login",
        LOGIN_TABLE_KEY_USERNAME = "username",
        LOGIN_TABLE_ATTR_PASSWORD = "password",
        LOGIN_TABLE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + LOGIN_TABLE_NAME + " (" +
                "  " + LOGIN_TABLE_KEY_USERNAME + " VARCHAR(20)," +
                "  " + LOGIN_TABLE_ATTR_PASSWORD + " VARCHAR(64)," +
                "  PRIMARY KEY (" + LOGIN_TABLE_KEY_USERNAME + ")," +
                "  FOREIGN KEY (" + LOGIN_TABLE_KEY_USERNAME + ") REFERENCES " + USER_TABLE_NAME + "(" + USER_TABLE_ATTR_USERNAME + ")" +
                    " ON DELETE CASCADE" +
                    " ON UPDATE CASCADE" +
                ");";

    // Create SQL command for creating the 'Patient' table
    private static final String PATIENT_TABLE_NAME = "Patient",
        PATIENT_TABLE_KEY_ID = "_id",
        PATIENT_TABLE_ATTR_NAME = "name",
        PATIENT_TABLE_ATTR_GENDER = "gender",
        PATIENT_TABLE_ATTR_AGE = "age",
        PATIENT_TABLE_ATTR_PHONE = "phone",
        PATIENT_TABLE_ATTR_EMAIL = "email",
        PATIENT_TABLE_ATTR_LOCATION = "location",
        PATIENT_TABLE_CREATE =
                "CREATE TABLE IF NOT EXISTS " + PATIENT_TABLE_NAME + " (" +
                        "  " + PATIENT_TABLE_KEY_ID + " INTEGER NOT NULL," +
                        "  " + PATIENT_TABLE_ATTR_NAME + " VARCHAR(50)," +
                        "  " + PATIENT_TABLE_ATTR_GENDER + " CHAR(1)," +
                        "  " + PATIENT_TABLE_ATTR_AGE + " INTEGER," +
                        "  " + PATIENT_TABLE_ATTR_PHONE + " CHAR(10)," +
                        "  " + PATIENT_TABLE_ATTR_EMAIL + " VARCHAR(20)," +
                        "  " + PATIENT_TABLE_ATTR_LOCATION + " VARCHAR(50)," +
                        "  PRIMARY KEY (" + PATIENT_TABLE_KEY_ID + ")," +
                        "  FOREIGN KEY (" + PATIENT_TABLE_KEY_ID + ") REFERENCES " + USER_TABLE_NAME + "(" + USER_TABLE_ATTR_USERNAME + ")" +
                            " ON DELETE CASCADE " +
                            " ON UPDATE CASCADE " +
                        ");";

    // Create SQL command for creating the 'Physician' table
    private static final String PHYSICIAN_TABLE_NAME = "Physician",
        PHYSICIAN_TABLE_KEY_ID = "_id",
        PHYSICIAN_TABLE_ATTR_NAME = "name",
        PHYSICIAN_TABLE_ATTR_GENDER = "gender",
        PHYSICIAN_TABLE_ATTR_PHONE = "phone",
        PHYSICIAN_TABLE_ATTR_EMAIL = "email",
        PHYSICIAN_TABLE_ATTR_LOCATION = "location",
        PHYSICIAN_TABLE_CREATE =
                "CREATE TABLE IF NOT EXISTS " + PHYSICIAN_TABLE_NAME + " (" +
                        "  " + PHYSICIAN_TABLE_KEY_ID + " INTEGER NOT NULL," +
                        "  " + PHYSICIAN_TABLE_ATTR_NAME + " VARCHAR(50)," +
                        "  " + PHYSICIAN_TABLE_ATTR_GENDER + " CHAR(1)," +
                        "  " + PHYSICIAN_TABLE_ATTR_PHONE + " CHAR(10)," +
                        "  " + PHYSICIAN_TABLE_ATTR_EMAIL + " VARCHAR(20)," +
                        "  " + PHYSICIAN_TABLE_ATTR_LOCATION + " VARCHAR(50)," +
                        "  PRIMARY KEY (" + PHYSICIAN_TABLE_KEY_ID + ")," +
                        "  FOREIGN KEY (" + PHYSICIAN_TABLE_KEY_ID + ") REFERENCES " + USER_TABLE_NAME + "(" + USER_TABLE_ATTR_USERNAME + ")" +
                            " ON DELETE CASCADE " +
                            " ON UPDATE CASCADE " +
                        ");";

    // Create SQL command for creating the 'Specialization' table
    private static final String SPECIALIZATION_TABLE_NAME = "Specialization",
        SPECIALIZATION_TABLE_KEY_NAME = "name",
        SPECIALIZATION_TABLE_ATTR_TAGS = "tags",
        SPECIALIZATION_TABLE_CREATE =
                "CREATE TABLE IF NOT EXISTS " + SPECIALIZATION_TABLE_NAME + " (" +
                        "  " + SPECIALIZATION_TABLE_KEY_NAME + " VARCHAR(50)," +
                        "  " + SPECIALIZATION_TABLE_ATTR_TAGS + " VARCHAR(100)," +
                        "  PRIMARY KEY (" + SPECIALIZATION_TABLE_KEY_NAME + ")" +
                        ");";

    // Create SQL command for creating the 'PhysicianSpecialization' table
    private static final String PHYSICIAN_SPECIALIZATIONS_TABLE_NAME = "PhysicianSpecializations",
            PHYSICIAN_SPECIALIZATIONS_TABLE_KEY_ID = "physician_id",
            PHYSICIAN_SPECIALIZATIONS_TABLE_KEY_SPECIALIZATION = "specialization",
            PHYSICIAN_SPECIALIZATIONS_TABLE_CREATE =
                    "CREATE TABLE IF NOT EXISTS " + PHYSICIAN_SPECIALIZATIONS_TABLE_NAME + " (" +
                            "  " + PHYSICIAN_SPECIALIZATIONS_TABLE_KEY_ID + " INTEGER NOT NULL," +
                            "  " + PHYSICIAN_SPECIALIZATIONS_TABLE_KEY_SPECIALIZATION + " VARCHAR(50) NOT NULL," +
                            "  PRIMARY KEY (" + PHYSICIAN_SPECIALIZATIONS_TABLE_KEY_ID + ", " + PHYSICIAN_SPECIALIZATIONS_TABLE_KEY_SPECIALIZATION +  ")," +
                            "  FOREIGN KEY (" + PHYSICIAN_SPECIALIZATIONS_TABLE_KEY_ID + ") REFERENCES " + USER_TABLE_NAME + "(" + USER_TABLE_ATTR_USERNAME + ")" +
                                " ON DELETE CASCADE " +
                                " ON UPDATE CASCADE, " +
                            "  FOREIGN KEY (" + PHYSICIAN_SPECIALIZATIONS_TABLE_KEY_SPECIALIZATION + ") REFERENCES " + SPECIALIZATION_TABLE_NAME + "(" + SPECIALIZATION_TABLE_KEY_NAME + ")" +
                                " ON DELETE SET NULL" +
                                " ON UPDATE CASCADE" +
                            ");";

    // Create SQL command for creating the 'Appointment' table
    private static final String APPOINTMENT_TABLE_NAME = "Appointment",
        APPOINTMENT_TABLE_KEY_PATIENT_ID = "patient_id",
        APPOINTMENT_TABLE_KEY_PHYSICIAN_ID = "physician_id",
        APPOINTMENT_TABLE_ATTR_TIME = "time",
        APPOINTMENT_TABLE_CREATE =
                    "CREATE TABLE IF NOT EXISTS " + APPOINTMENT_TABLE_NAME + " (" +
                            "  " + APPOINTMENT_TABLE_KEY_PATIENT_ID + " INTEGER NOT NULL," +
                            "  " + APPOINTMENT_TABLE_KEY_PHYSICIAN_ID + " INTEGER NOT NULL," +
                            "  " + APPOINTMENT_TABLE_ATTR_TIME + " DATETIME NOT NULL," +
                            "  PRIMARY KEY (" + APPOINTMENT_TABLE_KEY_PATIENT_ID + ", " + APPOINTMENT_TABLE_KEY_PHYSICIAN_ID +  ")," +
                            "  FOREIGN KEY (" + APPOINTMENT_TABLE_KEY_PATIENT_ID + ") REFERENCES " + PATIENT_TABLE_NAME + "(" + PATIENT_TABLE_KEY_ID + ")" +
                                "  ON DELETE CASCADE " +
                                "  ON UPDATE CASCADE, " +
                            "  FOREIGN KEY (" + APPOINTMENT_TABLE_KEY_PHYSICIAN_ID + ") REFERENCES " + PHYSICIAN_TABLE_NAME + "(" + PHYSICIAN_TABLE_KEY_ID + ")" +
                                "  ON DELETE SET NULL " +
                                "  ON UPDATE CASCADE " +
                            ");";

    private static final String DATABASE_NAME = "QuickAppt";
    private static final String DATABASE_TABLE = "titles";
    private static final int DATABASE_VERSION = 1;

    /* IMPORTANT: update this array when adding new tables onto the database */
    private static final String[] DATABASE_TABLES = {
            USER_TABLE_NAME,
            LOGIN_TABLE_NAME,
            PATIENT_TABLE_NAME,
            PHYSICIAN_TABLE_NAME,
            SPECIALIZATION_TABLE_NAME,
            PHYSICIAN_SPECIALIZATIONS_TABLE_NAME,
            APPOINTMENT_TABLE_NAME
    };

    private static final String DATABASE_CREATE =
            "create table titles (_id integer primary key autoincrement, "
                    + "isbn text not null, title text not null, "
                    + "publisher text not null);\n";

    private final Context context;

    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    private PasswordCrypter crypter;

    public QADBHelper(Context ctx)
    {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
        crypter = new PasswordCrypter();

        /* TODO: delete this line if we plan on NOT using a fresh database on startup. */
//        context.deleteDatabase(DATABASE_NAME);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL(USER_TABLE_CREATE);
            db.execSQL(LOGIN_TABLE_CREATE);
            db.execSQL(PATIENT_TABLE_CREATE);
            db.execSQL(PHYSICIAN_TABLE_CREATE);
            db.execSQL(SPECIALIZATION_TABLE_CREATE);
            db.execSQL(PHYSICIAN_SPECIALIZATIONS_TABLE_CREATE);
            db.execSQL(APPOINTMENT_TABLE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion,
                              int newVersion)
        {
            Log.w(TAG, "Upgrading database from version " + oldVersion
                    + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS titles");
            onCreate(db);
        }
    }

    private static class PasswordCrypter
    {
        // TODO: Implement a better encryption/decryption (maybe for future usage)
        public String encode(String s) {
            String result = "";
            for (char c : s.toCharArray()) {
                result += (char) (c + 2);
            }
            return result;
        }

        public String decode(String s) {
            String result = "";
            for (char c : s.toCharArray()) {
                result += (char) (c - 2);
            }
            return result;
        }
    }

    //---opens the database---
    public QADBHelper open() throws SQLException
    {
        db = DBHelper.getWritableDatabase();

        // IMPORTANT: If you don't want to start off with a fresh database each time you restart app, uncomment.
        dropTables();

        db.execSQL(USER_TABLE_CREATE);
        db.execSQL(LOGIN_TABLE_CREATE);
        db.execSQL(PATIENT_TABLE_CREATE);
        db.execSQL(PHYSICIAN_TABLE_CREATE);
        db.execSQL(SPECIALIZATION_TABLE_CREATE);
        db.execSQL(PHYSICIAN_SPECIALIZATIONS_TABLE_CREATE);
        db.execSQL(APPOINTMENT_TABLE_CREATE);

        addDummyUsers();
        return this;
    }

    //---closes the database---
    public void close() {
        DBHelper.close();
    }

    //---drops the tables in the database---
    private void dropTables()
    {
        for (String table : DATABASE_TABLES) {
            db.execSQL("DROP TABLE IF EXISTS " + table);
        }
    }

    /*---------- User/Login methods ----------*/

    //---adds a user (w/login info) into the database---
    public long addUser(String username, String password)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(LOGIN_TABLE_KEY_USERNAME, username);
        initialValues.put(LOGIN_TABLE_ATTR_PASSWORD, crypter.encode(password));

        // Add username and password into database
        db.insert(LOGIN_TABLE_NAME, null, initialValues);


        initialValues.clear();
        initialValues.put(USER_TABLE_ATTR_USERNAME, username);

        // Add user into main user database
        return db.insert(USER_TABLE_NAME, null, initialValues);
    }

    //---returns true if user exists on database, false otherwise---
    public boolean userExists(String username)
    {
        return getLogin(username).moveToFirst();
    }

    public boolean loginValid(String username, String password)
    {
        if (!userExists(username))
            return false;

        Cursor c = getLogin(username);
        return crypter.decode(c.getString(1)).equals(password);
    }

    //---returns login information for specific username---
    private Cursor getLogin(String username)
    {
        /*
        SELECT * FROM Login
        WHERE Login.username = username
         */

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                LOGIN_TABLE_KEY_USERNAME,
                LOGIN_TABLE_ATTR_PASSWORD
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = LOGIN_TABLE_KEY_USERNAME + " = ?";
        String[] selectionArgs = { username };

        Cursor mCursor =
                db.query(
                        LOGIN_TABLE_NAME,                         // The table to query
                        projection,                               // The columns to return
                        selection,                                // The columns for the WHERE clause
                        selectionArgs,                            // The values for the WHERE clause
                        null,                                     // don't group the rows
                        null,                                     // don't filter by row groups
                        null                                      // The sort order
                );
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public long getUserID(String username)
    {
        /*
        SELECT _id FROM User
        WHERE User.username = username
         */
        String[] projection = {
                USER_TABLE_KEY_ROWID
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = USER_TABLE_ATTR_USERNAME + " = ?";
        String[] selectionArgs = { username };

        Cursor mCursor =
                db.query(
                        USER_TABLE_NAME,                         // The table to query
                        projection,                               // The columns to return
                        selection,                                // The columns for the WHERE clause
                        selectionArgs,                            // The values for the WHERE clause
                        null,                                     // don't group the rows
                        null,                                     // don't filter by row groups
                        null                                      // The sort order
                );
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor.getLong(0);
    }

    //---adds test users into the database (should only be used for testing)---
    private void addDummyUsers()
    {
        addUser("hello", "world");
        addUser("foobar", "barfoo");
    }

    /* ---------- Christian: The next functions below don't matter for our application,
                  but a good guidance for handling database code ---------- */

    //---insert a title into the database---
    public long insertTitle(String isbn, String title, String publisher)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_ISBN, isbn);
        initialValues.put(KEY_TITLE, title);
        initialValues.put(KEY_PUBLISHER, publisher);
        return db.insert(DATABASE_TABLE, null, initialValues);
    }

    //---deletes a particular title---
    public boolean deleteTitle(long rowId)
    {
        return db.delete(DATABASE_TABLE, KEY_ROWID +
                "=" + rowId, null) > 0;
    }

    //---retrieves all the titles---
    public Cursor getAllTitles()
    {
        return db.query(DATABASE_TABLE, new String[] {
                        KEY_ROWID,
                        KEY_ISBN,
                        KEY_TITLE,
                        KEY_PUBLISHER},
                null,
                null,
                null,
                null,
                null);
    }

    //---retrieves a particular title---
    public Cursor getTitle(long rowId) throws SQLException
    {
        Cursor mCursor =
                db.query(true, DATABASE_TABLE, new String[] {
                                KEY_ROWID,
                                KEY_ISBN,
                                KEY_TITLE,
                                KEY_PUBLISHER
                        },
                        KEY_ROWID + "=" + rowId,
                        null,
                        null,
                        null,
                        null,
                        null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //---updates a title---
    public boolean updateTitle(long rowId, String isbn,
                               String title, String publisher)
    {
        ContentValues args = new ContentValues();
        args.put(KEY_ISBN, isbn);
        args.put(KEY_TITLE, title);
        args.put(KEY_PUBLISHER, publisher);
        return db.update(DATABASE_TABLE, args,
                KEY_ROWID + "=" + rowId, null) > 0;
    }
}


