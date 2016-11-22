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
import java.util.ArrayList;

public class QADBHelper
{
    public static final String KEY_ROWID = "_id",
            KEY_ISBN = "isbn",
            KEY_TITLE = "title",
            KEY_PUBLISHER = "publisher",
            TAG = "DBAdapter";


    private static final String DATABASE_CREATE =
            "create table titles (_id integer primary key autoincrement, "
                    + "isbn text not null, title text not null, "
                    + "publisher text not null);\n";

    private final Context context;

    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;
    private QADatabaseStrings dbStrings;

    private PasswordCrypter crypter;

    public QADBHelper(Context ctx)
    {
        this.context = ctx;
        dbStrings = new QADatabaseStrings();
        DBHelper = new DatabaseHelper(context, dbStrings);
        crypter = new PasswordCrypter();


        /* TODO: delete this line if we plan on NOT using a fresh database on startup. */
//        context.deleteDatabase(DATABASE_NAME);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        QADatabaseStrings strings;

        DatabaseHelper(Context context, QADatabaseStrings dbStrings)
        {
            super(context, dbStrings.DATABASE_NAME, null, dbStrings.DATABASE_VERSION);
            strings = dbStrings;
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL(strings.USER_TABLE_CREATE);
            db.execSQL(strings.LOGIN_TABLE_CREATE);
            db.execSQL(strings.PATIENT_TABLE_CREATE);
            db.execSQL(strings.PHYSICIAN_TABLE_CREATE);
            db.execSQL(strings.SPECIALIZATION_TABLE_CREATE);
            db.execSQL(strings.PHYSICIAN_SPECIALIZATIONS_TABLE_CREATE);
            db.execSQL(strings.APPOINTMENT_TABLE_CREATE);
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

        db.execSQL(dbStrings.USER_TABLE_CREATE);
        db.execSQL(dbStrings.LOGIN_TABLE_CREATE);
        db.execSQL(dbStrings.PATIENT_TABLE_CREATE);
        db.execSQL(dbStrings.PHYSICIAN_TABLE_CREATE);
        db.execSQL(dbStrings.SPECIALIZATION_TABLE_CREATE);
        db.execSQL(dbStrings.PHYSICIAN_SPECIALIZATIONS_TABLE_CREATE);
        db.execSQL(dbStrings.APPOINTMENT_TABLE_CREATE);
        db.execSQL(dbStrings.PATIENT_MEDICAL_HISTORY_TABLE_CREATE);

        addTestData();
        return this;
    }

    //---closes the database---
    public void close() {
        DBHelper.close();
    }

    //---drops the tables in the database---
    private void dropTables()
    {
        for (String table : dbStrings.DATABASE_TABLES) {
            db.execSQL("DROP TABLE IF EXISTS " + table);
        }
    }

    /*---------- User/Login methods ----------*/
    /*
    Returns true if a user exists (using the username) on the database, false otherwise.

    This should be used in the login screen to display a proper message if a username is inputted
    that does not exist within the application.
     */
    public boolean userExists(String username)
    {
        return getLogin(username).moveToFirst();
    }

    public boolean userExists(long id)
    {
        String[] projection = { dbStrings.USER_TABLE_KEY_ROWID };

        String selection = dbStrings.USER_TABLE_KEY_ROWID + " = ?";
        String[] selectionArgs = { Long.toString(id) };

        Cursor mCursor =
                db.query(
                        dbStrings.USER_TABLE_NAME,                // The table to query
                        projection,                               // The columns to return
                        selection,                                // The columns for the WHERE clause
                        selectionArgs,                            // The values for the WHERE clause
                        null,                                     // don't group the rows
                        null,                                     // don't filter by row groups
                        null                                      // The sort order
                );
        return mCursor != null;
    }

    /*
    Returns true if a login credential is valid (from user input), false otherwise.

    This should be used in the login screen to validate the input for a the login credentials.
    A proper error message should be displayed if this is returned false, otherwise go to next activity.
     */
    public boolean loginValid(String username, String password)
    {
        if (!userExists(username))
            return false;

        Cursor c = getLogin(username);
        return crypter.decode(c.getString(1)).equals(password);
    }

    /*
    Returns the user ID for a specified user (by username).

    This should be used to retrieve all information for a specific user from other tables within the database.
    If the user does not exist in the database, it returns 0 (null user).
     */
    public long getUserID(String username)
    {
        if (!userExists(username))
            return 0;

        String[] projection = {
                dbStrings.USER_TABLE_KEY_ROWID
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = dbStrings.USER_TABLE_ATTR_USERNAME + " = ?";
        String[] selectionArgs = { username };

        Cursor mCursor =
                db.query(
                        dbStrings.USER_TABLE_NAME,                // The table to query
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



    /*---------- Patient Table methods ----------*/
    /*
    Adds patient information into database.
    Returns the User ID for a patient.

    This should be called after a patient has completed the sign up process.
     */
    public long addPatient(long id, String name, String gender, String birthDate, String maritalStatus,
                           String phone, String email, String location,
                           String occupation, String regularExerciser,
                           String allergies, String medications, String surgeries,
                           String physicianName, String dentistName, String eyeDoctorName)
    {
        // Set patient attributes to content values map
        ContentValues initialValues = new ContentValues();
        initialValues.put(dbStrings.PATIENT_TABLE_KEY_ID, id);
        initialValues.put(dbStrings.PATIENT_TABLE_ATTR_NAME, name);
        initialValues.put(dbStrings.PATIENT_TABLE_ATTR_GENDER, gender);
        initialValues.put(dbStrings.PATIENT_TABLE_ATTR_BIRTH, birthDate);
        initialValues.put(dbStrings.PATIENT_TABLE_ATTR_MARITAL_STATUS, maritalStatus);
        initialValues.put(dbStrings.PATIENT_TABLE_ATTR_OCCUPATION, occupation);
        initialValues.put(dbStrings.PATIENT_TABLE_ATTR_PHONE, phone);
        initialValues.put(dbStrings.PATIENT_TABLE_ATTR_EMAIL, email);
        initialValues.put(dbStrings.PATIENT_TABLE_ATTR_LOCATION, location);
        initialValues.put(dbStrings.PATIENT_TABLE_ATTR_REGULAR_EXERCISER, regularExerciser);
        initialValues.put(dbStrings.PATIENT_TABLE_ATTR_PHYSICIAN_NAME, physicianName);
        initialValues.put(dbStrings.PATIENT_TABLE_ATTR_DENTIST_NAME, dentistName);
        initialValues.put(dbStrings.PATIENT_TABLE_ATTR_EYE_DOCTOR_NAME, eyeDoctorName);

        // Insert into database, return the User ID of patient
        long result = db.insert(dbStrings.PATIENT_TABLE_NAME, null, initialValues);

        initialValues.clear();
        initialValues.put(dbStrings.PATIENT_MEDICAL_HISTORY_TABLE_ATTR_ALLERGIES, allergies);
        initialValues.put(dbStrings.PATIENT_MEDICAL_HISTORY_TABLE_ATTR_MEDICATIONS, medications);
        initialValues.put(dbStrings.PATIENT_MEDICAL_HISTORY_TABLE_ATTR_SURGERIES, surgeries);

        db.insert(dbStrings.PATIENT_MEDICAL_HISTORY_TABLE_NAME, null, initialValues);
        return result;
    }

    public long addPatient(String username, String password,
                           String name, String gender, String birthDate, String maritalStatus,
                           String phone, String email, String location,
                           String occupation, String regularExerciser,
                           String allergies, String medications, String surgeries,
                           String physicianName, String dentistName, String eyeDoctorName)
    {
        return addPatient(addUser(username, password),
                            name, gender, birthDate, maritalStatus,
                            phone, email, location,
                            occupation, regularExerciser,
                            allergies, medications, surgeries,
                            physicianName, dentistName, eyeDoctorName);
    }

    /*
    Returns a map of information regarding a patient.
    The index format of the keys is as follows:
    [ 0: Patient (User) ID,
        1: Name,
        2: Gender,
        3: Birth,
        4: MaritalStatus
        5: Occupation
        6: Phone
        7: Email
        8: Location
        9: TobaccoSmoker
        10: RegularExerciser
        11: PhysicianName, 12: DentistName, 13: EyeDoctorName
        14: Allergies, 15: Medications, 16: Surgeries
    ]

    This should be called whenever you need to display specific patient information on a screen.
     */
    public HashMap getPatientInfo(long id)
    {
        HashMap<String,String> result;

        // Patient table query parameters
        String[] projection = {"*"};
        String selection = dbStrings.PATIENT_TABLE_KEY_ID + " = ?";
        String[] selectionArgs = { Long.toString(id) };

        String[] medHistoryProjection = {dbStrings.PATIENT_MEDICAL_HISTORY_TABLE_ATTR_ALLERGIES,
                                         dbStrings.PATIENT_MEDICAL_HISTORY_TABLE_ATTR_MEDICATIONS,
                                         dbStrings.PATIENT_MEDICAL_HISTORY_TABLE_ATTR_SURGERIES};
        String medHistorySelection = dbStrings.PATIENT_MEDICAL_HISTORY_TABLE_KEY_ID + " = ?";
        String[] medHistoryArgs = { Long.toString(id) };

        Cursor patientTableCursor =
                db.query(
                        dbStrings.PATIENT_TABLE_NAME,             // The table to query
                        projection,                               // The columns to return
                        selection,                                // The columns for the WHERE clause
                        selectionArgs,                            // The values for the WHERE clause
                        null,                                     // don't group the rows
                        null,                                     // don't filter by row groups
                        null                                      // The sort order
                ),

                medicalHistoryCursor =
                        db.query(
                                dbStrings.PATIENT_MEDICAL_HISTORY_TABLE_NAME,
                                medHistoryProjection,
                                medHistorySelection,
                                medHistoryArgs,
                                null,
                                null,
                                null
                        );


        if (patientTableCursor != null && patientTableCursor.moveToFirst()) {
            result = new HashMap<String,String>();
            result.put("ID", patientTableCursor.getString(0));
            result.put("Name", patientTableCursor.getString(1));
            result.put("Gender", patientTableCursor.getString(2));
            result.put("Birth", patientTableCursor.getString(3));
            result.put("MaritalStatus", patientTableCursor.getString(4));
            result.put("Occupation", patientTableCursor.getString(5));
            result.put("Phone", patientTableCursor.getString(6));
            result.put("Email", patientTableCursor.getString(7));
            result.put("Location", patientTableCursor.getString(8));
            result.put("TobaccoSmoker", patientTableCursor.getString(9));
            result.put("RegularExerciser", patientTableCursor.getString(10));
            result.put("PhysicianName", patientTableCursor.getString(11));
            result.put("DentistName", patientTableCursor.getString(12));
            result.put("EyeDoctorName", patientTableCursor.getString(13));
            result.put("Allergies", "");
            result.put("Medications", "");
            result.put("Surgeries", "");

            if (medicalHistoryCursor != null && medicalHistoryCursor.moveToFirst()) {
                result.put("Allergies", medicalHistoryCursor.getString(0));
                result.put("Medications", medicalHistoryCursor.getString(1));
                result.put("Surgeries", medicalHistoryCursor.getString(2));
            }

            return result;
        }
        return null;
    }

    /*
    Returns true if a user with an associated user ID is a patient, false otherwise.
     */
    public boolean isPatient(long id)
    {
        if (!userExists(id))
            return false;

        HashMap patientInfo = getPatientInfo(id);
        return !(patientInfo == null || patientInfo.isEmpty());
    }



    /*---------- Physician Table methods ----------*/
    /*
    Adds physician information into database.
    Returns the User ID for a physician.

    This should be called after a physician has completed the sign up process.
     */
    public long addPhysician(long id, String name, String gender,
                             String phone, String email, String location, String[] specializations)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(dbStrings.PHYSICIAN_TABLE_KEY_ID, id);
        initialValues.put(dbStrings.PHYSICIAN_TABLE_ATTR_NAME, name);
        initialValues.put(dbStrings.PHYSICIAN_TABLE_ATTR_GENDER, gender);
        initialValues.put(dbStrings.PHYSICIAN_TABLE_ATTR_PHONE, phone);
        initialValues.put(dbStrings.PHYSICIAN_TABLE_ATTR_EMAIL, email);
        initialValues.put(dbStrings.PHYSICIAN_TABLE_ATTR_LOCATION, location);

        long result = db.insert(dbStrings.PHYSICIAN_TABLE_NAME, null, initialValues);

        for (String specialization : specializations) {
            addPhysicianSpecialization(id, specialization);
        }

        // Insert into specialization database, return the User ID of physician
        return result;
    }

    public long addPhysician(String username, String password,
                             String name, String gender,
                             String phone, String email, String location, String[] specializations)
    {
        return addPhysician(addUser(username, password),
                                            name, gender, phone, email, location, specializations);
    }

    /*
    Returns a map of information regarding a physician.
    The index format of the keys is as follows:
    [ 0: Physician (User) ID,
        1: Name,
        2: Gender,
        3: Phone,
        4: Email,
        5: Location
    ]

    This should be called whenever you need to display specific patient information on a screen.
     */
    public HashMap<String,String> getPhysicianInfo(long id)
    {
        if (!userExists(id))
            return null;

        String[] projection = { "*" };

        String selection = dbStrings.PHYSICIAN_TABLE_KEY_ID + " = ?";
        String[] selectionArgs = { Long.toString(id) };
        HashMap<String,String> result;

        Cursor mCursor =
                db.query(
                        dbStrings.PHYSICIAN_TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null
                );
        if (mCursor != null && mCursor.moveToFirst()) {
            result = new HashMap<String,String>();
            result.put("ID", mCursor.getString(0));
            result.put("Name", mCursor.getString(1));
            result.put("Gender", mCursor.getString(2));
            result.put("Phone", mCursor.getString(3));
            result.put("Email", mCursor.getString(4));
            result.put("Location", mCursor.getString(5));
            result.put("Specializations", getPhysicianSpecializations(id).toString());

            return result;
        }
        return null;
    }

    /*
    Returns true if a user with an associated user ID is a physician, false otherwise.
     */
    public boolean isPhysician(long id)
    {
        if (!userExists(id))
            return false;

        HashMap physicianInfo = getPhysicianInfo(id);
        return !(physicianInfo == null || physicianInfo.isEmpty());
    }


    /*---------- Specializations Table methods ----------*/
    /*
    Adds a specialization for a physician (by ID) into the database.
    Returns the ID for the physician.
     */
    public long addPhysicianSpecialization(long id, String specialization)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(dbStrings.PHYSICIAN_SPECIALIZATIONS_TABLE_KEY_ID, id);
        initialValues.put(dbStrings.PHYSICIAN_SPECIALIZATIONS_TABLE_ATTR_SPECIALIZATION, specialization);

        // Insert into specialization database, return the User ID of physician
        return db.insert(dbStrings.PHYSICIAN_SPECIALIZATIONS_TABLE_NAME, null, initialValues);
    }
    /*
    Returns a list of all the specializations for a given physician (by id).
     */
    public ArrayList<String> getPhysicianSpecializations(long id)
    {
        if (!userExists(id))
            return null;

        String[] projection = {
                dbStrings.PHYSICIAN_SPECIALIZATIONS_TABLE_ATTR_SPECIALIZATION
        };

        String selection = dbStrings.PHYSICIAN_SPECIALIZATIONS_TABLE_KEY_ID + " = ?";
        String[] selectionArgs = { Long.toString(id) };
        ArrayList<String> result = new ArrayList<String>();

        Cursor mCursor =
                db.query(
                        dbStrings.PHYSICIAN_SPECIALIZATIONS_TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null
                );
        if (mCursor != null && mCursor.moveToFirst()) {
            while (mCursor.moveToNext()) {
                result.add(mCursor.getString(0));
            }
        }
        return result;
    }



    /*---------- Class Helpers ----------*/
    /*---------- User/Login Helpers ----------*/
    /*
    Adds a user into database with specified username and password.
    These two are stored inside the database, with the password being (weakly) encrypted.
    Returns the User ID of a user after adding into database.

    This should be called after a patient/physician has completed the sign up process.
     */
    private long addUser(String username, String password)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(dbStrings.LOGIN_TABLE_KEY_USERNAME, username);
        initialValues.put(dbStrings.LOGIN_TABLE_ATTR_PASSWORD, crypter.encode(password));

        // Add username and password into database
        db.insert(dbStrings.LOGIN_TABLE_NAME, null, initialValues);


        initialValues.clear();
        initialValues.put(dbStrings.USER_TABLE_ATTR_USERNAME, username);

        // Add user into main user database
        return db.insert(dbStrings.USER_TABLE_NAME, null, initialValues);
    }

    //---returns login information for specific username---
    private Cursor getLogin(String username)
    {
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                dbStrings.LOGIN_TABLE_KEY_USERNAME,
                dbStrings.LOGIN_TABLE_ATTR_PASSWORD
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = dbStrings.LOGIN_TABLE_KEY_USERNAME + " = ?";
        String[] selectionArgs = { username };

        Cursor mCursor =
                db.query(
                        dbStrings.LOGIN_TABLE_NAME,               // The table to query
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


    //---adds test users into the database (should only be used for testing)---
    private void addTestData()
    {
        // Add the user Alice Wonderland, with Username "Alice123" and Password "Alice123"
        addPatient("Alice123", "Alice123",                  // Username, password
                "Alice Wonderland",                         // Name
                "F",                                        // Gender (Female)
                "January 1, 1990",                          // Birth Date
                "Married",                                  // Marital status
                "1234567890",                               // Phone-number
                "alice@wonderland.com",                     // Email
                "1234 Example Street Irvine, CA 92617",     // Location
                "Software Engineer",                        // Occupation
                "N",                                        // Regular Exerciser?
                "Ibuprofen, dust",                          // Allergies
                "N/A",                                      // Medications
                "Right ankle surgery",                      // Surgeries
                "Winnie Pooh",                              // Physician Name
                "Spongebob Squarepants",                    // Dentist Name
                "Patrick Star"                              // Eye Doctor Name
        );
        // Add the user Bob Builder, with Username "Bob456" and Password "Bob456"
        addPatient("Bob456", "Bob456",
                "Bob Builder",
                "M",
                "February 23, 1954",
                "Single",
                "9285749483",
                "bob@builder.com",
                "789 Anteater Drive Irvine, CA 92617",
                "Construction Worker",
                "Y",
                "N/A",
                "Viagra",
                "Left rotator cuff surgery",
                "Thomas Train",
                "Kuroko Tetsuya",
                "Kagami Taiga"
        );

        // Add the physician Christian Morte into database
        addPhysician("Christian123", "Christian123",
                        "Christian Morte",
                        "M",
                        "4082345678",
                        "morte@christian.com",
                        "1234 One Road San Jose, CA 95131",
                        new String[]{"Cardiologist", "Exercise Specialist"});
        // Add the physician Adam Lorta into database
        addPhysician("Adam123", "Adam123",
                "Adam Lorta",
                "M",
                "1230987654",
                "adam@gmail.com",
                "86743 Two Street San Francisco, CA 12345",
                new String[]{"Optometrist", "Physical Therapist"});
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
        return db.insert(dbStrings.DATABASE_TABLE, null, initialValues);
    }

    //---deletes a particular title---
    public boolean deleteTitle(long rowId)
    {
        return db.delete(dbStrings.DATABASE_TABLE, KEY_ROWID +
                "=" + rowId, null) > 0;
    }

    //---retrieves all the titles---
    public Cursor getAllTitles()
    {
        return db.query(dbStrings.DATABASE_TABLE, new String[] {
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
                db.query(true, dbStrings.DATABASE_TABLE, new String[] {
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
        return db.update(dbStrings.DATABASE_TABLE, args,
                KEY_ROWID + "=" + rowId, null) > 0;
    }
}


