package com.example.sarah.soccermanagement;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseListAdapter;

public class PracticeFieldActivity extends AppCompatActivity {

    ListView players;

    FirebaseListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_field);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        players = (ListView) findViewById(R.id.fieldPlayersListView);

        Firebase.setAndroidContext(this);

        Firebase ref = new Firebase("https://soccer-management.firebaseio.com/Profiles");

        adapter = new FirebaseListAdapter<Player>(this, Player.class, R.layout.player_profile_row_layout, ref) {

            @Override
            protected void populateView(View view, Player player, int i) {
                String name = player.getLastName() + ", " + player.getFirstName() + "\n" + player.getPosition();
                ((TextView)view.findViewById(R.id.playerNameTextView)).setText(name);
                byte[] bArray = Base64.decode(player.getImage(), Base64.DEFAULT);
                Bitmap bMap = BitmapFactory.decodeByteArray(bArray, 0, bArray.length);
                ((ImageView) view.findViewById(R.id.playerImageView)).setImageBitmap(bMap);
            }
        };

        players.setAdapter(adapter);
    }
}
