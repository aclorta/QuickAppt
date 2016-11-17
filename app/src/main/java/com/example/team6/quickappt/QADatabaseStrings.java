package com.example.team6.quickappt;

/**
 * Created by kenmorte on 11/16/16.
 */


public class QADatabaseStrings {
    // Create SQL command for creating the 'User' table
    public final String USER_TABLE_NAME = "UserLogin",
            USER_TABLE_KEY_ROWID = "_id",
            USER_TABLE_ATTR_USERNAME = "username",
            USER_TABLE_ATTR_PASSWORD = "username",
            USER_TABLE_CREATE =
                    "CREATE TABLE IF NOT EXISTS " + USER_TABLE_NAME +  " (" +
                            "  " + USER_TABLE_KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "  " + USER_TABLE_ATTR_USERNAME +  " VARCHAR(20)" +
                            ");";

    // Create SQL command for creating the 'Login' table
    public final String LOGIN_TABLE_NAME = "Login",
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
    public final String PATIENT_TABLE_NAME = "Patient",
            PATIENT_TABLE_KEY_ID = "_id",
            PATIENT_TABLE_ATTR_NAME = "name",
            PATIENT_TABLE_ATTR_GENDER = "gender",
            PATIENT_TABLE_ATTR_BIRTH = "birth",
            PATIENT_TABLE_ATTR_MARITAL_STATUS = "marital_status",
            PATIENT_TABLE_ATTR_OCCUPATION = "occupation",
            PATIENT_TABLE_ATTR_PHONE = "phone",
            PATIENT_TABLE_ATTR_EMAIL = "email",
            PATIENT_TABLE_ATTR_LOCATION = "location",
            PATTIENT_TABLE_ATTR_TOBACCO_SMOKER = "tobacco_smoker",
            PATIENT_TABLE_ATTR_REGULAR_EXERCISER = "regular_exerciser",
            PATIENT_TABLE_ATTR_PHYSICIAN_NAME = "physician_name",
            PATIENT_TABLE_ATTR_DENTIST_NAME = "dentist_name",
            PATIENT_TABLE_ATTR_EYE_DOCTOR_NAME = "eye_doctor_name",
            PATIENT_TABLE_CREATE =
                    "CREATE TABLE IF NOT EXISTS " + PATIENT_TABLE_NAME + " (" +
                            "  " + PATIENT_TABLE_KEY_ID + " INTEGER NOT NULL," +
                            "  " + PATIENT_TABLE_ATTR_NAME + " VARCHAR(50)," +
                            "  " + PATIENT_TABLE_ATTR_GENDER + " CHAR(1)," +
                            "  " + PATIENT_TABLE_ATTR_BIRTH + " DATETIME," +
                            "  " + PATIENT_TABLE_ATTR_MARITAL_STATUS + " VARCHAR(20)," +
                            "  " + PATIENT_TABLE_ATTR_OCCUPATION + " VARCHAR(20)," +
                            "  " + PATIENT_TABLE_ATTR_PHONE + " CHAR(10)," +
                            "  " + PATIENT_TABLE_ATTR_EMAIL + " VARCHAR(20)," +
                            "  " + PATIENT_TABLE_ATTR_LOCATION + " VARCHAR(50)," +
                            "  " + PATTIENT_TABLE_ATTR_TOBACCO_SMOKER + " CHAR(1)," +
                            "  " + PATIENT_TABLE_ATTR_REGULAR_EXERCISER + " CHAR(1)," +
                            "  " + PATIENT_TABLE_ATTR_PHYSICIAN_NAME + " VARCHAR(20)," +
                            "  " + PATIENT_TABLE_ATTR_DENTIST_NAME + " VARCHAR(20)," +
                            "  " + PATIENT_TABLE_ATTR_EYE_DOCTOR_NAME + " VARCHAR(20)," +
                            "  PRIMARY KEY (" + PATIENT_TABLE_KEY_ID + ")," +
                            "  FOREIGN KEY (" + PATIENT_TABLE_KEY_ID + ") REFERENCES " + USER_TABLE_NAME + "(" + USER_TABLE_KEY_ROWID + ")" +
                            " ON DELETE CASCADE " +
                            " ON UPDATE CASCADE " +
                            ");";

    public final String PATIENT_MEDICAL_HISTORY_TABLE_NAME = "PatientMedicalHistory",
            PATIENT_MEDICAL_HISTORY_TABLE_KEY_ID = "_id",
            PATIENT_MEDICAL_HISTORY_TABLE_ATTR_ALLERGIES = "allergies",
            PATIENT_MEDICAL_HISTORY_TABLE_ATTR_MEDICATIONS = "medications",
            PATIENT_MEDICAL_HISTORY_TABLE_ATTR_SURGERIES = "surgeries",
            PATIENT_MEDICAL_HISTORY_TABLE_CREATE =
                    "CREATE TABLE IF NOT EXISTS " + PATIENT_MEDICAL_HISTORY_TABLE_NAME + " (" +
                            "  " + PATIENT_MEDICAL_HISTORY_TABLE_KEY_ID + " INTEGER NOT NULL," +
                            "  " + PATIENT_MEDICAL_HISTORY_TABLE_ATTR_ALLERGIES + " VARCHAR(50)," +
                            "  " + PATIENT_MEDICAL_HISTORY_TABLE_ATTR_MEDICATIONS + " VARCHAR(50)," +
                            "  " + PATIENT_MEDICAL_HISTORY_TABLE_ATTR_SURGERIES + " VARCHAR(50)," +
                            "  PRIMARY KEY (" + PATIENT_MEDICAL_HISTORY_TABLE_KEY_ID + ")," +
                            "  FOREIGN KEY (" + PATIENT_MEDICAL_HISTORY_TABLE_KEY_ID + ") REFERENCES " + PATIENT_TABLE_NAME + "(" + PATIENT_TABLE_KEY_ID + ")" +
                            " ON DELETE CASCADE " +
                            " ON UPDATE CASCADE " +
                            ");";

    // Create SQL command for creating the 'Physician' table
    public final String PHYSICIAN_TABLE_NAME = "Physician",
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
    public final String SPECIALIZATION_TABLE_NAME = "Specialization",
            SPECIALIZATION_TABLE_KEY_NAME = "name",
            SPECIALIZATION_TABLE_ATTR_TAGS = "tags",
            SPECIALIZATION_TABLE_CREATE =
                    "CREATE TABLE IF NOT EXISTS " + SPECIALIZATION_TABLE_NAME + " (" +
                            "  " + SPECIALIZATION_TABLE_KEY_NAME + " VARCHAR(50)," +
                            "  " + SPECIALIZATION_TABLE_ATTR_TAGS + " VARCHAR(100)," +
                            "  PRIMARY KEY (" + SPECIALIZATION_TABLE_KEY_NAME + ")" +
                            ");";

    // Create SQL command for creating the 'PhysicianSpecialization' table
    public final String PHYSICIAN_SPECIALIZATIONS_TABLE_NAME = "PhysicianSpecializations",
            PHYSICIAN_SPECIALIZATIONS_TABLE_KEY_ID = "physician_id",
            PHYSICIAN_SPECIALIZATIONS_TABLE_KEY_SPECIALIZATION = "specialization",
            PHYSICIAN_SPECIALIZATIONS_TABLE_CREATE =
                    "CREATE TABLE IF NOT EXISTS " + PHYSICIAN_SPECIALIZATIONS_TABLE_NAME + " (" +
                            "  " + PHYSICIAN_SPECIALIZATIONS_TABLE_KEY_ID + " INTEGER NOT NULL," +
                            "  " + PHYSICIAN_SPECIALIZATIONS_TABLE_KEY_SPECIALIZATION + " VARCHAR(50) NOT NULL," +
                            "  PRIMARY KEY (" + PHYSICIAN_SPECIALIZATIONS_TABLE_KEY_ID + ", " +  PHYSICIAN_SPECIALIZATIONS_TABLE_KEY_SPECIALIZATION + ")," +
                            "  FOREIGN KEY (" + PHYSICIAN_SPECIALIZATIONS_TABLE_KEY_ID + ") REFERENCES " + USER_TABLE_NAME + "(" + USER_TABLE_ATTR_USERNAME + ")" +
                            " ON DELETE CASCADE " +
                            " ON UPDATE CASCADE " +
                            ");";

    // Create SQL command for creating the 'Appointment' table
    public final String APPOINTMENT_TABLE_NAME = "Appointment",
            APPOINTMENT_TABLE_KEY_PATIENT_ID = "patient_id",
            APPOINTMENT_TABLE_KEY_PHYSICIAN_ID = "physician_id",
            APPOINTMENT_TABLE_KEY_START_TIME = "start_time",
            APPOINTMENT_TABLE_ATTR_END_TIME = "end_time",
            APPOINTMENT_TABLE_CREATE =
                    "CREATE TABLE IF NOT EXISTS " + APPOINTMENT_TABLE_NAME + " (" +
                            "  " + APPOINTMENT_TABLE_KEY_PATIENT_ID + " INTEGER NOT NULL," +
                            "  " + APPOINTMENT_TABLE_KEY_PHYSICIAN_ID + " INTEGER NOT NULL," +
                            "  " + APPOINTMENT_TABLE_KEY_START_TIME + " DATETIME NOT NULL," +
                            "  " + APPOINTMENT_TABLE_ATTR_END_TIME + " DATETIME NOT NULL," +
                            "  PRIMARY KEY (" + APPOINTMENT_TABLE_KEY_PATIENT_ID + ", " + APPOINTMENT_TABLE_KEY_PHYSICIAN_ID + ", " + APPOINTMENT_TABLE_KEY_START_TIME +  ")," +
                            "  FOREIGN KEY (" + APPOINTMENT_TABLE_KEY_PATIENT_ID + ") REFERENCES " + PATIENT_TABLE_NAME + "(" + PATIENT_TABLE_KEY_ID + ")" +
                            "  ON DELETE CASCADE " +
                            "  ON UPDATE CASCADE, " +
                            "  FOREIGN KEY (" + APPOINTMENT_TABLE_KEY_PHYSICIAN_ID + ") REFERENCES " + PHYSICIAN_TABLE_NAME + "(" + PHYSICIAN_TABLE_KEY_ID + ")" +
                            "  ON DELETE SET NULL " +
                            "  ON UPDATE CASCADE " +
                            ");";

    public final String DATABASE_NAME = "QuickAppt";
    public final String DATABASE_TABLE = "titles";
    public final int DATABASE_VERSION = 1;

    /* IMPORTANT: update this array when adding new tables onto the database */
    public final String[] DATABASE_TABLES = {
            USER_TABLE_NAME,
            LOGIN_TABLE_NAME,
            PATIENT_TABLE_NAME,
            PATIENT_MEDICAL_HISTORY_TABLE_NAME,
            PHYSICIAN_TABLE_NAME,
            SPECIALIZATION_TABLE_NAME,
            PHYSICIAN_SPECIALIZATIONS_TABLE_NAME,
            APPOINTMENT_TABLE_NAME
    };
}
