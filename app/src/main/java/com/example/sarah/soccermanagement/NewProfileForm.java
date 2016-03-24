package com.example.sarah.soccermanagement;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
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

    private Button submitButton;
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
    private Button uploadImage;
    private ImageView uploadedImage;
    private static final int RESULT_LOAD_IMAGE = 1;
    private Uri selectedImage;

    private Firebase ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_profile_form);

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

        ref = new Firebase("https://soccer-management.firebaseio.com/Profiles");



        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null && !bundle.isEmpty()) {
            if(bundle.containsKey("name")) {
                String n = bundle.getString("name");
                int index = n.indexOf(',');
                int endIndex = n.indexOf('\n');
                String fname = n.substring(index + 2, endIndex);
                String lname = n.substring(0, index);
                for(int i = 0; i < PlayerLists.allPlayers.size(); i++) {
                    if(PlayerLists.allPlayers.get(i).getFirstName().equals(fname) && PlayerLists.allPlayers.get(i).getLastName().equals(lname)) {
                        playerName.setText(PlayerLists.allPlayers.get(i).getFirstName());
                        playerLName.setText(PlayerLists.allPlayers.get(i).getLastName());
                        int pos = 0;
                        if(PlayerLists.allPlayers.get(i).getYear().equals("Sophomore")) { pos = 1; }
                        else if(PlayerLists.allPlayers.get(i).getYear().equals("Junior")) { pos = 2; }
                        else if(PlayerLists.allPlayers.get(i).getYear().equals("Senior")) { pos = 3; }
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

            Firebase newPlayer = ref.child("Player" + playerLName.getText().toString() + playerName.getText().toString());
            int feet = Integer.parseInt(playerHeightFeet.getText().toString());
            int inches = Integer.parseInt(playerHeightInches.getText().toString());
            float weight = Float.parseFloat(playerWeight.getText().toString());
            Player p = new Player(playerName.getText().toString(), playerLName.getText().toString(), playerYear.getSelectedItem().toString(), feet,
                    inches, weight, playerPosition.getText().toString(), playerHometown.getText().toString(), playerHighSchool.getText().toString(),
                    playerClub.getText().toString(), false);
            Bitmap image = ((BitmapDrawable) uploadedImage.getDrawable()).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 100, stream);
            image.recycle();
            byte[] byteArray = stream.toByteArray();
            String imageFile = Base64.encodeToString(byteArray, Base64.DEFAULT);
            p.setImage(imageFile);
            newPlayer.setValue(p);

            Intent intent = new Intent(NewProfileForm.this, ProfileActivity.class);

            startActivity(intent);
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
            selectedImage = data.getData();
            uploadedImage.setImageURI(selectedImage);
        }
    }
}
