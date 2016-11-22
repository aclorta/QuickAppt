package com.example.team6.quickappt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;

public class Patient_Profile extends AppCompatActivity {

    Button btnClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient__profile);

        btnClick = (Button) findViewById(R.id.button_patient_profile_submit);
        /** Edit Button sends user to Profile activity */

        btnClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Patient_Profile.this, HomeScreen.class);
                startActivity(intent);
            }
        });

        QADBHelper mDB = new QADBHelper(this);
        mDB.open();

        Spinner spindown = (Spinner) findViewById(R.id.Months);
        Spinner spindownDays = (Spinner) findViewById(R.id.spinner3_days);
        Spinner spindownYears = (Spinner) findViewById(R.id.spinner4_years);

        ArrayList<String> Montharray= new ArrayList<String>(){{
            add("January");
            add("February");
            add("March");
            add("April");
            add("May");
            add("June");
            add("July");
            add("August");
            add("September");
            add("October");
            add("November");
            add("December");
        }};

        ArrayList<String> DayArray = new ArrayList<String>();
        ArrayList<String> YearArray = new ArrayList<String>();

        for (int i = 1; i < 32; i++) {
            DayArray.add(Integer.toString(i));
        }

        for (int i = 1900; i < 2016; i++) {
            YearArray.add(Integer.toString(i));
        }

        ArrayAdapter<String> adaptmonths = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,Montharray);
        spindown.setAdapter(adaptmonths);

        ArrayAdapter<String> adaptdays = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,DayArray);
        spindownDays.setAdapter(adaptdays);

        ArrayAdapter<String> adaptyears = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,YearArray);
        spindownYears.setAdapter(adaptyears);
    }
}
