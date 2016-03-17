package com.example.sarah.soccermanagement;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.firebase.client.Firebase;

public class NewProfileForm extends AppCompatActivity {

    Button submitButton;
    EditText playerName;
    EditText playerHeightFeet;
    EditText playerHeightInches;
    EditText playerWeight;
    EditText playerHometown;
    EditText playerHighSchool;
    EditText playerClub;
    Spinner playerYear;

    private Firebase ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_profile_form);

        submitButton = (Button) findViewById(R.id.profileSubmitButton);
        playerName = (EditText) findViewById(R.id.profileNameEditText);
        playerYear = (Spinner) findViewById(R.id.yearSpinner);
        playerHeightFeet = (EditText) findViewById(R.id.heightFeetEditText);
        playerHeightInches = (EditText) findViewById(R.id.heightInchesEditText);
        playerWeight = (EditText) findViewById(R.id.weightEditText);
        playerHometown = (EditText) findViewById(R.id.homeTownEditText);
        playerHighSchool = (EditText) findViewById(R.id.highSchoolEditText);
        playerClub = (EditText) findViewById(R.id.clubEditText);

        submitButton.setOnClickListener(submitNewProfile);

        Firebase.setAndroidContext(this);

        ref = new Firebase("https://soccer-management.firebaseio.com/Profiles");
    }

    public View.OnClickListener submitNewProfile = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Firebase newPlayer = ref.child("Player" + playerName.getText().toString());
            int feet = Integer.parseInt(playerHeightFeet.getText().toString());
            int inches = Integer.parseInt(playerHeightInches.getText().toString());
            float weight = Float.parseFloat(playerWeight.getText().toString());
            Player p = new Player(playerName.getText().toString(), playerYear.getSelectedItem().toString(), feet,
                    inches, weight, playerHometown.getText().toString(), playerHighSchool.getText().toString(),
                    playerClub.getText().toString());

            //ref.push().setValue(p);
            p.setPicResourceId(getResources().getIdentifier("ic_google", "drawable", getPackageName()));
            newPlayer.setValue(p);
            //PlayerLists.allPlayers.add(p.getFirstName());

            Intent intent = new Intent(NewProfileForm.this, ProfileActivity.class);

            startActivity(intent);
        }
    };
}
