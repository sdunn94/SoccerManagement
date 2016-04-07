package com.example.sarah.soccermanagement;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button profileButton;
    Button practiceButton;
    Button hyperlinksButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        profileButton = (Button) findViewById(R.id.profileButton);
        practiceButton = (Button) findViewById(R.id.practiceButton);
        hyperlinksButton = (Button) findViewById(R.id.hyperlinksButton);
        practiceButton.setOnClickListener(goToPracticeField);
        profileButton.setOnClickListener(goToProfileActivity);
        hyperlinksButton.setOnClickListener(goToHyperlinks);

    }

    public View.OnClickListener goToProfileActivity = new View.OnClickListener() {
        @Override
        public void onClick(View v)
        {
            Intent profile = new Intent(MainActivity.this, ProfileActivity.class);

            startActivity(profile);
        }
    };

    public View.OnClickListener goToPracticeField = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, PracticeSetUpActivity.class);

            startActivity(intent);
        }
    };

    public View.OnClickListener goToHyperlinks = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, HyperlinksActivity.class);

            startActivity(intent);
        }
    };
}
