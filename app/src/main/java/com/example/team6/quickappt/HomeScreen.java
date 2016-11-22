package com.example.team6.quickappt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ListView;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.ArrayList;
import java.text.SimpleDateFormat;

public class HomeScreen extends AppCompatActivity {
    long patientID;
    Button btnClick;

    TextView nameLabel, locationLabel;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        QADBHelper mDB = new QADBHelper(this);
        mDB.open();

        patientID = getIntent().getLongExtra("patientID", 1);
        HashMap<String,String> patientInfo = mDB.getPatientInfo(patientID);
        String name = patientInfo.get("Name"),
                location = patientInfo.get("Location");

        nameLabel = (TextView)findViewById(R.id.name_input);
        nameLabel.setText("Patient: " + name);

        locationLabel = (TextView)findViewById(R.id.locate_lbl);
        locationLabel.setText(location);

        listView = (ListView)findViewById(R.id.reminder_lst);

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
            items.add("No upcoming appointments to show!");
        }

        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items));

        btnClick = (Button) findViewById(R.id.edit_btn);
        /** Edit Button sends user to Profile activity */

        btnClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreen.this, Patient_Profile.class);
                intent.putExtra("patientID", patientID);
                startActivity(intent);
            }
        });

        btnClick = (Button) findViewById(R.id.search_btn);
        /** Search Button sends user to Search activity */

        btnClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreen.this, SearchActivity.class);
                intent.putExtra("patientID", patientID);
                startActivity(intent);
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
                Intent intent = new Intent(HomeScreen.this, HistoryActivity.class);
                intent.putExtra("patientID", patientID);
                startActivity(intent);
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

    private String doctorName(String name) {
        String[] parts = name.split(" ");
        return "Dr. " + parts[1];
    }
}
