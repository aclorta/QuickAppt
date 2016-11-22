package com.example.team6.quickappt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.HashMap;

public class HomeScreen extends AppCompatActivity {
    Button btnClick;
    TextView txtClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
//
        QADBHelper mDB = new QADBHelper(this);
        mDB.open();

        long patientID = 1;
        HashMap<String,String> patientInfo = mDB.getPatientInfo(patientID);
        String name = patientInfo.get("Name");
        System.out.println("Name = " + name);
        //String gender = patientInfo.get("Location");

        txtClick = (TextView)findViewById(R.id.name_input);
        txtClick.setText(name);

        btnClick = (Button) findViewById(R.id.edit_btn);
        /** Edit Button sends user to Profile activity */

        btnClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeScreen.this, Patient_Profile.class));
            }
        });

        btnClick = (Button) findViewById(R.id.search_btn);
        /** Search Button sends user to Search activity */

        btnClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeScreen.this, SearchAct.class));
            }
        });

        btnClick = (Button) findViewById(R.id.logout_btn);
        /** Log Out Button sends user to LoginActiivity */

        btnClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeScreen.this, LoginActivity.class));
            }
        });

        btnClick = (Button) findViewById(R.id.history_btn);
        /** History Button sends user to History activity */

        btnClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeScreen.this, HistoryActivity.class));
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
