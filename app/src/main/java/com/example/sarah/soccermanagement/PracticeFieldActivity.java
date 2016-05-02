package com.example.sarah.soccermanagement;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PracticeFieldActivity extends AppCompatActivity {

    Button startButton;
    Button stopButton;
    Button clearButton;
    Button clearAllButton;
    Button clearFieldButton;
    RadioGroup radioGroup;

    ListView players;
    RelativeLayout field;
    ItemAdapter itemAdapter;
    ItemAdapter itemAdapter2;
    ItemAdapter itemAdapter3;
    ItemAdapter itemAdapter4;

    Firebase ref;
    private static final String TAG = "ArrayLength: ";
    MyDragEventListener myDragEventListener = new MyDragEventListener();
    ArrayList<Player> group1 = new ArrayList<>();
    ArrayList<Player> group2 = new ArrayList<>();
    ArrayList<Player> group3 = new ArrayList<>();
    ArrayList<Player> group4 = new ArrayList<>();
    ArrayList<Player> inPlayPlayers = new ArrayList<>();
    HashMap<String, ImageView> imageViewMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_field);

        //ensures that the screen orientation is horizontal
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        players = (ListView) findViewById(R.id.fieldPlayersListView);
        field = (RelativeLayout) findViewById(R.id.field);
        startButton = (Button) findViewById(R.id.startButton);
        stopButton = (Button) findViewById(R.id.stopButton);
        clearButton = (Button) findViewById(R.id.clearButton);
        clearAllButton = (Button) findViewById(R.id.clearAllButton);
        clearFieldButton = (Button) findViewById(R.id.clearFieldButton);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {  //listens for changes in the radio button selection
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton button = (RadioButton) findViewById(checkedId);
                //displays the list that corresponds with the selected radio button
                if(button.getText().toString().equals("A")) {
                    players.setAdapter(itemAdapter);
                }
                if(button.getText().toString().equals("B")) {
                    players.setAdapter(itemAdapter2);
                }
                if(button.getText().toString().equals("C")) {
                    players.setAdapter(itemAdapter3);
                }
                if(button.getText().toString().equals("GK/OUT")) {
                    players.setAdapter(itemAdapter4);
                }

            }
        });
        startButton.setOnClickListener(startTimerListener);
        stopButton.setOnClickListener(stopTimerListener);
        clearButton.setOnClickListener(clearTimersListener);
        clearAllButton.setOnClickListener(clearAllListener);
        clearFieldButton.setOnClickListener(clearFieldListener);

        Firebase.setAndroidContext(this);

        ref = new Firebase("your test firebase account url goes here");

        ref.addChildEventListener(new ChildEventListener() {

            @Override //this is called when screen is first generated and anytime a new child is added
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Player p = dataSnapshot.getValue(Player.class); //converts datasnapshot to a player
                if(p.isInPlay()) { //add player to in play list and generate image if player is in play
                    inPlayPlayers.add(p);
                    generateImageViewForPlayer(p, p.getxPos(), p.getyPos());
                }
                else { //add the player to the all players list if they are not in play (on the field)
                    if(p.getGroupNum() == 1)
                        group1.add(p);
                    else if(p.getGroupNum() == 2)
                        group2.add(p);
                    else if(p.getGroupNum() == 3)
                        group3.add(p);
                    else if(p.getGroupNum() == 4)
                        group4.add(p);
                }
                itemAdapter.notifyDataSetChanged(); //activate the list adapter to update the on screen list view
                itemAdapter2.notifyDataSetChanged();
                itemAdapter3.notifyDataSetChanged();
                itemAdapter4.notifyDataSetChanged();
            }

            @Override //this is called when any child is changed and the data snapshot is the child that was changed
            public void onChildChanged(final DataSnapshot dataSnapshot, String s) {

                dataSnapshot.getRef().addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot2, String s2) {
                        if(dataSnapshot2.getKey().equals("timerOn")) {
                            boolean isTimerOn = dataSnapshot2.getValue(Boolean.class);
                            Player p = dataSnapshot.getValue(Player.class);
                            if (isTimerOn) {
                                try {
                                    Profiler.getInstance().start(p.getFirstName() + p.getLastName());
                                    imageViewMap.get(p.getFirstName() + p.getLastName()).setBackgroundResource(R.drawable.image_border_green);
                                } catch (ProfilerStartException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    Profiler.getInstance().stop(p.getFirstName() + p.getLastName());
                                    Firebase r = ref.child("Player" + p.getLastName() + p.getFirstName()).child("timeOnField");
                                    r.setValue(Profiler.getInstance().getDuration(p.getFirstName() + p.getLastName()));
                                    imageViewMap.get(p.getFirstName() + p.getLastName()).setBackgroundResource(R.drawable.image_border_red);
                                } catch (ProfilerEndException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        else if(dataSnapshot2.getKey().equals("timeOnField")) {
                            int time = dataSnapshot2.getValue(Integer.class);
                            Player p = dataSnapshot.getValue(Player.class);

                            if(time == -1) {  //-1 flag for clear button click
                                Profiler.getInstance().clear(p.getFirstName() + p.getLastName());
                                if(isInList(group1, p)) {
                                    itemAdapter.notifyDataSetChanged();
                                }
                                else if(isInList(group2, p)) {
                                    itemAdapter2.notifyDataSetChanged();
                                }
                                else if(isInList(group3, p)) {
                                    itemAdapter3.notifyDataSetChanged();
                                }
                                else if(isInList(group4, p)) {
                                    itemAdapter4.notifyDataSetChanged();
                                }
                            }
                        }
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });

                Player p = dataSnapshot.getValue(Player.class);

                if(p.getGroupNum() == 0) {
                    removeFromList(p);

                    if(group1.isEmpty() && group2.isEmpty() && group3.isEmpty() && group4.isEmpty()) {
                        Intent intent = new Intent(PracticeFieldActivity.this, PracticeSetUpActivity.class);
                        startActivity(intent);
                    }
                }

                if(p.isInPlay() && p.getxPos() != null && p.getyPos() != null) {  //if the player is in play and has a position
                    if(!isInList(inPlayPlayers, p)) { //if the player is not already on the field
                        inPlayPlayers.add(p);
                        removeFromList(p);
                        generateImageViewForPlayer(p, p.getxPos(), p.getyPos()); //generate image for player on field
                    }
                    else {  //if the player is already on the field by moved to a new position

                        imageViewMap.get(p.getFirstName() + p.getLastName()).setX(p.getxPos());
                        imageViewMap.get(p.getFirstName() + p.getLastName()).setY(p.getyPos());
                    }
                }
                else if(!p.isInPlay() && p.getxPos() == null && p.getyPos() == null) { //if the player is moved from the field and to the list
                    for(Player player : inPlayPlayers) { //search for the correct player

                        if(p.getFirstName().equals(player.getFirstName()) && p.getLastName().equals(player.getLastName())) {

                            field.removeView(imageViewMap.get(player.getFirstName() + player.getLastName()));
                            imageViewMap.remove(player.getFirstName() + player.getLastName());

                            inPlayPlayers.remove(player); //remove player form in play players
                            break;
                        }
                    }
                    //determine which list they were added to, and notify the correct adapter
                    if(p.getGroupNum() == 1 && !isInList(group1, p)) {
                        group1.add(p);
                        itemAdapter.notifyDataSetChanged();
                    }
                    else if(p.getGroupNum() == 2 && !isInList(group2, p)) {
                        group2.add(p);
                        itemAdapter2.notifyDataSetChanged();
                    }
                    else if(p.getGroupNum() == 3 && !isInList(group3, p)) {
                        group3.add(p);
                        itemAdapter3.notifyDataSetChanged();
                    }
                    else if(p.getGroupNum() == 4 && !isInList(group4, p)) {
                        group4.add(p);
                        itemAdapter4.notifyDataSetChanged();
                    }
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

        itemAdapter = new ItemAdapter(this, R.layout.player_profile_row_layout, group1);
        itemAdapter2 = new ItemAdapter(this, R.layout.player_profile_row_layout, group2);
        itemAdapter3 = new ItemAdapter(this, R.layout.player_profile_row_layout, group3);
        itemAdapter4 = new ItemAdapter(this, R.layout.player_profile_row_layout, group4);

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

    private void removeFromList(Player p) {
        for (Player player : group1) { //find the correct player to remove
            if (p.getFirstName().equals(player.getFirstName()) && p.getLastName().equals(player.getLastName())) {
                group1.remove(player);
                itemAdapter.notifyDataSetChanged();
                break;
            }
        }
        for (Player player : group2) { //find the correct player to remove
            if (p.getFirstName().equals(player.getFirstName()) && p.getLastName().equals(player.getLastName())) {
                group2.remove(player);
                itemAdapter2.notifyDataSetChanged();
                break;
            }
        }
        for (Player player : group3) { //find the correct player to remove
            if (p.getFirstName().equals(player.getFirstName()) && p.getLastName().equals(player.getLastName())) {
                group3.remove(player);
                itemAdapter3.notifyDataSetChanged();
                break;
            }
        }
        for (Player player : group4) { //find the correct player to remove
            if (p.getFirstName().equals(player.getFirstName()) && p.getLastName().equals(player.getLastName())) {
                group4.remove(player);
                itemAdapter4.notifyDataSetChanged();
                break;
            }
        }
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

            ClipData.Item item = new ClipData.Item(iv.getTag().toString());
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
                    Vibrator v1 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    v1.vibrate(300);
                    return true;
                case DragEvent.ACTION_DRAG_ENTERED:
                    return true;
                case DragEvent.ACTION_DRAG_LOCATION:
                    return true;
                case DragEvent.ACTION_DRAG_EXITED:
                    return true;
                case DragEvent.ACTION_DROP:
                    ClipData.Item item = event.getClipData().getItemAt(0);

                    if(item.getText().toString().contains("UserName:")) { //moving player from list to field

                        if(v != players) { //execute as long as they are not dropped in the list, from the list

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
                    }
                    else { //either moving player to new position or back to list
                        Player currentPlayer = null;
                        for(Player p : inPlayPlayers) {
                            String tag = item.getText().toString();
                            if(String.valueOf(p.getFirstName() + p.getLastName()).equals(tag)) { //find correct player by tag on the image
                                currentPlayer = p;
                            }
                        }

                        if(currentPlayer != null) {
                            if(v == field) { //moving around on the field
                                Firebase player = ref.child("Player" + currentPlayer.getLastName() + currentPlayer.getFirstName()).child("xPos");
                                player.setValue(event.getX());
                                player = ref.child("Player" + currentPlayer.getLastName() + currentPlayer.getFirstName()).child("yPos");
                                player.setValue(event.getY());
                            }
                            else if(v == players) { //moving back to the list
                                if(currentPlayer.isTimerOn()) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(PracticeFieldActivity.this);
                                    builder.setMessage("You must stop the timers first!").setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });
                                    AlertDialog alert = builder.create();
                                    alert.show();
                                }
                                else {
                                    Firebase player = ref.child("Player" + currentPlayer.getLastName() + currentPlayer.getFirstName()).child("inPlay");
                                    player.setValue(false);
                                    player = ref.child("Player" + currentPlayer.getLastName() + currentPlayer.getFirstName()).child("xPos");
                                    player.setValue(null);
                                    player = ref.child("Player" + currentPlayer.getLastName() + currentPlayer.getFirstName()).child("yPos");
                                    player.setValue(null);
                                }
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

        ImageView newImageView = new ImageView(PracticeFieldActivity.this);
        newImageView.setAdjustViewBounds(true);
        newImageView.setMaxHeight(90);
        newImageView.setMaxWidth(90);
        newImageView.setX(x);
        newImageView.setY(y);
        newImageView.setOnLongClickListener(fieldItemClickListener);
        byte[] bArray = Base64.decode(p.getImage(), Base64.DEFAULT);
        Bitmap bMap = BitmapFactory.decodeByteArray(bArray, 0, bArray.length);
        newImageView.setImageBitmap(bMap);
        newImageView.setTag(p.getFirstName() + p.getLastName());
        if(p.isTimerOn()) {
            newImageView.setBackgroundResource(R.drawable.image_border_green);
        }
        else {
            newImageView.setBackgroundResource(R.drawable.image_border_red);
        }
        p.setxPos(x);
        p.setyPos(y);

        Firebase player = ref.child("Player" + p.getLastName() + p.getFirstName()).child("xPos");
        player.setValue(x);
        player = ref.child("Player" + p.getLastName() + p.getFirstName()).child("yPos");
        player.setValue(y);

        field.addView(newImageView);
        imageViewMap.put(p.getFirstName() + p.getLastName(), newImageView);
    }

    public View.OnClickListener startTimerListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //go through all in-play players and start timers
            for (Player p : inPlayPlayers) {

                Firebase r = ref.child("Player" + p.getLastName() + p.getFirstName()).child("timerOn");
                r.setValue(true);
                p.setTimerOn(true);
            }
        }
    };

    public View.OnClickListener stopTimerListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //go through all in-play players and stop timers
            for(Player p : inPlayPlayers) {

                Firebase r = ref.child("Player" + p.getLastName() + p.getFirstName()).child("timerOn");
                r.setValue(false);
                p.setTimerOn(false);
            }
        }
    };

    public View.OnClickListener clearTimersListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            AlertDialog.Builder builder = new AlertDialog.Builder(PracticeFieldActivity.this);
            builder.setTitle("Confirm");
            builder.setMessage("Are you sure you would like to clear all timers?").setCancelable(false);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    clearTimersForAllPlayers();
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
    };

    void clearTimersForAllPlayers() {
        for(Player p : group1) {
            Firebase r = ref.child("Player" + p.getLastName() + p.getFirstName()).child("timeOnField");
            r.setValue(-1);
        }
        for(Player p : group2) {
            Firebase r = ref.child("Player" + p.getLastName() + p.getFirstName()).child("timeOnField");
            r.setValue(-1);
        }
        for(Player p : group3) {
            Firebase r = ref.child("Player" + p.getLastName() + p.getFirstName()).child("timeOnField");
            r.setValue(-1);
        }
        for(Player p : group4) {
            Firebase r = ref.child("Player" + p.getLastName() + p.getFirstName()).child("timeOnField");
            r.setValue(-1);
        }
        for(Player p : inPlayPlayers) {
            if(p.isTimerOn()) { //if timer is still on, stop it before clearing it
                Firebase r = ref.child("Player" + p.getLastName() + p.getFirstName()).child("timerOn");
                r.setValue(false);
                p.setTimerOn(false);
            }
            Firebase r = ref.child("Player" + p.getLastName() + p.getFirstName()).child("timeOnField");
            r.setValue(-1);
        }
    }

    void clearInPlayPlayers() {
        for(Player p : inPlayPlayers) {
            Firebase player = ref.child("Player" + p.getLastName() + p.getFirstName()).child("inPlay");
            player.setValue(false);
            player = ref.child("Player" + p.getLastName() + p.getFirstName()).child("xPos");
            player.setValue(null);
            player = ref.child("Player" + p.getLastName() + p.getFirstName()).child("yPos");
            player.setValue(null);
        }
    }

    void resetAllGroupNums() {
        for(Player p : group1) {
            Firebase r = ref.child("Player" + p.getLastName() + p.getFirstName()).child("groupNum");
            r.setValue(0);
        }
        for(Player p : group2) {
            Firebase r = ref.child("Player" + p.getLastName() + p.getFirstName()).child("groupNum");
            r.setValue(0);
        }
        for(Player p : group3) {
            Firebase r = ref.child("Player" + p.getLastName() + p.getFirstName()).child("groupNum");
            r.setValue(0);
        }
        for(Player p : group4) {
            Firebase r = ref.child("Player" + p.getLastName() + p.getFirstName()).child("groupNum");
            r.setValue(0);
        }
        for(Player p : inPlayPlayers) {
            Firebase r = ref.child("Player" + p.getLastName() + p.getFirstName()).child("groupNum");
            r.setValue(0);
        }
    }

    public View.OnClickListener clearAllListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(PracticeFieldActivity.this);
            builder.setTitle("Confirm");
            builder.setMessage("Are you sure you would like to clear everything?").setCancelable(false);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //clear all timers
                    clearTimersForAllPlayers();
                    //in play back to false
                    clearInPlayPlayers();
                    //set all group nums to zero
                    resetAllGroupNums();
                    //return to sorting page
                    Intent intent = new Intent(PracticeFieldActivity.this, PracticeSetUpActivity.class);
                    startActivity(intent);
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Do nothing!!!
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
        }
    };

    public View.OnClickListener clearFieldListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //if the timers are still running don't let them clear the field
            if (inPlayPlayers.size() > 0 && inPlayPlayers.get(0).isTimerOn()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PracticeFieldActivity.this);
                builder.setMessage("You must stop the timers first!!!").setCancelable(false);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
            else { //ask for confirmation before clearing
                AlertDialog.Builder builder = new AlertDialog.Builder(PracticeFieldActivity.this);
                builder.setTitle("Confirm");
                builder.setMessage("Are you sure you would like to clear the field?").setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //in play back to false
                        clearInPlayPlayers();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Do nothing!!!
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    };
}
