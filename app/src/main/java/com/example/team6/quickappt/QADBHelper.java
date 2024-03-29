/**
 * Created by kenmorte on 11/13/16.
 */
package com.example.team6.quickappt;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Address;
import android.util.Pair;
import android.location.Geocoder;
import android.location.Location;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Calendar;
import java.util.HashMap;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.text.ParsePosition;

public class QADBHelper
{
    private static boolean initialized = false;

    private final Context context;
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;
    private QADatabaseStrings dbStrings;
    private PasswordCrypter crypter;
    private SimpleDateFormat dateFormatter;
    private Geocoder geocoder;

    public QADBHelper(Context ctx)
    {
        this.context = ctx;
        dbStrings = new QADatabaseStrings();
        DBHelper = new DatabaseHelper(context, dbStrings);
        crypter = new PasswordCrypter();
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        geocoder = new Geocoder(this.context);


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

    public class PatientAppointmentInfo
    {
        private long physicianID;
        private Date startDateTime;
        private Date endDateTime;

        public PatientAppointmentInfo(long id, Date start, Date end) {
            physicianID = id;
            startDateTime = start;
            endDateTime = end;
        }

        // Getter methods
        public long physicianID() {
            return physicianID;
        }
        public Date startDateTime() {
            return startDateTime;
        }
        public Date endDateTime() {
            return endDateTime;
        }

        @Override
        public String toString() {
            return "PatientAppointmentInfo{ PhysicianID: " + physicianID +
                                            ", StartDateTime: " + startDateTime +
                                            ", EndDateTime: " + endDateTime + " }";
        }
    }
    public class PhysicianAppointmentInfo
    {
        private long patientID;
        private Date startDateTime;
        private Date endDateTime;

        public PhysicianAppointmentInfo(long id, Date start, Date end) {
            patientID = id;
            startDateTime = start;
            endDateTime = end;
        }

        // Getter methods
        public long patientID() {
            return patientID;
        }
        public Date startDateTime() {
            return startDateTime;
        }
        public Date endDateTime() {
            return endDateTime;
        }

        @Override
        public String toString() {
            return "PhysicianAppointmentInfo{ PatientID: " + patientID +
                    ", StartDateTime: " + startDateTime +
                    ", EndDateTime: " + endDateTime + " }";
        }
    }


    private enum AppointmentQueryType {
        ALL, PAST, UPCOMING
    }

    //---opens the database---
    public QADBHelper open() throws SQLException
    {
        db = DBHelper.getWritableDatabase();

        // IMPORTANT: If you don't want to start off with a fresh database each time you restart app, uncomment.
//        dropTables();

        db.execSQL(dbStrings.USER_TABLE_CREATE);
        db.execSQL(dbStrings.LOGIN_TABLE_CREATE);
        db.execSQL(dbStrings.PATIENT_TABLE_CREATE);
        db.execSQL(dbStrings.PHYSICIAN_TABLE_CREATE);
        db.execSQL(dbStrings.SPECIALIZATION_TABLE_CREATE);
        db.execSQL(dbStrings.PHYSICIAN_SPECIALIZATIONS_TABLE_CREATE);
        db.execSQL(dbStrings.APPOINTMENT_TABLE_CREATE);
        db.execSQL(dbStrings.PATIENT_MEDICAL_HISTORY_TABLE_CREATE);

        System.out.println("Initialized = " + initialized);

        if (!initialized) {
            addTestData();

            // Add null user into database
            addPatient(0,"","",getDate(0,0,0),"","","","","","","","","","","","","");
        }

        initialized = true;
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
    Returns true if a given patient ID is a null patient (for time slot blockages).
     */
    public boolean isNullPatient(long patientID)
    {
        return patientID == 0;
    }

    /*
    Adds patient information into database.
    Returns the User ID for a patient.

    This should be called after a patient has completed the sign up process.
     */
    public long addPatient(long id, String name, String gender, Date birthDate, String maritalStatus,
                           String phone, String email, String location,
                           String occupation,
                           String tobaccoSmoker, String regularExerciser,
                           String allergies, String medications, String surgeries,
                           String physicianName, String dentistName, String eyeDoctorName)
    {
        // Set patient attributes to content values map
        ContentValues initialValues = new ContentValues();
        initialValues.put(dbStrings.PATIENT_TABLE_KEY_ID, id);
        initialValues.put(dbStrings.PATIENT_TABLE_ATTR_NAME, name);
        initialValues.put(dbStrings.PATIENT_TABLE_ATTR_GENDER, gender);
        initialValues.put(dbStrings.PATIENT_TABLE_ATTR_BIRTH, birthDate.toString());
        initialValues.put(dbStrings.PATIENT_TABLE_ATTR_MARITAL_STATUS, maritalStatus);
        initialValues.put(dbStrings.PATIENT_TABLE_ATTR_OCCUPATION, occupation);
        initialValues.put(dbStrings.PATIENT_TABLE_ATTR_PHONE, phone);
        initialValues.put(dbStrings.PATIENT_TABLE_ATTR_EMAIL, email);
        initialValues.put(dbStrings.PATIENT_TABLE_ATTR_LOCATION, location);
        initialValues.put(dbStrings.PATTIENT_TABLE_ATTR_TOBACCO_SMOKER, tobaccoSmoker);
        initialValues.put(dbStrings.PATIENT_TABLE_ATTR_REGULAR_EXERCISER, regularExerciser);
        initialValues.put(dbStrings.PATIENT_TABLE_ATTR_PHYSICIAN_NAME, physicianName);
        initialValues.put(dbStrings.PATIENT_TABLE_ATTR_DENTIST_NAME, dentistName);
        initialValues.put(dbStrings.PATIENT_TABLE_ATTR_EYE_DOCTOR_NAME, eyeDoctorName);

        // Insert into database, return the User ID of patient
        long result = db.insert(dbStrings.PATIENT_TABLE_NAME, null, initialValues);

        initialValues = new ContentValues();
        initialValues.put(dbStrings.PATIENT_MEDICAL_HISTORY_TABLE_KEY_ID, id);
        initialValues.put(dbStrings.PATIENT_MEDICAL_HISTORY_TABLE_ATTR_ALLERGIES, allergies);
        initialValues.put(dbStrings.PATIENT_MEDICAL_HISTORY_TABLE_ATTR_MEDICATIONS, medications);
        initialValues.put(dbStrings.PATIENT_MEDICAL_HISTORY_TABLE_ATTR_SURGERIES, surgeries);

        db.insert(dbStrings.PATIENT_MEDICAL_HISTORY_TABLE_NAME, null, initialValues);
        return result;
    }

    public long addPatient(String username, String password,
                           String name, String gender, Date birthDate, String maritalStatus,
                           String phone, String email, String location,
                           String occupation,
                           String tobaccoSmoker, String regularExerciser,
                           String allergies, String medications, String surgeries,
                           String physicianName, String dentistName, String eyeDoctorName)
    {
        return addPatient(addUser(username, password),
                            name, gender, birthDate, maritalStatus,
                            phone, email, location,
                            occupation, tobaccoSmoker, regularExerciser,
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
    public HashMap<String,String> getPatientInfo(long id)
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

            // Get the medical history for the patient
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
                             String phone, String email,
                             String location, int startHour, int endHour,
                             String[] specializations)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(dbStrings.PHYSICIAN_TABLE_KEY_ID, id);
        initialValues.put(dbStrings.PHYSICIAN_TABLE_ATTR_NAME, name);
        initialValues.put(dbStrings.PHYSICIAN_TABLE_ATTR_GENDER, gender);
        initialValues.put(dbStrings.PHYSICIAN_TABLE_ATTR_PHONE, phone);
        initialValues.put(dbStrings.PHYSICIAN_TABLE_ATTR_EMAIL, email);
        initialValues.put(dbStrings.PHYSICIAN_TABLE_ATTR_LOCATION, location);
        initialValues.put(dbStrings.PHYSICIAN_TABLE_ATTR_START_HOUR, startHour);
        initialValues.put(dbStrings.PHYSICIAN_TABLE_ATTR_END_HOUR, endHour);

        long result = db.insert(dbStrings.PHYSICIAN_TABLE_NAME, null, initialValues);

        // Insert into specialization database
        for (String specialization : specializations) {
            addPhysicianSpecialization(id, specialization);
        }

        // return the User ID of physician
        return id;
    }

    public long addPhysician(String username, String password,
                             String name, String gender,
                             String phone, String email,
                             String location, int startHour, int endHour,
                             String[] specializations)
    {
        return addPhysician(addUser(username, password),
                name, gender, phone, email,
                location, startHour, endHour,
                specializations);
    }

    /*
    Returns a map of information regarding a physician.
    The index format of the keys is as follows:
    [ 0: Physician (User) ID,
        1: Name,
        2: Gender,
        3: Phone,
        4: Email,
        5: Location,
        6: Start Hour,
        7: End Hour,
        8: Specializations
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
            result.put("StartHour", mCursor.getString(6));
            result.put("EndHour", mCursor.getString(7));
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
        initialValues.put(dbStrings.PHYSICIAN_SPECIALIZATIONS_TABLE_KEY_SPECIALIZATION, specialization);

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
                dbStrings.PHYSICIAN_SPECIALIZATIONS_TABLE_KEY_SPECIALIZATION
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
            result.add(mCursor.getString(0));
            while (mCursor.moveToNext()) {
                result.add(mCursor.getString(0));
            }
        }
        return result;
    }

    /*
    Returns a list of all the physicians with a given specialization.
        - If using the location parameter with this, then the search is specified by the location,
            using the range parameter (if specified) as a the max distance accepted.
    Each list is a map containing information for each physician, which was taken from the
        getPhysicianInfo(id) method.
    Returns null if no physicians found or error occurred.
    */
    public ArrayList<HashMap<String,String>> getPhysiciansWithSpecialization(String specialization,
                                                                             String location,
                                                                             float range,
                                                                             Date startDateRange,
                                                                             Date endDateRange)
    {
        String[] projection = {
                dbStrings.PHYSICIAN_SPECIALIZATIONS_TABLE_KEY_ID
        };

        String selection = dbStrings.PHYSICIAN_SPECIALIZATIONS_TABLE_KEY_SPECIALIZATION + " = ?";
        String[] selectionArgs = { specialization };
        ArrayList<HashMap<String,String>> result;

        if (specialization.equalsIgnoreCase("any")) {
            selection = null;
            selectionArgs = null;
        }

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
            result = new ArrayList<HashMap<String,String>>();

            HashMap<String,String> physicianInfo = getPhysicianInfo(mCursor.getLong(0));
            float distanceToPhysician = getDistance(location, physicianInfo.get("Location"));

            if (distanceToPhysician <= range && getTimeSlotsAvailableForPhysician( Long.parseLong(physicianInfo.get("ID")),
                                                                                    startDateRange, endDateRange).size() > 0) {
                result.add(physicianInfo);
            }

            while (mCursor.moveToNext()) {
                physicianInfo = getPhysicianInfo(mCursor.getLong(0));
                distanceToPhysician = getDistance(location, physicianInfo.get("Location"));

                if (distanceToPhysician <= range && getTimeSlotsAvailableForPhysician( Long.parseLong(physicianInfo.get("ID")),
                        startDateRange, endDateRange).size() > 0) {
                    result.add(physicianInfo);
                }
            }
            return result;
        }
        return null;
    }
    public ArrayList<HashMap<String,String>> getPhysiciansWithSpecialization(String specialization,
                                                                             Date startDateRange,
                                                                             Date endDateRange)
    {
        String[] projection = {
                dbStrings.PHYSICIAN_SPECIALIZATIONS_TABLE_KEY_ID
        };

        String selection = dbStrings.PHYSICIAN_SPECIALIZATIONS_TABLE_KEY_SPECIALIZATION + " = ?";
        String[] selectionArgs = { specialization };
        ArrayList<HashMap<String,String>> result;

        if (specialization.equalsIgnoreCase("any")) {
            selection = null;
            selectionArgs = null;
        }

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
            result = new ArrayList<HashMap<String,String>>();

            HashMap<String,String> physicianInfo = getPhysicianInfo(mCursor.getLong(0));

            if (getTimeSlotsAvailableForPhysician( Long.parseLong(physicianInfo.get("ID")),
                    startDateRange, endDateRange).size() > 0) {
                result.add(physicianInfo);
            }

            while (mCursor.moveToNext()) {
                physicianInfo = getPhysicianInfo(mCursor.getLong(0));

                if (getTimeSlotsAvailableForPhysician( Long.parseLong(physicianInfo.get("ID")),
                        startDateRange, endDateRange).size() > 0) {
                    result.add(physicianInfo);
                }
            }
            return result;
        }
        return null;
    }
    public ArrayList<HashMap<String,String>> getPhysiciansWithSpecialization(String specialization,
                                                                             String location,
                                                                             float range)
    {
        String[] projection = {
                dbStrings.PHYSICIAN_SPECIALIZATIONS_TABLE_KEY_ID
        };

        String selection = dbStrings.PHYSICIAN_SPECIALIZATIONS_TABLE_KEY_SPECIALIZATION + " = ?";
        String[] selectionArgs = { specialization };
        ArrayList<HashMap<String,String>> result;

        if (specialization.equalsIgnoreCase("any")) {
            selection = null;
            selectionArgs = null;
        }

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
            result = new ArrayList<HashMap<String,String>>();

            HashMap<String,String> physicianInfo = getPhysicianInfo(mCursor.getLong(0));
            float distanceToPhysician = getDistance(location, physicianInfo.get("Location"));
            System.out.println("Distance from " + location + " to " + physicianInfo.get("Location") + " = " + distanceToPhysician);

            if (distanceToPhysician <= range) {
                result.add(physicianInfo);
            }

            while (mCursor.moveToNext()) {
                physicianInfo = getPhysicianInfo(mCursor.getLong(0));
                distanceToPhysician = getDistance(location, physicianInfo.get("Location"));

                if (distanceToPhysician <= range) {
                    result.add(physicianInfo);
                }
            }
            return result;
        }
        return null;
    }
    public ArrayList<HashMap<String,String>> getPhysiciansWithSpecialization(String specialization,
                                                                             String location)
    {
        return getPhysiciansWithSpecialization(specialization, location, 25);
    }
    public ArrayList<HashMap<String,String>> getPhysiciansWithSpecialization(String specialization)
    {
        String[] projection = {
                dbStrings.PHYSICIAN_SPECIALIZATIONS_TABLE_KEY_ID
        };

        String selection = dbStrings.PHYSICIAN_SPECIALIZATIONS_TABLE_KEY_SPECIALIZATION + " = ?";
        String[] selectionArgs = { specialization };
        ArrayList<HashMap<String,String>> result;

        if (specialization.equalsIgnoreCase("any")) {
            selection = null;
            selectionArgs = null;
        }

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
            result = new ArrayList<HashMap<String,String>>();

            result.add(getPhysicianInfo(mCursor.getLong(0)));

            while (mCursor.moveToNext()) {
                result.add(getPhysicianInfo(mCursor.getLong(0)));
            }
            return result;
        }
        return null;
    }




    /*---------- Appointment Table methods ----------*/
    /*
    Adds an appointment to the database for a given patient ID, physician ID, and time slot.
    Returns the row inserted on the Appointment table.

    Returns -1 if there was an error creating the appointment (eg. gave patient ID for physicianID parameter).
     */
    public long addAppointment(long patientID, long physicianID, Date startDate, Date endDate)
    {
        if (!isPatient(patientID) || !isPhysician(physicianID))     // Wrong user ID's given
            return -1;
        if (endDate.before(startDate))                              // End date is before start Date
            return -1;
        if (!timeSlotAvailableForPhysician(physicianID, startDate, endDate))  // time slot not available for physician
            return -1;

        ContentValues initialValues = new ContentValues();
        initialValues.put(dbStrings.APPOINTMENT_TABLE_KEY_PATIENT_ID, patientID);
        initialValues.put(dbStrings.APPOINTMENT_TABLE_KEY_PHYSICIAN_ID, physicianID);

        // Put the UNIX timestamp of start/end date into database
        initialValues.put(dbStrings.APPOINTMENT_TABLE_KEY_START_TIME, startDate.getTime());
        initialValues.put(dbStrings.APPOINTMENT_TABLE_ATTR_END_TIME, endDate.getTime());

        return db.insert(dbStrings.APPOINTMENT_TABLE_NAME, null, initialValues);
    }
    public long addAppointment(long patientID, long physicianID,
                               int startYear, int startMonth, int startDay, int startHour, int startMinute,
                               int endYear, int endMonth, int endDay, int endHour, int endMinute)
    {
        return addAppointment(patientID,
                            physicianID,
                            getDate(startYear, startMonth, startDay, startHour, startMinute),   // Start DateTime
                            getDate(endYear, endMonth, endDay, endHour, endMinute)              // End DateTime
        );
    }

    /*
    Adds a timeslot block (masked as an Appointment) to the database for a given physician ID and time slot.
    Returns the row inserted on the Appointment table.

    Returns -1 if there was an error creating the appointment (eg. physician doesn't exist, etc.).
     */
    public long addTimeBlockForPhysician(long physicianID, Date startDateTime, Date endDateTime)
    {
        return addAppointment(0, physicianID, startDateTime, endDateTime);
    }

    /*
    Returns a sorted list of ALL, UPCOMING, OR PAST appointments (from earliest to latest) for a patient given his/her ID.
        Each appointment has:
            - Physician ID
            - Start date/time
            - End date/time
    Returns empty list if no appointments listed for a patient.
    Returns null if ID did not belong to a patient.
     */
    public ArrayList<PatientAppointmentInfo> getAllAppointmentsForPatient(long patientID)
    {
        return getAppointmentsForPatient(patientID, AppointmentQueryType.ALL);
    }
    public ArrayList<PatientAppointmentInfo> getUpcomingAppointmentsForPatient(long patientID)
    {
        return getAppointmentsForPatient(patientID, AppointmentQueryType.UPCOMING);
    }
    public ArrayList<PatientAppointmentInfo> getPastAppointmentsForPatient(long patientID)
    {
        return getAppointmentsForPatient(patientID, AppointmentQueryType.PAST);
    }

    /*
    Returns true if a time slot is available for a patient.
    The time slot is defined by a start date-time and an end date-time.
     */
    public boolean timeSlotAvailableForPatient(long patientID, Date startDateTime, Date endDateTime)
    {
        ArrayList<PatientAppointmentInfo> appointments = getUpcomingAppointmentsForPatient(patientID);
        long startTime = startDateTime.getTime(),
                endTime = endDateTime.getTime();

        for (PatientAppointmentInfo appointment : appointments) {
            long otherApptStart = appointment.startDateTime.getTime(),
                    otherApptEnd = appointment.endDateTime.getTime();

            if ( (startTime >= otherApptStart && startTime < otherApptEnd) ||
                    (endTime > otherApptStart && endTime <= otherApptEnd) )
                return false;
        }
        return true;
    }

    /*
    Returns a sorted list of ALL, UPCOMING, OR PAST appointments (from earliest to latest) for a physician given his/her ID.
        Each appointment has:
            - Patient ID
            - Start date/time
            - End date/time
    Returns empty list if no appointments listed for a physician.
    Returns null if ID did not belong to a physician.
     */
    public ArrayList<PhysicianAppointmentInfo> getAllAppointmentsForPhysician(long physicianID)
    {
        return getAppointmentsForPhysician(physicianID, AppointmentQueryType.ALL);
    }
    public ArrayList<PhysicianAppointmentInfo> getUpcomingAppointmentsForPhysician(long physicianID)
    {
        return getAppointmentsForPhysician(physicianID, AppointmentQueryType.UPCOMING);
    }
    public ArrayList<PhysicianAppointmentInfo> getPastAppointmentsForPhysician(long physicianID)
    {
        return getAppointmentsForPhysician(physicianID, AppointmentQueryType.PAST);
    }

    /*
    Returns true if a time slot is available for a physician.
    The time slot is defined by a start date-time and an end date-time.
     */
    public boolean timeSlotAvailableForPhysician(long physicianID, Date startDateTime, Date endDateTime)
    {
        if (startDateTime.after(endDateTime))
            return false;

        ArrayList<PhysicianAppointmentInfo> appointments = getUpcomingAppointmentsForPhysician(physicianID);
        long startTime = startDateTime.getTime(),
                endTime = endDateTime.getTime();

        HashMap<String,String> physicianInfo = getPhysicianInfo(physicianID);
        int startAvailability = Integer.parseInt(physicianInfo.get("StartHour")),
                endAvailability = Integer.parseInt(physicianInfo.get("EndHour")),
                startAppointmentHour = getDateInfo(startDateTime, Calendar.HOUR_OF_DAY),
                endAppointmentHour = getDateInfo(endDateTime, Calendar.HOUR_OF_DAY);

        if (startAppointmentHour < startAvailability || endAppointmentHour > endAvailability)
            return false;

        for (PhysicianAppointmentInfo appointment : appointments) {
            long otherApptStart = appointment.startDateTime.getTime(),
                    otherApptEnd = appointment.endDateTime.getTime();

            if ( (startTime >= otherApptStart && startTime < otherApptEnd) ||
                    (endTime > otherApptStart && endTime <= otherApptEnd) )
                return false;
        }
        return true;
    }
    /*
    Returns a list of pairs that contain (start-datetime, end-datetime) for open slots for appointments.
    Returns null if start date time before end date time, or an error occurred.

    The max length the list returns is 60, to avoid handling too much results.
     */
    public ArrayList<Pair<Date,Date>> getTimeSlotsAvailableForPhysician(long physicianID, Date startDateTime, Date endDateTime)
    {
        if (startDateTime.after(endDateTime))
            return null;

        ArrayList<Pair<Date,Date>> result = new ArrayList<Pair<Date,Date>>();

        int     startYear = getDateInfo(startDateTime, Calendar.YEAR),
                startMonth = getDateInfo(startDateTime, Calendar.MONTH) + 1,
                startDay = getDateInfo(startDateTime, Calendar.DAY_OF_MONTH),
                startHour = getDateInfo(startDateTime, Calendar.HOUR_OF_DAY),
                startMinute = getDateInfo(startDateTime, Calendar.MINUTE),

                endYear = getDateInfo(endDateTime, Calendar.YEAR),
                endMonth = getDateInfo(endDateTime, Calendar.MONTH) + 1,
                endDay = getDateInfo(endDateTime, Calendar.DAY_OF_MONTH),
                endHour = getDateInfo(endDateTime, Calendar.HOUR_OF_DAY),
                endMinute = getDateInfo(endDateTime, Calendar.MINUTE);

        if (startMinute > 30) {
            startDateTime = getDate(startYear, startMonth, startDay, startHour + 1, 0);

        } else if (startMinute > 0 && startMinute < 30) {
            startDateTime = getDate(startYear, startMonth, startDay, startHour, 30);
        }

        HashMap<String,String> physicianInfo = getPhysicianInfo(physicianID);
        int startAvailabiltiyHour = Integer.parseInt(physicianInfo.get("StartHour")),
                endAvailabilityHour = Integer.parseInt(physicianInfo.get("EndHour"));

        int maxResultSize = 60;
        while (startDateTime.before(endDateTime)) {
            startYear = getDateInfo(startDateTime, Calendar.YEAR);
            startMonth = getDateInfo(startDateTime, Calendar.MONTH) + 1;
            startDay = getDateInfo(startDateTime, Calendar.DAY_OF_MONTH);
            startHour = getDateInfo(startDateTime, Calendar.HOUR_OF_DAY);
            startMinute = getDateInfo(startDateTime, Calendar.MINUTE);

            Date newApptStart = startDateTime,
                    newApptEnd = getDate(startYear, startMonth, startDay, startHour, startMinute + 30);

            // If time slot reaches beyond physician's availability hours, continue to next day
            if (newApptEnd.after(getDate(startYear, startMonth, startDay, endAvailabilityHour, 0))) {
                startDateTime = getDate(startYear, startMonth, startDay+1, startAvailabiltiyHour, 0);
                continue;

            } else if (newApptStart.before(getDate(startYear, startMonth, startDay, startAvailabiltiyHour, 0))) {
                startDateTime = getDate(startYear, startMonth, startDay, startAvailabiltiyHour, 0);
                continue;
            }


            if (timeSlotAvailableForPhysician(physicianID, newApptStart, newApptEnd)) {
                result.add(new Pair<Date,Date>(newApptStart, newApptEnd));
                System.out.println("start = " + newApptStart + ", end = " + newApptEnd);
                if (result.size() > maxResultSize)
                    return result;
            }

            startDateTime = newApptEnd;
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
        /*
        ADDING PATIENTS TO THE TABLE
         */
        try {
            addPatient("Alice123",                              // Username
                    "Alice123",                                 // Password
                    "Alice Wonderland",                         // Name
                    "F",                                        // Gender (Female)
                    getDate(1990, 1, 2),                        // Birth Date
                    "Married",                                  // Marital status
                    "1234567890",                               // Phone-number
                    "alice@wonderland.com",                     // Email
                    "1234 Example Street Irvine, CA 92617",     // Location
                    "Software Engineer",                        // Occupation
                    "Y",                                        // Tobacco Smoker ?
                    "N",                                        // Regular Exerciser?
                    "Ibuprofen, dust",                          // Allergies
                    "N/A",                                      // Medications
                    "Right ankle surgery",                      // Surgeries
                    "Winnie Pooh",                              // Physician Name
                    "Spongebob Squarepants",                    // Dentist Name
                    "Patrick Star"                              // Eye Doctor Name
            );
            // Add the patient Bob Builder, with Username "Bob456" and Password "Bob456"
            addPatient("Bob456",
                    "Bob456",
                    "Bob Builder",
                    "M",
                    getDate(1954, 2, 24),
                    "Single",
                    "9285749483",
                    "bob@builder.com",
                    "789 Anteater Drive Irvine, CA 92617",
                    "Construction Worker",
                    "N",
                    "Y",
                    "N/A",
                    "Viagra",
                    "Left rotator cuff surgery",
                    "Thomas Train",
                    "Kuroko Tetsuya",
                    "Kagami Taiga"
            );


        /*
        ADDING PHYSICIANS TO THE TABLE
         */
            // Add the physician Christian Morte into database
            addPhysician("Christian123",                                                // Username
                    "Christian123",                                             // Password
                    "Christian Morte",                                          // Name
                    "M",                                                        // Gender
                    "4082345678",                                               // Phone
                    "morte@christian.com",                                      // Email
                    "Irvine, CA",                                               // Location
                    8,18,                                                       // Hours: 8am - 6pm
                    new String[]{"Cardiologist", "Exercise Specialist"});       // Specializations

            // Add the physician Adam Lorta into database
            addPhysician("Adam123",
                    "Adam123",
                    "Adam Lorta",
                    "M",
                    "1230987654",
                    "adam@gmail.com",
                    "San Francisco, CA",
                    7, 19,                                                  // Hours: 7am - 7pm
                    new String[]{"Cardiologist", "Physical Therapist"});
            addPhysician("Crystal456",
                    "Crystal456",
                    "Crystal Yee",
                    "F",
                    "1230987654",
                    "crystal@gmail.com",
                    "Merced, CA",
                    9, 17,                                          // Hours: 9am - 5pm
                    new String[]{"Cardiologist"});


        /*
        CREATING APPOINTMENTS FOR PATIENTS/PHYSICIANS, ADDING INTO TABLE
         */
            addAppointment(1,               // Patient: Alice
                    3,                      // Physician: Christian
                    2016, 5, 4, 12, 30,      // Start Date Time: May 4, 2016 12:30 PM
                    2016, 5, 4, 1, 30);     // End Date Time: May 4, 2016, 1:30 PM
            addAppointment(1,               // Patient: Alice
                    3,                      // Physician: Christian
                    2017, 5, 4, 12, 30,      // Start Date Time: May 4, 2017 12:30 PM
                    2017, 5, 4, 1, 30);     // End Date Time: May 4, 2017, 1:30 PM
            addAppointment(1,               // Patient: Alice
                    4,                      // Physician: Adam
                    2016, 12, 4, 12, 30,      // Start Date Time: December 4, 2016 12:30 PM
                    2016, 12, 4, 1, 30);     // End Date Time: December 4, 2016, 1:30 PM
            addAppointment(1,               // Patient: Alice
                    4,                      // Physician: Adam
                    2016, 5, 6, 15, 30,      // Start Date Time: May 6, 2016 3:30 PM
                    2016, 5, 6, 16, 30);     // End Date Time: May 6, 2016, 4:30 PM
            addAppointment(2,               // Patient: Bob
                    4,                      // Physician: Adam
                    2016, 5, 6, 15, 30,      // Start Date Time: May 6, 2016 3:30 PM
                    2016, 5, 6, 16, 30);     // End Date Time: May 6, 2016, 4:30 PM
            addAppointment(2,               // Patient: Bob
                    4,                      // Physician: Adam
                    2016, 2, 26, 9, 30,      // Start Date Time: November 28, 2016 8:30 AM
                    2016, 2, 26, 10, 0);     // End Date Time: November 28, 2016, 9:00 AM
            addAppointment(2,               // Patient: Bob
                    3,                      // Physician: Christian
                    2016, 11, 28, 8, 30,      // Start Date Time: November 28, 2016 8:30 AM
                    2016, 11, 28, 9, 0);     // End Date Time: November 28, 2016, 9:00 AM

        /*
        THIS SHOULD NOT WORK - User ID 3 belongs to a physician, and physicians should not be able to book
                                appointments w/each other (unless registered as patients in a separate account).

        This returns -1, which means the appointment was not able to be added.
        */
            addAppointment(3,               // Patient: Christian (SHOULD NOT WORK)
                    4,                      // Physician: Adam
                    2016, 5, 4, 12, 30,     // Start Date Time: May 4, 2016 12:30 PM
                    2016, 5, 4, 1, 30);     // End Date Time: May 4, 2016, 1:30 PM

            addAppointment(0,
                    3,
                    2016, 11, 28, 22, 30,
                    2016, 11, 28, 24, 40);
        } catch (SQLiteConstraintException e) {
            System.out.println("Error inserting test data into database (ignoring): " + e);
        } catch (Exception e) {
            System.out.println("Error inserting test data into database (ignoring): " + e);
        }

    }


    /*---------- Location Helpers ----------*/
    public float getDistance(String location1, String location2)
    {
        if (location1 == null || location2 == null)
            return -1;

        Address address1 = getAddress(location1),
                address2 = getAddress(location2);

        if (address1 == null || address2 == null)
            return -1;

        float[] results = new float[] {0,0,0};
        Location.distanceBetween(address1.getLatitude(),
                                        address1.getLongitude(),
                                        address2.getLatitude(),
                                        address2.getLongitude(),
                                        results);
        // convert meters to miles
        return (float) (results[0] * 0.000621371);
    }
    /*
    Returns an address for a specific location, null if not available.
     */
    private Address getAddress(String location)
    {
        try {
            List<Address> result = geocoder.getFromLocationName(location, 1);
            if (result.size() == 0)
                return null;
            return result.get(0);
        } catch (IOException e) {
            System.out.println("Error: " + e);
            return null;
        }
    }

    /*---------- Appointment/Date Helpers ----------*/
    /*
    Returns a Date object given the integer parameters:
        year, month, day, hour, and minute.
     */
    public Date getDate(int year, int month, int day, int hour, int minute)
    {
        String dateTime = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":00";
        return dateFormatter.parse(dateTime, new ParsePosition(0));
    }
    public Date getDate(int year, int month, int day)
    {
        String dateTime = year + "-" + month + "-" + day + " " + "00:00:00";
        return dateFormatter.parse(dateTime, new ParsePosition(0));
    }

    /*
    Returns a year, month, day, hour, and/or minute given a Date object.
    Parameter mode can be:
        Calendar.YEAR,
        Calendar.MONTH,
        Calendar.DAY,
     */
    public int getDateInfo(Date date, int mode) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date.getTime());
        return cal.get(mode);
    }

    /*
    Returns a sorted list of ALL appointments (from earliest to latest) for a patient given his/her ID.
    Returns empty list if no appointments listed for a patient.
    Returns null if ID did not belong to a patient.
     */
    private ArrayList<PatientAppointmentInfo> getAppointmentsForPatient(long patientID, AppointmentQueryType type)
    {
        if (!isPatient(patientID))
            return null;

        ArrayList<PatientAppointmentInfo> result = new ArrayList<PatientAppointmentInfo>();
        String[] projection = { dbStrings.APPOINTMENT_TABLE_KEY_PHYSICIAN_ID,
                dbStrings.APPOINTMENT_TABLE_KEY_START_TIME,
                dbStrings.APPOINTMENT_TABLE_ATTR_END_TIME
        };

        String selection;
        String[] selectionArgs;

        if (type.equals(AppointmentQueryType.PAST)) {
            selection = dbStrings.APPOINTMENT_TABLE_KEY_PATIENT_ID + " = ? AND " +
                    dbStrings.APPOINTMENT_TABLE_KEY_START_TIME + " < ? ";
            selectionArgs = new String[]{ Long.toString(patientID), Long.toString(new Date().getTime()) };

        }
        else if (type.equals(AppointmentQueryType.UPCOMING)) {
            selection = dbStrings.APPOINTMENT_TABLE_KEY_PATIENT_ID + " = ? AND " +
                            dbStrings.APPOINTMENT_TABLE_KEY_START_TIME + " > ? ";
            selectionArgs = new String[]{ Long.toString(patientID), Long.toString(new Date().getTime()) };

        } else if (type.equals(AppointmentQueryType.ALL)) {
            selection = dbStrings.APPOINTMENT_TABLE_KEY_PATIENT_ID + " = ? ";
            selectionArgs = new String[]{ Long.toString(patientID) };

        } else {
            return null;
        }

        String orderBy = dbStrings.APPOINTMENT_TABLE_KEY_START_TIME + " ASC";


        Cursor mCursor =
                db.query(
                        dbStrings.APPOINTMENT_TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        orderBy
                );

        if (mCursor != null && mCursor.moveToFirst()) {
            long id = mCursor.getLong(0);
            Date startDateTime = new Date(mCursor.getLong(1));
            Date endDateTime = new Date(mCursor.getLong(2));

            result.add(new PatientAppointmentInfo(id, startDateTime, endDateTime));

            while (mCursor.moveToNext()) {
                id = mCursor.getLong(0);
                startDateTime = new Date(mCursor.getLong(1));
                endDateTime = new Date(mCursor.getLong(2));
                result.add(new PatientAppointmentInfo(id, startDateTime, endDateTime));
            }
        }
        return result;
    }

    /*
    Returns a sorted list of appointments (from earliest to latest) for a physician given his/her ID.
    Returns empty list if no appointments listed for a physician.
    Returns null if ID did not belong to a physician.
     */
    private ArrayList<PhysicianAppointmentInfo> getAppointmentsForPhysician(long physicianID, AppointmentQueryType type)
    {
        if (!isPhysician(physicianID))
            return null;

        ArrayList<PhysicianAppointmentInfo> result = new ArrayList<PhysicianAppointmentInfo>();
        String[] projection = { dbStrings.APPOINTMENT_TABLE_KEY_PATIENT_ID,
                dbStrings.APPOINTMENT_TABLE_KEY_START_TIME,
                dbStrings.APPOINTMENT_TABLE_ATTR_END_TIME
        };

        String selection;
        String[] selectionArgs;

        if (type.equals(AppointmentQueryType.PAST)) {
            selection = dbStrings.APPOINTMENT_TABLE_KEY_PHYSICIAN_ID + " = ? AND " +
                    dbStrings.APPOINTMENT_TABLE_KEY_START_TIME + " < ? ";
            selectionArgs = new String[]{ Long.toString(physicianID), Long.toString(new Date().getTime()) };

        }
        else if (type.equals(AppointmentQueryType.UPCOMING)) {
            selection = dbStrings.APPOINTMENT_TABLE_KEY_PHYSICIAN_ID + " = ? AND " +
                    dbStrings.APPOINTMENT_TABLE_KEY_START_TIME + " > ? ";
            selectionArgs = new String[]{ Long.toString(physicianID), Long.toString(new Date().getTime()) };

        } else if (type.equals(AppointmentQueryType.ALL)) {
            selection = dbStrings.APPOINTMENT_TABLE_KEY_PHYSICIAN_ID + " = ? ";
            selectionArgs = new String[]{ Long.toString(physicianID) };

        } else {
            return null;
        }

        String orderBy = dbStrings.APPOINTMENT_TABLE_KEY_START_TIME + " ASC";

        Cursor mCursor =
                db.query(
                        dbStrings.APPOINTMENT_TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        orderBy
                );

        if (mCursor != null && mCursor.moveToFirst()) {
            long patientID = mCursor.getLong(0);
            Date startDateTime = new Date(mCursor.getLong(1));
            Date endDateTime = new Date(mCursor.getLong(2));

            result.add(new PhysicianAppointmentInfo(patientID, startDateTime, endDateTime));

            while (mCursor.moveToNext()) {
                patientID = mCursor.getLong(0);
                startDateTime = new Date(mCursor.getLong(1));
                endDateTime = new Date(mCursor.getLong(2));
                result.add(new PhysicianAppointmentInfo(patientID, startDateTime, endDateTime));
            }
        }
        return result;
    }
}


