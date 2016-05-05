package com.example.sarah.soccermanagement;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;

import com.firebase.client.Firebase;

import java.io.ByteArrayOutputStream;

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

        //Loads 70 test players into firebase
//        Firebase.setAndroidContext(this);

//        Firebase ref = new Firebase(getString(R.string.FirebaseURL));
//
//        for(int i = 0; i < 70; i++) {
//            Firebase newP = ref.child("Player" + String.valueOf(i) + String.valueOf(i));
//            Player p= new Player(String.valueOf(i), String.valueOf(i), "Freshmen", 5, 5, 100f, "GK", "hometown", "highschool", "club", false);
//            BitmapDrawable drawable = ((BitmapDrawable) ResourcesCompat.getDrawable(getResources(), R.drawable.shieldc_small, null));
//            String imageFile = "";
//            if(drawable != null) {
//                Bitmap image = drawable.getBitmap();
//                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                image.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                byte[] byteArray = stream.toByteArray();
//                imageFile = Base64.encodeToString(byteArray, Base64.DEFAULT);
//            }
//            p.setImage(imageFile);
//            if(i >= 0 && i < 22) {
//                p.setGroupNum(1);
//            }
//            else if(i >= 22 && i < 44) {
//                p.setGroupNum(2);
//            }
//            else if(i >= 44 && i < 66) {
//                p.setGroupNum(3);
//            }
//            else
//            {
//                p.setGroupNum(4);
//            }
//            p.setTimerOn(false);
//            newP.setValue(p);
//        }

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
