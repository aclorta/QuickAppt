package com.example.team6.quickappt;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class PatientCalendar extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_calendar);

        QADBHelper mDB = new QADBHelper(this);
        mDB.open();

    }
}
