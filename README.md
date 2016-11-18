# QuickAppt

Android App which connects users to local physicians.

# API Reference
All of the methods shown below are located in the <a href="https://github.com/aclorta/QuickAppt/blob/Database/app/src/main/java/com/example/team6/quickappt/QADBHelper.java"><i>QADBHelper.java</i></a> file inside the <i>src</i> directory. For more information on how these were implemented, please see the code for further reference. There are various comments throughout each definition as well as some documentation above each defined method.

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

## Login table Methods
#### `public boolean userExists(String username)`
###### Return value
&emsp;Returns <i>true</i> if a user exists on the database, <i>false</i> otherwise.<br>
###### Parameters
- `String username` - username for a user
```java
if ( mDB.userExists("enter username here") ) {
  // Do stuff here
}
```

#### `public boolean userExists(long id)`
###### Return value
&emsp;Returns <i>true</i> if a user exists on the database, <i>false</i> otherwise.<br>
###### Parameters
- `long id` - unique ID for a user
```java
if ( mDB.userExists(1) ) {  // if user with ID=1 exists
  // Do stuff here
}
```

#### `public boolean loginValid(String username, String password)`
###### Return value
&emsp;Returns <i>true</i> if a given username-password combination matches inside the user database, false otherwise.
###### Parameters
- `String username` - username for a user
- `String password` - password for a user
```java
if ( mDB.loginValid("enter username here", "enter password here") ) {
  // Do stuff here
}
```

#### `public long getUserID(String username)`
###### Return value
&emsp;Returns the user ID for a specific user. If the user does not exist, then <b>0</b> is returned (which is a <i>null</i> user).
###### Parameters
- `String username` - username for a user
```java
String username = mDB.getUserID("enter username here");
System.out.println("Username = " + username);
```

## Patient table Methods

#### `public boolean isNullPatient(long patientID)`
###### Return value
&emsp;Returns true if a given patient ID is the null patient (has ID of 0).
###### Parameters
- `long patientID` - unique ID for a patient user
```java
mDB.isNullPatient(0);   // true
mDB.isNullPatient(1);   // false
mDB.isNullPatient(23235235);   // false
```


#### `public long addPatient(String username, String password, String name, String gender, Date birthDate, String maritalStatus, String phone, String email, String location, String occupation, String tobaccoSmoker, String regularExerciser, String allergies, String medications, String surgeries, String physicianName, String dentistName, String eyeDoctorName)`
###### Description
Adds a patient into the database with given parameters. There is also another `addPatient(...)` function that replaces the first two parameters of this function with the `patientID`, but this is <b>not</b> recommended, as the ID is automatically generated for user creation in this method call.
###### Return value
&emsp;Returns the row ID of the newly inserted row in the patient table, or -1 if an error occurred.
###### Parameters
- `String username` - username for patient user
- `String password` - password for patient user
- `String name` - name of the patient
- `String gender` - <b><u>single</u></b> character string for patient's gender (M/F)
- `Date birthDate` - Date object for the birth date of patient
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
```java
mDB.addPatient( "Alice123",                                 // Username
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
```

#### `public HashMap<String,String> getPatientInfo(long id)`
###### Return value
&emsp;Returns `HashMap<String,String>` object of information for a patient. If an error occurred (eg. ID doesn't exist), returns `null`.
###### Parameters
- `long id` - unique ID for a patient user
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

#### `public boolean isPatient(long id)`
###### Return value
&emsp;Returns true if a patient exists on the database, false otherwise.
###### Parameters
- `long id` - unique ID for a patient user
```java
if (mDB.isPatient(1))
  // Do stuff here
```

## Physician table Methods

#### `public long addPhysician(String username, String password, String name, String gender, String phone, String email, String location, String[] specializations)`
###### Description
Adds a physician into the database with given parameters. There is also another `addPhysician(...)` function that replaces the first two parameters of this function with the `physicianID`, but this is <b>not</b> recommended, as the ID is automatically generated for user creation in this method call.
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
```java
mDB.addPhysician( "Christian123",                                             // Username
                  "Christian123",                                             // Password
                  "Christian Morte",                                          // Name
                  "M",                                                        // Gender
                  "4082345678",                                               // Phone
                  "morte@christian.com",                                      // Email
                  "1234 One Road Irvine, CA 95131",                           // Location
                  new String[]{"Cardiologist", "Exercise Specialist"});       // Specializations
```

#### `public HashMap<String,String> getPhysicianInfo(long id)`
###### Return value
&emsp;Returns `HashMap<String,String>` object of information for a physician. If an error occurred (eg. ID doesn't exist), returns `null`.
###### Parameters
- `long id` - unique ID for a physician user
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

#### `public boolean isPhysician(long id)`
###### Return value
&emsp;Returns true if a physician exists on the database, false otherwise.
###### Parameters
- `long id` - unique ID for a physician user
```java
if (mDB.isPhysician(1))
  // Do stuff here
```
