package com.example.team6.quickappt;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;

public class Physician_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physician_activity);

        QADBHelper mDB = new QADBHelper(this);
        mDB.open();

        Spinner spindown = (Spinner) findViewById(R.id.Checkin);
        ArrayList<String> TimeArray= new ArrayList<String>(){{
            add("12");
            add("1");
            add("2");
            add("3");
            add("4");
            add("5");
            add("6");
            add("7");
            add("8");
            add("9");
            add("10");
            add("11");

        }

        };
        ArrayAdapter<String> adaptTime = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,TimeArray);
        spindown.setAdapter(adaptTime);

        Spinner spins = (Spinner) findViewById(R.id.Checkout);
        ArrayList<String> timeArray= new ArrayList<String>(){{
            add("12");
            add("1");
            add("2");
            add("3");
            add("4");
            add("5");
            add("6");
            add("7");
            add("8");
            add("9");
            add("10");
            add("11");

        }

        };
        ArrayAdapter<String> adaptime = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,timeArray);
        spins.setAdapter(adaptime);
        Spinner clicker = (Spinner) findViewById(R.id.AM);
        ArrayList<String> amOr = new ArrayList<String>(){{
            add("AM");
            add("PM");
        }};
        ArrayAdapter<String> adaptam = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,amOr);
        clicker.setAdapter(adaptam);
        Spinner second = (Spinner) findViewById(R.id.PM);
        ArrayList<String> pmOr = new ArrayList<String>(){{
            add("AM");
            add("PM");
        }};
        ArrayAdapter<String> adaptpm = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,pmOr);
        second.setAdapter(adaptpm);
    }
}
