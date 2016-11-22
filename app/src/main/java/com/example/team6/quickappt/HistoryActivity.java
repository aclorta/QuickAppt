package com.example.team6.quickappt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.HashMap;

public class HistoryActivity extends AppCompatActivity {

    Button btnClick;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        QADBHelper mDB = new QADBHelper(this);
        mDB.open();

        long patientID = 1;
        HashMap<String,String> patientInfo = mDB.getPatientInfo(patientID);
        String name = patientInfo.get("Name");


        btnClick = (Button) findViewById(R.id.home_btn);
        /** Home Button sends user to HomeScreen activity */

        btnClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HistoryActivity.this, HomeScreen.class));
            }
        });
    }
}
