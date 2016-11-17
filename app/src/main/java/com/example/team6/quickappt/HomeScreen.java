package com.example.team6.quickappt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeScreen extends AppCompatActivity {
    Button btnClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        btnClick = (Button) findViewById(R.id.edit_btn);
        /** Edit Button sends user to Profile activity; need to update name */

        btnClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeScreen.this, Patient_Profile.class));
            }
        });

        btnClick = (Button) findViewById(R.id.search_btn);
        /** Search Button sends user to Search activity; need to update name */

        btnClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeScreen.this, SearchActivity.class));
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

//        btnClick = (Button) findViewById(R.id.history_btn);
//        /** History Button sends user to History activity; need to update name */
//
//        btnClick.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(HomeScreen.this, historyActivity.class));
//            }
//        });
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
