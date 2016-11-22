package com.example.team6.quickappt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    ListView listView;
    Button btnClick;
    long patientID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        QADBHelper mDB = new QADBHelper(this);
        mDB.open();

        patientID = getIntent().getLongExtra("userID", 1);
        HashMap<String,String> patientInfo = mDB.getPatientInfo(patientID);

        ArrayList<String> items = new ArrayList<String>();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE, MMM d, yyyy h:mm a");

        for (QADBHelper.PatientAppointmentInfo appointmentInfo : mDB.getUpcomingAppointmentsForPatient(patientID) ) {
            HashMap<String,String> physicianInfo = mDB.getPhysicianInfo(appointmentInfo.physicianID());
            String item = dateFormatter.format(appointmentInfo.startDateTime()) + "\n" +
                    "Appointment with " + doctorName(physicianInfo.get("Name")) + "\n" +
                    "@ " + physicianInfo.get("Location");
            items.add(item);
        }

        if (items.isEmpty()) {
            items.add("No past appointments to show!");
        }

        listView = (ListView)findViewById(R.id.history_lst);
        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items));

        btnClick = (Button) findViewById(R.id.home_btn);
        /** Home Button sends user to HomeScreen activity */

        btnClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HistoryActivity.this, HomeScreen.class);
                intent.putExtra("patientID", patientID);
                startActivity(intent);
            }
        });
    }

    private String doctorName(String name) {
        String[] parts = name.split(" ");
        return "Dr. " + parts[1];
    }
}
