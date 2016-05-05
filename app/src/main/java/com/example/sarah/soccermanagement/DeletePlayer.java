package com.example.sarah.soccermanagement;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseListAdapter;

public class DeletePlayer extends AppCompatActivity {

    ListView players;
    private static final String TAG = "MyActivity";
    FirebaseListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_player);

        Firebase.setAndroidContext(this);

        final Firebase ref = new Firebase(getString(R.string.FirebaseURL));

        players = (ListView) findViewById(R.id.playersListView);
        players.setOnItemClickListener(new AdapterView.OnItemClickListener() {  //listens for a click action on one of the list items
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final View v = view;
                //ask user to confirm the delete
                AlertDialog.Builder builder = new AlertDialog.Builder(DeletePlayer.this);
                builder.setTitle("Confirm");
                builder.setMessage("Are you sure you would like to delete " + ((TextView) view.findViewById(R.id.playerNameTextView)).getText().toString() + "?").setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //delete from firebase
                        String name = ((TextView) v.findViewById(R.id.playerNameTextView)).getText().toString();
                        int index = name.indexOf(' ');
                        int index2 = name.indexOf('\n');
                        String firstName = name.substring(0, index - 1);
                        String lastName = name.substring(index + 1, index2);
                        Firebase r = ref.child("Player" + firstName + lastName);
                        r.removeValue();
                        //go back to profiles
                        Intent intent = new Intent(DeletePlayer.this, ProfileActivity.class);
                        startActivity(intent);
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //do nothing!!!
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();

            }
        });

        //list adapter that populates the list view directly from firebase
        adapter = new FirebaseListAdapter<Player>(this, Player.class, R.layout.player_profile_row_layout, ref) {

            @Override
            protected void populateView(View view, Player player, int i) {
                String name = player.getLastName() + ", " + player.getFirstName() + "\n" + player.getPosition();
                ((TextView)view.findViewById(R.id.playerNameTextView)).setText(name);
                if(player.getImage() != null) {
                    byte[] bArray = Base64.decode(player.getImage(), Base64.DEFAULT);
                    Bitmap bMap = BitmapFactory.decodeByteArray(bArray, 0, bArray.length);
                    ((ImageView) view.findViewById(R.id.playerImageView)).setImageBitmap(bMap);
                }
                else {
                    ((ImageView) view.findViewById(R.id.playerImageView)).setImageResource(R.drawable.shieldc_small);
                }
            }
        };

        players.setAdapter(adapter);
    }
}
