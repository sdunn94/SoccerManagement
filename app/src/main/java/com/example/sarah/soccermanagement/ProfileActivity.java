package com.example.sarah.soccermanagement;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseRecyclerAdapter;

public class ProfileActivity extends AppCompatActivity {

    Button newProfileButton;
    Button backToMenuButton;
    RecyclerView players;

    private Firebase ref;
    FirebaseRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_activity);

        newProfileButton = (Button) findViewById(R.id.newProfileButton);
        backToMenuButton = (Button) findViewById(R.id.backToMenuButton);
        players = (RecyclerView) findViewById(R.id.playersRecyclerView);
        players.setHasFixedSize(true);
        players.setLayoutManager(new LinearLayoutManager(this));

        newProfileButton.setOnClickListener(startNewProfile);
        backToMenuButton.setOnClickListener(backToMenu);

        Firebase.setAndroidContext(this);

        ref = new Firebase("https://soccer-management.firebaseio.com/Profiles");

        adapter = new FirebaseRecyclerAdapter<Player, MyViewHolder>(Player.class, R.layout.player_profile_row_layout, MyViewHolder.class, ref) {

            @Override
            protected void populateViewHolder(MyViewHolder messageViewHolder, Player player, int i) {
                messageViewHolder.tv.setText(player.getFirstName());
                messageViewHolder.iv.setImageResource(player.getPicResourceId());
            }
        };

        players.setAdapter(adapter);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv;
        ImageView iv;
        public MyViewHolder(View v)
        {
            super(v);
            tv = (TextView) v.findViewById(R.id.playerNameTextView);
            iv = (ImageView) v.findViewById(R.id.playerImageView);
        }
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
