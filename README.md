# QuickAppt

Android App which connects users to local physicians.

# API Reference
All of the methods shown below are located in the <a href="https://github.com/aclorta/QuickAppt/blob/Database/app/src/main/java/com/example/team6/quickappt/QADBHelper.java"><i>QADBHelper.java</i></a> file inside the <i>src</i> directory. For more information on how these were implemented, please see the code for further reference. There are various comments throughout each definition as well as some documentation above each defined method.

## Table of Contents
1. [Login Methods](#Login table Methods)
  - [userExists(long id)](#userExists(long id))
  - [loginValid(String username, String password)](#loginValid(String username, String password))
  - [getUserID(String username)](#getUserID(String username))
2. [Patient Methods](#Patient Methods)
  - [isNullPatient(long patientID)](#isNullPatient(long patientID))
  - [addPatient( String username, String password, ... )](#addPatient( String username, String password, ... ))
  - [getPatientInfo(long id)](#getPatientInfo(long id))
3. [Physician Methods](#Physician Methods)
4. [Specialization Methods](#Specialization Methods)
5. [Appointment Methods](#Appointment Methods)

## QADB Helper
In order to use the API methods, you must create an instance of the QADBHelper class inside a context. After initialization, calling the `open()` method creates the database and allows for proper use of the API. Once finished using the class inside the application, it is good practice to call the `close()` method to shut down the database helper.

```java
// Instantiate the QADBHelper object
QADBHelper mDB = new QADBHelper(this);

// Open the database
mDB.open();

/*
Insert specific activity code here
*/

// Close the database
mDB.close();
```

<a name="Login table Methods"/>
## Login table Methods
```java
public boolean userExists(String username)
```
###### Return value
&emsp;Returns <i>true</i> if a user exists on the database, <i>false</i> otherwise.<br>
###### Parameters
- `String username` - username for a user

###### Example usage
```java
if ( mDB.userExists("enter username here") ) {
  // Do stuff here
}
```
======
<a name="userExists(long id)"/>
```java
public boolean userExists(long id)
```
###### Return value
&emsp;Returns <i>true</i> if a user exists on the database, <i>false</i> otherwise.<br>
###### Parameters
- `long id` - unique ID for a user

###### Example usage
```java
if ( mDB.userExists(1) ) {  // if user with ID=1 exists
  // Do stuff here
}
```
======
<a name="loginValid(String username, String password)"/>
```java
public boolean loginValid(String username, String password)
```
###### Return value
&emsp;Returns <i>true</i> if a given username-password combination matches inside the user database, false otherwise.
###### Parameters
- `String username` - username for a user
- `String password` - password for a user

###### Example usage
```java
if ( mDB.loginValid("enter username here", "enter password here") ) {
  // Do stuff here
}
```
======
<a name="getUserID(String username)"/>
```java
public long getUserID(String username)
```
###### Return value
&emsp;Returns the user ID for a specific user. If the user does not exist, then <b>0</b> is returned (which is a <i>null</i> user).
###### Parameters
- `String username` - username for a user

###### Example usage
```java
String username = mDB.getUserID("enter username here");
System.out.println("Username = " + username);
```
======
<a name="Patient Methods"/>
## Patient table Methods

<a name="isNullPatient(long patientID)"/>
```java
public boolean isNullPatient(long patientID)
```
###### Return value
&emsp;Returns true if a given patient ID is the null patient (has ID of 0).
###### Parameters
- `long patientID` - unique ID for a patient user

###### Example usage
```java
mDB.isNullPatient(0);   // true
mDB.isNullPatient(1);   // false
mDB.isNullPatient(23235235);   // false
```
======
<a name="addPatient( String username, String password, ... )"/>
```java
public long addPatient( String username, String password, 
                        String name, String gender, 
                        Date birthDate, String maritalStatus, 
                        String phone, String email, String location, String occupation, 
                        String tobaccoSmoker, String regularExerciser, 
                        String allergies, String medications, String surgeries, 
                        String physicianName, String dentistName, String eyeDoctorName)
```
###### Description
Adds a patient into the database with given parameters. There is also another `addPatient(...)` function that replaces the first two parameters of this function with the `patientID`, but this is <b>not</b> recommended, as the ID is automatically generated for user creation in this method call.

**IMPORTANT**: You should store the return value of this function throughout the program because it returns the ID of the patient user.
###### Return value
&emsp;Returns the row ID of the newly inserted row in the patient table, or -1 if an error occurred.
###### Parameters
- `String username` - username for patient user
- `String password` - password for patient user
- `String name` - name of the patient
- `String gender` - <b><u>single</u></b> character string for patient's gender (M/F)
- `Date birthDate` - Date object for the birth date of patient (refer to the `getDate(...)` method for creating dates)
- `String maritalStatus` - marital status of patient (single, married, etc.)
- `String phone` - phone number of patient (<b><u>must</u></b> be in format XXXYYYZZZZ)
- `String email` - email contact for patient
- `String location` - primary address of patient
- `String occupation` - occupation of patient
- `String tobaccoSmoker` - <b><u>single</u></b> character string for patient's status as a tobacco smoker (Y/N)
- `String regularExerciser` - <b><u>single</u></b> character string for patient's status as a regular exerciser (Y/N)
- `String allergies` - allergies the patient has
- `String medications` - medications the patient are/were taking
- `String surgeries` - past surgeries patient has had
- `String surgeries` - past surgeries patient has had
- `String physicianName` - name of patient's primary physician
- `String dentistName` - name of patient's primary dentist
- `String eyeDoctorName` - name of patient's primary optometrist

###### Example usage
```java
long patientID = mDB.addPatient(  "Alice123",                                 // Username
                                  "Alice123",                                 // Password
                                  "Alice Wonderland",                         // Name
                                  "F",                                        // Gender (Female)
                                  mDB.getDate(1990, 1, 2),                    // Birth Date: January 2, 1990
                                  "Married",                                  // Marital status
                                  "1234567890",                               // Phone-number: (123) 456-7890
                                  "alice@wonderland.com",                     // Email
                                  "1234 Example Street Irvine, CA 92617",     // Location
                                  "Software Engineer",                        // Occupation
                                  "Y",                                        // Tobacco Smoker?
                                  "N",                                        // Regular Exerciser?
                                  "Ibuprofen, dust",                          // Allergies
                                  "N/A",                                      // Medications
                                  "Right ankle surgery",                      // Surgeries
                                  "Winnie Pooh",                              // Physician Name
                                  "Spongebob Squarepants",                    // Dentist Name
                                  "Patrick Star"                              // Eye Doctor Name
);

System.out.println("The patient ID for the new patient user = " + patientID);
```
======
<a name="getPatientInfo(long id)"/>
```java
public HashMap<String,String> getPatientInfo(long id)
```
###### Return value
&emsp;Returns `HashMap<String,String>` object of information for a patient. If an error occurred (eg. ID doesn't exist), returns `null`.
###### Parameters
- `long id` - unique ID for a patient user

###### Example usage
```java
// Get patient ID
long patientID = mDB.getUserID("enter username here");

// Get patient information
HashMap<String,String> patientInfo = mDB.getPatientInfo(patientID);

// Output patient information
System.out.println( " ID = " + patientInfo["ID"] +
                    " Name = " + patientInfo["Name"] +
                    " Gender = " + patientInfo["Gender"] +
                    " Birth = " + patientInfo["Birth"] +
                    " Marital Status = " + patientInfo["MaritalStatus"] +
                    " Occupation = " + patientInfo["Occupation"] +
                    " Phone = " + patientInfo["Phone"] +
                    " Email = " + patientInfo["Email"] +
                    " Location = " + patientInfo["Location"] +
                    " Tobacco Smoker? = " + patientInfo["TobaccoSmoker"] +
                    " Regular Exerciser? = " + patientInfo["RegularExerciser"] +
                    " Primary Care Physician = " + patientInfo["PhysicianName"] +
                    " Primary Dentist = " + patientInfo["DentistName"] +
                    " Primary Optometrist = " + patientInfo["EyeDoctorName"] +
                    " Allergies = " + patientInfo["Allergies"] +
                    " Medications = " + patientInfo["Medications"] +
                    " Surgeries = " + patientInfo["Surgeries"] +
);
```
======
```java
public boolean isPatient(long id)
```
###### Return value
&emsp;Returns true if a patient exists on the database, false otherwise.
###### Parameters
- `long id` - unique ID for a patient user

###### Example usage
```java
long patientID = mDB.getUserID("enter patient username here");

if (mDB.isPatient(patientID)) {
  // Do stuff here
}
```
======
<a name="Physician Methods"/>
## Physician table Methods

```java
public long addPhysician( String username, String password, 
                          String name, String gender, 
                          String phone, String email, String location, 
                          String[] specializations)
```
###### Description
Adds a physician into the database with given parameters. There is also another `addPhysician(...)` function that replaces the first two parameters of this function with the `physicianID`, but this is <b>not</b> recommended, as the ID is automatically generated for user creation in this method call.

**IMPORTANT**: You should store the return value of this function throughout the program because it returns the ID of the physician user.
###### Return value
&emsp;Returns the row ID of the newly inserted row in the physician table, or -1 if an error occurred.
###### Parameters
- `String username` - username for physician user
- `String password` - password for physician user
- `String name` - name of the physician
- `String gender` - <b><u>single</u></b> character string for physician's gender (M/F)
- `String phone` - phone number of physician (<b><u>must</u></b> be in format XXXYYYZZZZ)
- `String email` - email contact for physician
- `String location` - location of primary hospital for physician
- `String[] specializations` - specializations for the physician (eg. {"Cardiologist", "Exercise Specialist", ...})

###### Example usage
```java
long physicianID = mDB.addPhysician(  "Christian123",                                             // Username
                                      "Christian123",                                             // Password
                                      "Christian Morte",                                          // Name
                                      "M",                                                        // Gender
                                      "4082345678",                                               // Phone
                                      "morte@christian.com",                                      // Email
                                      "1234 One Road Irvine, CA 95131",                           // Location
                                      new String[]{"Cardiologist", "Exercise Specialist"});       // Specializations
);

System.out.println("The ID for the new physician user = " + physicianID);
```
======
```java
public HashMap<String,String> getPhysicianInfo(long id)
```
###### Return value
&emsp;Returns `HashMap<String,String>` object of information for a physician. If an error occurred (eg. ID doesn't exist), returns `null`.
###### Parameters
- `long id` - unique ID for a physician user

###### Example usage
```java
// Get physician ID
long physicianID = mDB.getUserID("enter username here");

// Get physician information
HashMap<String,String> physicianInfo = mDB.getPhysicianInfo(physicianID);

// Output physician information
System.out.println( " ID = " + physicianInfo["ID"] +
                    " Name = " + physicianInfo["Name"] +
                    " Gender = " + physicianInfo["Gender"] +
                    " Phone = " + physicianInfo["Phone"] +
                    " Email = " + physicianInfo["Email"] +
                    " Location = " + physicianInfo["Location"]
);

// Output physician specializations
System.out.println("Specializations = ");
for (String specialization : physicianInfo["Specializations"]) {
  System.out.print( specialization + ", " );
```
======
```java
public boolean isPhysician(long id)
```
###### Return value
&emsp;Returns true if a physician exists on the database, false otherwise.
###### Parameters
- `long id` - unique ID for a physician user

###### Example usage
```java
long physicianId = mDB.getUserID("enter physician username here");
if (mDB.isPhysician(physicianId)) {
  // Do stuff here
}
```
======
<a name="Specialization Methods"/>
## Specialization table Methods
```java
public long addPhysicianSpecialization(long id, String specialization)
```
###### Return value
&emsp;Returns the row ID of the new specialization added in the table, -1 if an error occurred.
###### Parameters
- `long id` - unique ID for a physician user
- `String specialization` - specialization to be added for physician

###### Example usage
```java
long physicianId = mDB.getUserID("enter physician username here");

long specializationRow = addPhysicianSpecialization(physicianID, "Cardiologist");
if (specializationRow == -1) {
  // specialization was not added, do something here
} else {
  // specialization added, do something here
}
```
======
```java
public long addPhysicianSpecialization(long id, String specialization)
```
###### Return value
&emsp;Returns the row ID of the new specialization added in the table, -1 if an error occurred.
###### Parameters
- `long id` - unique ID for a physician user
- `String specialization` - specialization to be added for physician

###### Example usage
```java
long physicianId = mDB.getUserID("enter physician username here");

long specializationRow = addPhysicianSpecialization(physicianID, "Cardiologist");
if (specializationRow == -1) {
  // specialization was not added, do something here
} else {
  // specialization added, do something here
}
```
======
```java
public ArrayList<String> getPhysicianSpecializations(long id)
```
###### Return value
&emsp;Returns an `ArrayList<String>` of specializations for a specific physician, or `null` if an error occurred.
###### Parameters
- `long id` - unique ID for a physician user

###### Example usage
```java
long physicianId = mDB.getUserID("enter physician username here");

ArrayList<String> specializations = getPhysicianSpecializations(physicianId);
for (String specialization : specializations) {
  // Do stuff here
}
```
======
```java
public ArrayList<HashMap<String,String>> getPhysiciansWithSpecialization(String specialization)
```
###### Return value
&emsp;Returns an `ArrayList<HashMap<String,String>>` of physicians given a specialization, or `null` if an error occurred. 
###### Parameters
- `String specialization` - specialization string for query

###### Example usage
```java
String specialization = "Cardiologist";
ArrayList<HashMap<String,String>> physiciansWithSpecialization = getPhysiciansWithSpecialization(specialization);

for ( HashMap<String,String> physicianInfo : physiciansWithSpecialization ) {
  System.out.println( " ID = " + physicianInfo["ID"] +
                      " Name = " + physicianInfo["Name"] +
                      " Gender = " + physicianInfo["Gender"] +
                      " Phone = " + physicianInfo["Phone"] +
                      " Email = " + physicianInfo["Email"] +
                      " Location = " + physicianInfo["Location"]
  );
}
```
======
<a name="Appointment Methods"/>
## Appointment table Methods
```java
public long addAppointment(long patientID, long physicianID, Date startDate, Date endDate)
public long addAppointment(   long patientID, long physicianID,
                              int startYear, int startMonth, int startDay, int startHour, int startMinute,
                              int endYear, int endMonth, int endDay, int endHour, int endMinute)
```
###### Description
Adds an appointment for a given patient and physician spanning from the start date-time and end date-time. If a patient and/or physician is unavailable for the time slot specified, the appointment is not added to the table. If there is an error concerning the patient ID, physician ID, or the time slot specified, the appointment is not added to the table.
###### Return value
&emsp;Returns the row ID of new appointment in the table. Otherwise, returns -1 if an error occurred (eg. ID doesn't exist, time slot booked for patient or physician).
###### Parameters
- `long patientID` - unique ID for a patient user
- `long physicianID` - unique ID for a physician user
- `Date startDate` - start date-time of appointment (refer to the `getDate(...)` method for creating dates)
- `Date endDate` - end date-time of appointment (refer to the `getDate(...)` method for creating dates)
- `int startYear, int startMonth, int startDay, int startHour, int startMinute` - <i>(for second method)</i> parameters for start date
- `int endYear, int endMonth, int endDay, int endHour, int endMinute` - <i>(for second method)</i> parameters for end date

###### Example usage
```java
// Get patient, physician IDs 
long patientId = mDB.getUserID("enter patient username here");
long physicianId = mDB.getUserID("enter physician username here");

// Appointment time slot:
//  Start = January 1, 2016 12:00 PM
//  End = January 1, 2016 12:30 PM
Date start = mDB.getDate(2016, 1, 1, 12, 0),
     end = mDB.getDate(2016, 1, 1, 12, 30);
     
if ( mDB.addAppointment(patientId, physicianId, start, end) > -1 ) {
  // appointment added, do something here
} else {
  // appointment not added, do something here
}

// Appointment time slot:
//  Start = February 1, 2016 6:15 PM
//  End = February 1, 2016 7:30 PM
if ( mDB.addAppointment(patientId, physicianId, 
                        2016, 2, 1, 18, 15, 
                        2016, 2, 1, 19, 30) > -1 ) {
  // appointment added, do something here
} else {
  // appointment not added, do something here
}
```
======
```java
public long addTimeBlockForPhysician(long physicianID, Date startDateTime, Date endDateTime)
```
###### Description
Adds a time slot block for a physician. This means that the specified time slot is not available for patients to choose from the physician's schedule. Internally, this function masks the time slot block by booking an appointment with the <i>null user</i> (patient ID = 0). If you want to find out if a time slot is blocked, check if an appointment is associated with patient ID = 0. (Refer to `getAllAppointmentsForPhysician(...) ` or `getUpcomingAppointmentsForPhysician(...)` for more info) If the time slots specified or the physician does not exist on the database, the time block is not added into the database.
###### Return value
&emsp;Returns the row ID of the new appointment (time block) added in the table, -1 if an error occurred (eg. time slot booked, ID does not exist).
###### Parameters
- `long physicianID` - unique ID for a physician user
- `Date startDateTime` - start date-time of time block (refer to the `getDate(...)` method for creating dates)
- `Date endDateTime` - end date-time of time block (refer to the `getDate(...)` method for creating dates)

###### Example usage
```java
long physicianId = mDB.getUserID("enter physician username here");

// Physician unavailable from:
//  Start = January 1, 2016 12:00 PM
//  End = January 1, 2016 12:30 PM
Date start = mDB.getDate(2016, 1, 1, 12, 0),
     end = mDB.getDate(2016, 1, 1, 12, 30);

if ( mDB.addTimeBlockForPhysician(long physicianID, Date startDateTime, Date endDateTime) == -1) {
  // time block not added, do something here
} else {
  // time block added, do something here
}
```
======
```java
public ArrayList<PatientAppointmentInfo> getAllAppointmentsForPatient(long patientID)
```
###### Description
Returns a list of <b>all</b> appointments for a patient. A `PatientAppointmentInfo` object has three getter methods for information for the appointment:
  - `physicianID()` - returns a `long` ID for associated physician in appointment
  - `startDateTime()` - returns a `Date` object for the start date-time of appointment
  - `endDateTime()` - returns a `Date` object for the end date-time of appointment
  
###### Return value
&emsp;Returns an `ArrayList<PatientAppointmentInfo>` of <b>all</b> the appointments the patient has/had. Returns an <b>empty</b> list if a patient has no appointments associated with him/her. Returns `null` if an error occurred (eg. patientID does not exist in database).

###### Parameters
- `long patientID` - unique ID for a patient user

###### Example usage
```java
long patientID = mDB.getUserID("enter patient username here");
ArrayList<PatientAppointmentInfo> appointments = mDB.getAllAppointmentsForPatient(patientID);

for (PatientAppointmentInfo appointment : appointments) {
  long physicianID = appointment.physicianID();
  Date startOfAppointment = appointment.startDateTime(),
       endOfAppointment = appointment.endDateTime();
  
  // Initialize a date formatter object
  android.text.format.DateFormat df = new android.text.format.DateFormat();
  
  // Format date (in this case, it will be like "2016-1-23 12:30:00" for the date January 23, 2016 12:30:00 PM) 
  String start = df.format("yyyy-MM-dd hh:mm:ss", startOfAppointment),
         end = df.format("yyyy-MM-dd hh:mm:ss", endOfAppointment);

  System.out.println( " Physician (ID) for appointment = " + physicianID +
                      " Start of Appointment = " + start +
                      " End of Appointment = " + end
  );
}
```
======
```java
public ArrayList<PatientAppointmentInfo> getUpcomingAppointmentsForPatient(long patientID)
```
###### Description
Returns a list of <b>upcoming</b> appointments for a patient. A `PatientAppointmentInfo` object has three getter methods for information for the appointment:
  - `physicianID()` - returns a `long` ID for associated physician in appointment
  - `startDateTime()` - returns a `Date` object for the start date-time of appointment
  - `endDateTime()` - returns a `Date` object for the end date-time of appointment
  
###### Return value
&emsp;Returns an `ArrayList<PatientAppointmentInfo>` of <b>upcoming</b> appointments for a patient. Returns an <b>empty</b> list if a patient has no upcoming appointments. Returns `null` if an error occurred (eg. patientID does not exist in database).

###### Parameters
- `long patientID` - unique ID for a patient user

###### Example usage
&emsp;Please see the `getAllAppointmentsForPatient(long patientID)` method for example usage.

======
```java
public boolean timeSlotAvailableForPatient(long patientID, Date startDateTime, Date endDateTime)
```
###### Return value
&emsp;Returns <i>true</i> if a given time slot is available for a patient, <i>false</i> otherwise.
###### Parameters
- `long patientID` - unique ID for a patient user
- `Date startDateTime` - Date object for start of time slot (refer to the `getDate(...)` method for creating dates)
- `Date endDateTime` - Date object for end of time slot (refer to the `getDate(...)` method for creating dates)

###### Example usage
```java
// Patient ID
long patientID = 1;

// Start = March 5, 2016 8:00 AM
// End = March 5, 2016 8:30 AM
Date start = mDB.getDate(2016, 3, 5, 8, 0),
     end = mDB.getDate(2016, 3, 5, 8, 30);

if ( mDB.timeSlotAvailableForPatient(patientID, start, end) ) {
  // Time slot available for patient, do something here
} else {
  // Time slot not available for patient, do something here
}
```
======
```java
public ArrayList<PhysicianAppointmentInfo> getAllAppointmentsForPhysician(long physicianID)
```
###### Description
Returns a list of <b>all</b> appointments for a physician. A `PhysicianAppointmentInfo` object has three getter methods for information for the appointment:
  - `patientID()` - returns a `long` ID for associated patient in appointment
  - `startDateTime()` - returns a `Date` object for the start date-time of appointment
  - `endDateTime()` - returns a `Date` object for the end date-time of appointment
  
###### Return value
&emsp;Returns an `ArrayList<PhysicianAppointmentInfo>` of <b>all</b> the appointments the physician has/had/will have. Returns an <b>empty</b> list if a physician has no appointments associated with him/her. Returns `null` if an error occurred (eg. physicianID does not exist in database).

###### Parameters
- `long physicianID` - unique ID for a physician user

###### Example usage
```java
long physicianID = mDB.getUserID("enter physician username here");
ArrayList<PhysicianAppointmentInfo> appointments = mDB.getAllAppointmentsForPhysician(physicianID);

for (PhysicianAppointmentInfo appointment : appointments) {
  long patientID = appointment.patientID();
  Date startOfAppointment = appointment.startDateTime(),
       endOfAppointment = appointment.endDateTime();
  
  // Initialize a date formatter object
  android.text.format.DateFormat df = new android.text.format.DateFormat();
  
  // Format date (in this case, it will be like "2016-1-23 12:30:00" for the date January 23, 2016 12:30:00 PM) 
  String start = df.format("yyyy-MM-dd hh:mm:ss", startOfAppointment),
         end = df.format("yyyy-MM-dd hh:mm:ss", endOfAppointment);

  System.out.println( " Patient (ID) for appointment = " + patientID +
                      " Start of Appointment = " + start +
                      " End of Appointment = " + end
  );
}
```
======
```java
public ArrayList<PhysicianAppointmentInfo> getUpcomingAppointmentsForPhysician(long physicianID)
```
###### Description
Returns a list of <b>upcoming</b> appointments for a physician. A `PhysicianAppointmentInfo` object has three getter methods for information for the appointment:
  - `patientID()` - returns a `long` ID for associated patient in appointment
  - `startDateTime()` - returns a `Date` object for the start date-time of appointment
  - `endDateTime()` - returns a `Date` object for the end date-time of appointment
  
###### Return value
&emsp;Returns an `ArrayList<PhysicianAppointmentInfo>` of <b>upcoming</b> appointments for a physician. Returns an <b>empty</b> list if a patient has no upcoming appointments. Returns `null` if an error occurred (eg. physicianID does not exist in database).

###### Parameters
- `long physicianID` - unique ID for a physician user

###### Example usage
&emsp;Please see the `getAllAppointmentsForPhysician(long physicianID)` method for example usage.

======
```java
public boolean timeSlotAvailableForPhysician(long physicianID, Date startDateTime, Date endDateTime)
```
###### Return value
&emsp;Returns <i>true</i> if a given time slot is available for a physician, <i>false</i> otherwise.
###### Parameters
- `long physicianID` - unique ID for a physician user
- `Date startDateTime` - Date object for start of time slot (refer to the `getDate(...)` method for creating dates)
- `Date endDateTime` - Date object for end of time slot (refer to the `getDate(...)` method for creating dates)

###### Example usage
```java
// Physician ID
long physicianID = 1;

// Start = March 5, 2016 8:00 AM
// End = March 5, 2016 8:30 AM
Date start = mDB.getDate(2016, 3, 5, 8, 0),
     end = mDB.getDate(2016, 3, 5, 8, 30);

if ( mDB.timeSlotAvailableForPhysician(physicianID, start, end) ) {
  // Time slot available for physician, do something here
} else {
  // Time slot not available for physician, do something here
}
```

<a name="General Helper Methods"/>
## General helper methods
```java
public Date getDate(int year, int month, int day, int hour, int minute)   // for specific date-times
public Date getDate(int year, int month, int day)                         // for general dates (such as birth dates)
```
###### Return value
&emsp;Returns a `Date` object based on the given parameters of the function. Returns `null` if an error occurred.
###### Parameters
- `int year` - year for date
- `int month` - month for date
- `int hour` - hour for date 
- `int minute` - minute for date

###### Example usage
```java
// d1 = November 28, 2016 1:00 AM
Date d1 = mDB.getDate(2016, 11, 28, 1, 0);

// d2 = November 28, 2016 (technically, on 12:00 AM)
Date d2 = mDB.getDate(2016, 11, 28);
```
