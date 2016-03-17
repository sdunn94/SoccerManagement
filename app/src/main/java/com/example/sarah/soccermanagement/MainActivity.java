package com.example.sarah.soccermanagement;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button profileButton;
//    Button practiceButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        profileButton = (Button) findViewById(R.id.profileButton);
//        practiceButton = (Button) findViewById(R.id.practiceButton);
//        practiceButton.setOnClickListener(goToPracticeField);
        profileButton.setOnClickListener(goToProfileActivity);


    }

    public View.OnClickListener goToProfileActivity = new View.OnClickListener() {
        @Override
        public void onClick(View v)
        {
            Intent profile = new Intent(MainActivity.this, ProfileActivity.class);

            startActivity(profile);
        }
    };

//    public View.OnClickListener goToPracticeField = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//
//            Intent intent = new Intent(MainActivity.this, PracticeFieldActivity.class);
//
//            startActivity(intent);
//        }
//    };
}
