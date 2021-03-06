package com.example.sarah.soccermanagement;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.firebase.client.Firebase;

import java.io.ByteArrayOutputStream;

public class NewProfileForm extends AppCompatActivity {


    private EditText playerName;
    private EditText playerLName;
    private EditText playerHeightFeet;
    private EditText playerHeightInches;
    private EditText playerWeight;
    private EditText playerPosition;
    private EditText playerHometown;
    private EditText playerHighSchool;
    private EditText playerClub;
    private Spinner playerYear;
    private Player player;

    private ImageView uploadedImage;
    private static final int RESULT_LOAD_IMAGE = 1;


    private Firebase ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_profile_form);

        Button submitButton;
        Button uploadImage;

        submitButton = (Button) findViewById(R.id.profileSubmitButton);
        playerName = (EditText) findViewById(R.id.profileNameEditText);
        playerLName = (EditText) findViewById(R.id.profileLastNameEditText);
        playerYear = (Spinner) findViewById(R.id.yearSpinner);
        playerHeightFeet = (EditText) findViewById(R.id.heightFeetEditText);
        playerHeightInches = (EditText) findViewById(R.id.heightInchesEditText);
        playerWeight = (EditText) findViewById(R.id.weightEditText);
        playerPosition = (EditText) findViewById(R.id.positionEditText);
        playerHometown = (EditText) findViewById(R.id.homeTownEditText);
        playerHighSchool = (EditText) findViewById(R.id.highSchoolEditText);
        playerClub = (EditText) findViewById(R.id.clubEditText);
        uploadImage = (Button) findViewById(R.id.uploadImageButton);
        uploadedImage = (ImageView) findViewById(R.id.uploadedImagePreview);

        submitButton.setOnClickListener(submitNewProfile);
        uploadImage.setOnClickListener(uploadImageFromDevice);

        Firebase.setAndroidContext(this);

        ref = new Firebase(getString(R.string.FirebaseURL));

        //unpackage the bundle sent from the Profile activity and if it contains data, fill the fields on the screen
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null && !bundle.isEmpty()) {
            if(bundle.containsKey("name")) {

                //extract the players name from the data
                String n = bundle.getString("name");
                int index = n.indexOf(',');
                int endIndex = n.indexOf('\n');
                String fname = n.substring(index + 2, endIndex);
                String lname = n.substring(0, index);
                for(int i = 0; i < PlayerLists.allPlayers.size(); i++) { //find the correct player and use his data to populate the fields

                    if(PlayerLists.allPlayers.get(i).getFirstName().equals(fname) && PlayerLists.allPlayers.get(i).getLastName().equals(lname)) {
                        player = PlayerLists.allPlayers.get(i);
                        playerName.setText(PlayerLists.allPlayers.get(i).getFirstName());
                        playerLName.setText(PlayerLists.allPlayers.get(i).getLastName());
                        int pos = 0;
                        if(PlayerLists.allPlayers.get(i).getYear().equals("Sophomore")) { pos = 1; }
                        else if(PlayerLists.allPlayers.get(i).getYear().equals("Junior")) { pos = 2; }
                        else if(PlayerLists.allPlayers.get(i).getYear().equals("Senior")) { pos = 3; }
                        else if(PlayerLists.allPlayers.get(i).getYear().equals("Other")) { pos = 4; }
                        playerYear.setSelection(pos);
                        playerHeightFeet.setText(String.format("%d", PlayerLists.allPlayers.get(i).getHeightFeet()));
                        playerHeightInches.setText(String.format("%d", PlayerLists.allPlayers.get(i).getHeightInches()));
                        playerWeight.setText(String.format("%f", PlayerLists.allPlayers.get(i).getWeight()));
                        playerPosition.setText(PlayerLists.allPlayers.get(i).getPosition());
                        playerHometown.setText(PlayerLists.allPlayers.get(i).getHometown());
                        playerHighSchool.setText(PlayerLists.allPlayers.get(i).getHighSchool());
                        playerClub.setText(PlayerLists.allPlayers.get(i).getClub());
                        byte[] bArray = Base64.decode(PlayerLists.allPlayers.get(i).getImage(), Base64.DEFAULT);
                        Bitmap bMap = BitmapFactory.decodeByteArray(bArray, 0, bArray.length);
                        uploadedImage.setImageBitmap(bMap);
                        PlayerLists.allPlayers.remove(i);
                        break;
                    }
                }
            }
        }
    }

    public View.OnClickListener submitNewProfile = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            //if no first name or last name is given don't let them submit the new profile
            if(playerLName.getText().toString().isEmpty() || playerName.getText().toString().isEmpty()){
                //open message box
                AlertDialog.Builder builder = new AlertDialog.Builder(NewProfileForm.this);
                builder.setMessage("You must have a first and last name!").setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
            else { //create or overwrite the new player with all the info gathered from the edit text fields
                Firebase newPlayer = ref.child("Player" + playerLName.getText().toString() + playerName.getText().toString());
                int feet;
                if(playerHeightFeet.getText().toString().isEmpty()) { feet = 0; }
                else { feet = Integer.parseInt(playerHeightFeet.getText().toString()); }

                int inches;
                if(playerHeightInches.getText().toString().isEmpty()) { inches = 0; }
                else { inches = Integer.parseInt(playerHeightInches.getText().toString()); }

                float weight;
                if(playerWeight.getText().toString().isEmpty()) { weight = 0f; }
                else { weight = Float.parseFloat(playerWeight.getText().toString()); }

                Player p = new Player(playerName.getText().toString(), playerLName.getText().toString(), playerYear.getSelectedItem().toString(), feet,
                        inches, weight, playerPosition.getText().toString(), playerHometown.getText().toString(), playerHighSchool.getText().toString(),
                        playerClub.getText().toString(), false);

                String imageFile = "";
                if(uploadedImage.getDrawable() == null ) {
                    BitmapDrawable drawable = ((BitmapDrawable) ResourcesCompat.getDrawable(getResources(), R.drawable.shieldc_small, null));
                    if(drawable != null) {
                        Bitmap image = drawable.getBitmap();
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        image.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        image.recycle();
                        byte[] byteArray = stream.toByteArray();
                        imageFile = Base64.encodeToString(byteArray, Base64.DEFAULT);
                    }
                }
                else {
                    Bitmap image = ((BitmapDrawable) uploadedImage.getDrawable()).getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    image.recycle();
                    byte[] byteArray = stream.toByteArray();
                    imageFile = Base64.encodeToString(byteArray, Base64.DEFAULT);
                }
                p.setImage(imageFile);
                if(player != null) {
                    p.setGroupNum(player.getGroupNum());
                }
                newPlayer.setValue(p);
                //go back to profiles
                Intent intent = new Intent(NewProfileForm.this, ProfileActivity.class);

                startActivity(intent);
            }
        }
    };

    public View.OnClickListener uploadImageFromDevice = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(gallery, RESULT_LOAD_IMAGE);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            uploadedImage.setImageURI(selectedImage);
        }
    }
}
