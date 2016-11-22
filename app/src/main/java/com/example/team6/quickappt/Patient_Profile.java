package com.example.team6.quickappt;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;

public class Patient_Profile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient__profile);

        QADBHelper mDB = new QADBHelper(this);
        mDB.open();

        Spinner spindown = (Spinner) findViewById(R.id.Months);
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
        }
        };
        ArrayAdapter<String> adaptmonths = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,Montharray);
        spindown.setAdapter(adaptmonths);
    }
}
