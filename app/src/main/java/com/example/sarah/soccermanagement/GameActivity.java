package com.example.sarah.soccermanagement;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.DragEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {

    Button startButton;
    Button stopButton;
    Button clearButton;
    ListView players;
    RelativeLayout field;
    ItemAdapter itemAdapter;

    Firebase ref;
    private static final String TAG = "MyActivity";
    MyDragEventListener myDragEventListener = new MyDragEventListener();
    ArrayList<Player> playerList = new ArrayList<>();
    ArrayList<Player> inPlayPlayers = new ArrayList<>();
    ArrayList<ImageView> images = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //ensures that the screen orientation is horizontal
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        players = (ListView) findViewById(R.id.fieldPlayersListView);
        field = (RelativeLayout) findViewById(R.id.field);
        startButton = (Button) findViewById(R.id.startButton);
        stopButton = (Button) findViewById(R.id.stopButton);
        clearButton = (Button) findViewById(R.id.clearButton);

        startButton.setOnClickListener(startTimerListener);
        stopButton.setOnClickListener(stopTimerListener);
        clearButton.setOnClickListener(clearTimersListener);

        Firebase.setAndroidContext(this);

        ref = new Firebase("https://soccer-management.firebaseio.com/Profiles");

        ref.addChildEventListener(new ChildEventListener() {

            @Override //this is called when screen is first generated and anytime a new child is added
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Player p = dataSnapshot.getValue(Player.class); //converts datasnapshot to a player
                if(p.isInPlay()) { //add player to in play list and generate image if player is in play
                    inPlayPlayers.add(p);
                    generateImageViewForPlayer(p, p.getxPos(), p.getyPos());
                }
                else { //add the player to the all players list if they are not in play (on the field)
                    playerList.add(p);
                }
                itemAdapter.notifyDataSetChanged(); //activate the list adapter to update the on screen list view
            }

            @Override //this is called when any child is changed and the datasnapshot is the child that was changed
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Player p = dataSnapshot.getValue(Player.class);
                if(p.isInPlay() && p.getxPos() != null && p.getyPos() != null) {  //if the player is in play and has a position
                    if(!isInList(inPlayPlayers, p)) { //if the player is not already on the field
                        inPlayPlayers.add(p);
                        for(Player player : playerList) { //find the correct player to remove
                            if(p.getFirstName().equals(player.getFirstName()) && p.getLastName().equals(player.getLastName())) {
                                playerList.remove(player);
                                break;
                            }
                        }
                        generateImageViewForPlayer(p, p.getxPos(), p.getyPos()); //generate image for player on field
                        itemAdapter.notifyDataSetChanged();
                    }
                    else {  //if the player is already on the field by moved to a new position

                        for(ImageView iv : images) { //find the player with the corresponding image
                            Bitmap image = ((BitmapDrawable) iv.getDrawable()).getBitmap(); //create bitmap from the drawable on the screen
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            image.compress(Bitmap.CompressFormat.PNG, 100, stream); //compress the bitmap
                            //image.recycle();
                            byte[] byteArray = stream.toByteArray(); //convert to a byte array
                            String imageFile = Base64.encodeToString(byteArray, Base64.DEFAULT); //code the byte array into a string

                            if(imageFile.equals(p.getImage())) { //changes the coordinates of the existing image
                                iv.setX(p.getxPos());
                                iv.setY(p.getyPos());
                            }
                        }
                    }
                }
                else if(!p.isInPlay() && p.getxPos() == null && p.getyPos() == null) { //if the player is moved from the field and to the list
                    for(Player player : inPlayPlayers) { //search for the correct player

                        if(p.getFirstName().equals(player.getFirstName()) && p.getLastName().equals(player.getLastName())) {

                            for(ImageView iv : images) { //search for the correct image

                                Bitmap image = ((BitmapDrawable) iv.getDrawable()).getBitmap();
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                image.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                //image.recycle();
                                byte[] byteArray = stream.toByteArray();
                                String imageFile = Base64.encodeToString(byteArray, Base64.DEFAULT);

                                if(imageFile.equals(player.getImage())) { //remove the image from the field
                                    field.removeView(iv);
                                    images.remove(iv);
                                    break;
                                }
                            }

                            inPlayPlayers.remove(player); //remove player form in play players
                            break;
                        }
                    }
                    playerList.add(p); //add player back to all players
                    itemAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //N/A - child cannot be removed within this activity
                //what if a coach removes a player while another player is on this activity???
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                //N/A - child cannot be moved within this activity
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        itemAdapter = new ItemAdapter(this, R.layout.player_profile_row_layout, playerList);

        players.setAdapter(itemAdapter);
        players.setOnItemLongClickListener(listItemClickListener);
        players.setOnDragListener(myDragEventListener);
        field.setOnDragListener(myDragEventListener);
    }

    private boolean isInList(ArrayList<Player> list, Player p) { //determines if a player is in the specified list
        boolean retVal = false;
        for(Player player : list) {
            if(player.getFirstName().equals(p.getFirstName()) && player.getLastName().equals(p.getLastName())) {
                retVal = true;
                break;
            }
        }
        return retVal;
    }

    public AdapterView.OnItemLongClickListener listItemClickListener = new AdapterView.OnItemLongClickListener(){ //listener for a long click on a list item

        @Override //extract the player's first and last name from the list view item to send in the clip data
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            LinearLayout linearLayoutParent = (LinearLayout) view;
            TextView tv = (TextView) linearLayoutParent.getChildAt(1);
            String text = tv.getText().toString();
            int index1 = text.indexOf(',');
            int index2 = text.indexOf('\n');
            ClipData.Item item = new ClipData.Item("UserName:" + text.substring(index1 + 2, index2) +
                    " " + text.substring(0, index1));

            String[] clipDescription = {ClipDescription.MIMETYPE_TEXT_PLAIN};
            ClipData dragData = new ClipData((CharSequence)view.getTag(), clipDescription,item);
            View.DragShadowBuilder myShadow = new MyDragShadowBuilder(view);
            view.startDrag(dragData, myShadow, null, 0);

            return true;
        }
    };

    public AdapterView.OnLongClickListener fieldItemClickListener = new AdapterView.OnLongClickListener() { //listener for a long click on a image in the field

        @Override //extract image string from the image on the field to send through the clip data
        public boolean onLongClick(View v) {
            ImageView iv = (ImageView) v;

            Bitmap image = ((BitmapDrawable) iv.getDrawable()).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 100, stream);
            //image.recycle();
            byte[] byteArray = stream.toByteArray();
            String imageFile = Base64.encodeToString(byteArray, Base64.DEFAULT);

            ClipData.Item item = new ClipData.Item(imageFile);
            String[] clipDescription = {ClipDescription.MIMETYPE_TEXT_PLAIN};
            ClipData dragData = new ClipData((CharSequence) v.getTag(), clipDescription, item);
            View.DragShadowBuilder myShadow = new MyDragShadowBuilder(v);
            v.startDrag(dragData, myShadow, null, 0);

            return true;
        }
    };

    private static class MyDragShadowBuilder extends View.DragShadowBuilder {
        private static Drawable shadow;

        public MyDragShadowBuilder(View v) {
            super(v);
            shadow = new ColorDrawable(Color.LTGRAY);
        }

        @Override
        public void onProvideShadowMetrics (Point size, Point touch){
            int width = getView().getWidth();
            int height = getView().getHeight();

            shadow.setBounds(0, 0, width, height);
            size.set(width, height);
            touch.set(width / 2, height / 2);
        }

        @Override
        public void onDrawShadow(Canvas canvas) {
            shadow.draw(canvas);
        }

    }

    protected class MyDragEventListener implements View.OnDragListener {

        @Override
        public boolean onDrag(View v, DragEvent event) {
            final int action = event.getAction();

            switch(action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    return true;
                case DragEvent.ACTION_DRAG_ENTERED:
                    return true;
                case DragEvent.ACTION_DRAG_LOCATION:
                    return true;
                case DragEvent.ACTION_DRAG_EXITED:
                    return true;
                case DragEvent.ACTION_DROP:
                    ClipData.Item item = event.getClipData().getItemAt(0);

                    if(item.getText().toString().contains("UserName:")) {
                        String droppedItem = item.getText().toString();
                        int index1 = droppedItem.indexOf(':');
                        int index = droppedItem.indexOf(" ");
                        String firstName = droppedItem.substring(index1 + 1, index);
                        String lastName = droppedItem.substring(index + 1);

                        Firebase player = ref.child("Player" + lastName + firstName).child("inPlay");
                        player.setValue(true);
                        player = ref.child("Player" + lastName + firstName).child("xPos");
                        player.setValue(event.getX());
                        player = ref.child("Player" + lastName + firstName).child("yPos");
                        player.setValue(event.getY());
                    }
                    else {
                        Player currentPlayer = null;
                        for(Player p : inPlayPlayers) {
                            String image = item.getText().toString();
                            if(p.getImage().equals(image)) {
                                currentPlayer = p;
                            }
                        }

                        if(currentPlayer != null) {
                            if(v == field) {
                                Firebase player = ref.child("Player" + currentPlayer.getLastName() + currentPlayer.getFirstName()).child("xPos");
                                player.setValue(event.getX());
                                player = ref.child("Player" + currentPlayer.getLastName() + currentPlayer.getFirstName()).child("yPos");
                                player.setValue(event.getY());
                            }
                            else if(v == players) {
                                Firebase player = ref.child("Player" + currentPlayer.getLastName() + currentPlayer.getFirstName()).child("inPlay");
                                player.setValue(false);
                                player = ref.child("Player" + currentPlayer.getLastName() + currentPlayer.getFirstName()).child("xPos");
                                player.setValue(null);
                                player = ref.child("Player" + currentPlayer.getLastName() + currentPlayer.getFirstName()).child("yPos");
                                player.setValue(null);
                            }
                        }
                    }

                    return true;
                case DragEvent.ACTION_DRAG_ENDED:
                    return true;
                default:
                    return false;
            }
        }
    }

    private void generateImageViewForPlayer(Player p, float x, float y) {

        ImageView newImageView = new ImageView(GameActivity.this);
        newImageView.setAdjustViewBounds(true);
        newImageView.setMaxHeight(90);
        newImageView.setMaxWidth(90);
        newImageView.setX(x);
        newImageView.setY(y);
        newImageView.setOnLongClickListener(fieldItemClickListener);
        byte[] bArray = Base64.decode(p.getImage(), Base64.DEFAULT);
        Bitmap bMap = BitmapFactory.decodeByteArray(bArray, 0, bArray.length);
        newImageView.setImageBitmap(bMap);
        p.setxPos(x);
        p.setyPos(y);

        Firebase player = ref.child("Player" + p.getLastName() + p.getFirstName()).child("xPos");
        player.setValue(x);
        player = ref.child("Player" + p.getLastName() + p.getFirstName()).child("yPos");
        player.setValue(y);

        field.addView(newImageView);
        images.add(newImageView);
    }

    public View.OnClickListener startTimerListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //go through all in-play players and start timers
            for (Player p : inPlayPlayers) {

                try {
                    Profiler.getInstance().start(p.getFirstName() + p.getLastName());
                }
                catch(ProfilerStartException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public View.OnClickListener stopTimerListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //go through all in-play players and stop timers
            for(Player p : inPlayPlayers) {

                try {
                    Profiler.getInstance().stop(p.getFirstName() + p.getLastName());
                    //Firebase r = ref.child("Player" + p.getLastName() + p.getFirstName()).child("timeOnField");
                    //r.setValue(Profiler.getInstance().getDuration(p.getFirstName() + p.getLastName()));
                }
                catch(ProfilerEndException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public View.OnClickListener clearTimersListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            for(Player p : playerList) {
                Profiler.getInstance().clear(p.getFirstName() + p.getLastName());
                //Firebase r = ref.child("Player" + p.getLastName() + p.getFirstName()).child("timeOnField");
                //r.setValue(0);
            }
            for(Player p : inPlayPlayers) {
                Profiler.getInstance().clear(p.getFirstName() + p.getLastName());
                //Firebase r = ref.child("Player" + p.getLastName() + p.getFirstName()).child("timeOnField");
                //r.setValue(0);
            }
            itemAdapter.notifyDataSetChanged();
        }
    };
}
