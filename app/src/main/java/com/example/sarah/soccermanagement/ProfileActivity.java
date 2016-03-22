package com.example.sarah.soccermanagement;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseListAdapter;


public class ProfileActivity extends AppCompatActivity {

    Button newProfileButton;
    Button backToMenuButton;
    ListView players;
    //private static final String TAG = "MyActivity";
    FirebaseListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_activity);

        newProfileButton = (Button) findViewById(R.id.newProfileButton);
        backToMenuButton = (Button) findViewById(R.id.backToMenuButton);
        players = (ListView) findViewById(R.id.playersRecyclerView);
        players.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Log.d(TAG, "onClick " + ((TextView) view.findViewById(R.id.playerNameTextView)).getText().toString());
                Intent intent = new Intent(ProfileActivity.this, NewProfileForm.class);
                Bundle bundle = new Bundle();
                bundle.putString("name", ((TextView) view.findViewById(R.id.playerNameTextView)).getText().toString());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        newProfileButton.setOnClickListener(startNewProfile);
        backToMenuButton.setOnClickListener(backToMenu);

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
                PlayerLists.allPlayers.add(player);
            }
        };

        players.setAdapter(adapter);
    }

    public View.OnClickListener startNewProfile = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(ProfileActivity.this, NewProfileForm.class);

            startActivity(intent);
        }
    };

    public View.OnClickListener backToMenu = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);

            startActivity(intent);
        }
    };
}
