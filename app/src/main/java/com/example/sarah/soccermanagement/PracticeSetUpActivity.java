package com.example.sarah.soccermanagement;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;

public class PracticeSetUpActivity extends AppCompatActivity {

    ListView allPlayers;
    ListView groupOnePlayers;
    ListView groupTwoPlayers;
    ListView groupThreePlayers;
    ListView groupFourPlayers;
    Button clearButton;
    Button startPracticeButton;
    TextView groupOneCount;
    TextView groupTwoCount;
    TextView groupThreeCount;
    TextView groupFourCount;

    ArrayList<Player> players = new ArrayList<>();
    ArrayList<Player> group1 = new ArrayList<>();
    ArrayList<Player> group2 = new ArrayList<>();
    ArrayList<Player> group3 = new ArrayList<>();
    ArrayList<Player> group4 = new ArrayList<>();

    Firebase ref;
    ItemAdapter itemAdapter1;
    ItemAdapter itemAdapter2;
    ItemAdapter itemAdapter3;
    ItemAdapter itemAdapter4;
    ItemAdapter itemAdapter5;
    CustomDragEventListener myDragEventListener = new CustomDragEventListener();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_set_up);

        Firebase.setAndroidContext(this);
        ref = new Firebase("https://soccer-management.firebaseio.com/Profiles");

        allPlayers = (ListView) findViewById(R.id.allPlayersListView);
        groupOnePlayers = (ListView) findViewById(R.id.groupOneListView);
        groupTwoPlayers = (ListView) findViewById(R.id.groupTwoListView);
        groupThreePlayers = (ListView) findViewById(R.id.groupThreeListView);
        groupFourPlayers = (ListView) findViewById(R.id.groupFourListView);
        clearButton = (Button) findViewById(R.id.clearButton);
        startPracticeButton = (Button) findViewById(R.id.startPracticeButton);
        groupOneCount = (TextView) findViewById(R.id.groupOneCountTV);
        groupTwoCount = (TextView) findViewById(R.id.groupTwoCountTV);
        groupThreeCount = (TextView) findViewById(R.id.groupThreeCountTV);
        groupFourCount = (TextView) findViewById(R.id.groupFourCountTV);

        clearButton.setOnClickListener(clearClickListener);
        startPracticeButton.setOnClickListener(startPracticeListener);

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Player p = dataSnapshot.getValue(Player.class);
                if(p.getGroupNum() == 0 && !isInList(players, p)) {
                    players.add(p);
                    itemAdapter1.notifyDataSetChanged();
                }
                else if(p.getGroupNum() == 1 && !isInList(group1, p)) {
                    group1.add(p);
                    itemAdapter2.notifyDataSetChanged();
                }
                else if(p.getGroupNum() == 2 && !isInList(group2, p)) {
                    group2.add(p);
                    itemAdapter3.notifyDataSetChanged();
                }
                else if(p.getGroupNum() == 3 && !isInList(group3, p)) {
                    group3.add(p);
                    itemAdapter4.notifyDataSetChanged();
                }
                else if(!isInList(group4, p))
                {
                    group4.add(p);
                    itemAdapter5.notifyDataSetChanged();
                }

                groupOneCount.setText(String.valueOf("Group A:   " + group1.size()));
                groupTwoCount.setText(String.valueOf("Group B:   " + group2.size()));
                groupThreeCount.setText(String.valueOf("Group C:   " + group3.size()));
                groupFourCount.setText(String.valueOf("Group GK/OUT:   " + group4.size()));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Player p = dataSnapshot.getValue(Player.class);
                if(p.getGroupNum() == 0 && !isInList(players, p)) {
                    players.add(p);
                    itemAdapter1.notifyDataSetChanged();
                    if(isInList(group1, p)) {
                        removeFromList(group1, p);
                        itemAdapter2.notifyDataSetChanged();
                    }
                    else if(isInList(group2, p)) {
                        removeFromList(group2, p);
                        itemAdapter3.notifyDataSetChanged();
                    }
                    else if(isInList(group3, p)) {
                        removeFromList(group3, p);
                        itemAdapter4.notifyDataSetChanged();
                    }
                    else if(isInList(group4, p)) {
                        removeFromList(group4, p);
                        itemAdapter5.notifyDataSetChanged();
                    }
                }
                else if(p.getGroupNum() == 1 && !isInList(group1, p)) {
                    group1.add(p);
                    itemAdapter2.notifyDataSetChanged();
                    if(isInList(players, p)) {
                        removeFromList(players, p);
                        itemAdapter1.notifyDataSetChanged();
                    }
                    else if(isInList(group2, p)) {
                        removeFromList(group2, p);
                        itemAdapter3.notifyDataSetChanged();
                    }
                    else if(isInList(group3, p)) {
                        removeFromList(group3, p);
                        itemAdapter4.notifyDataSetChanged();
                    }
                    else if(isInList(group4, p)) {
                        removeFromList(group4, p);
                        itemAdapter5.notifyDataSetChanged();
                    }
                }
                else if(p.getGroupNum() == 2 && !isInList(group2, p)) {
                    group2.add(p);
                    itemAdapter3.notifyDataSetChanged();
                    if(isInList(players, p)) {
                        removeFromList(players, p);
                        itemAdapter1.notifyDataSetChanged();
                    }
                    else if(isInList(group1, p)) {
                        removeFromList(group1, p);
                        itemAdapter2.notifyDataSetChanged();
                    }
                    else if(isInList(group3, p)) {
                        removeFromList(group3, p);
                        itemAdapter4.notifyDataSetChanged();
                    }
                    else if(isInList(group4, p)) {
                        removeFromList(group4, p);
                        itemAdapter5.notifyDataSetChanged();
                    }
                }
                else if(p.getGroupNum() == 3 && !isInList(group3, p)) {
                    group3.add(p);
                    itemAdapter4.notifyDataSetChanged();
                    if(isInList(players, p)) {
                        removeFromList(players, p);
                        itemAdapter1.notifyDataSetChanged();
                    }
                    else if(isInList(group2, p)) {
                        removeFromList(group2, p);
                        itemAdapter3.notifyDataSetChanged();
                    }
                    else if(isInList(group1, p)) {
                        removeFromList(group1, p);
                        itemAdapter2.notifyDataSetChanged();
                    }
                    else if(isInList(group4, p)) {
                        removeFromList(group4, p);
                        itemAdapter5.notifyDataSetChanged();
                    }
                }
                else if(p.getGroupNum() == 4 && !isInList(group4, p)) {
                    group4.add(p);
                    itemAdapter5.notifyDataSetChanged();
                    if(isInList(players, p)) {
                        removeFromList(players, p);
                        itemAdapter1.notifyDataSetChanged();
                    }
                    else if(isInList(group2, p)) {
                        removeFromList(group2, p);
                        itemAdapter3.notifyDataSetChanged();
                    }
                    else if(isInList(group3, p)) {
                        removeFromList(group3, p);
                        itemAdapter4.notifyDataSetChanged();
                    }
                    else if(isInList(group1, p)) {
                        removeFromList(group1, p);
                        itemAdapter2.notifyDataSetChanged();
                    }
                }
                groupOneCount.setText(String.valueOf("Group A:   " + group1.size()));
                groupTwoCount.setText(String.valueOf("Group B:   " + group2.size()));
                groupThreeCount.setText(String.valueOf("Group C:   " + group3.size()));
                groupFourCount.setText(String.valueOf("Group GK/OUT:   " + group4.size()));
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

        itemAdapter1 = new ItemAdapter(this, R.layout.player_profile_row_layout, players);
        itemAdapter2 = new ItemAdapter(this, R.layout.player_profile_row_layout, group1);
        itemAdapter3 = new ItemAdapter(this, R.layout.player_profile_row_layout, group2);
        itemAdapter4 = new ItemAdapter(this, R.layout.player_profile_row_layout, group3);
        itemAdapter5 = new ItemAdapter(this, R.layout.player_profile_row_layout, group4);

        allPlayers.setAdapter(itemAdapter1);
        allPlayers.setOnItemLongClickListener(listItemClickListener);
        allPlayers.setOnDragListener(myDragEventListener);

        groupOnePlayers.setAdapter(itemAdapter2);
        groupOnePlayers.setOnItemLongClickListener(listItemClickListener);
        groupOnePlayers.setOnDragListener(myDragEventListener);

        groupTwoPlayers.setAdapter(itemAdapter3);
        groupTwoPlayers.setOnItemLongClickListener(listItemClickListener);
        groupTwoPlayers.setOnDragListener(myDragEventListener);

        groupThreePlayers.setAdapter(itemAdapter4);
        groupThreePlayers.setOnItemLongClickListener(listItemClickListener);
        groupThreePlayers.setOnDragListener(myDragEventListener);

        groupFourPlayers.setAdapter(itemAdapter5);
        groupFourPlayers.setOnItemLongClickListener(listItemClickListener);
        groupFourPlayers.setOnDragListener(myDragEventListener);
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

    private void removeFromList(ArrayList<Player> list, Player p) {
        for(Player player : list) { //find the correct player to remove
            if(p.getFirstName().equals(player.getFirstName()) && p.getLastName().equals(player.getLastName())) {
                list.remove(player);
                break;
            }
        }
    }

    public View.OnClickListener clearClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            for(Player p : group1) {
                Firebase player = ref.child("Player" + p.getLastName() + p.getFirstName()).child("groupNum");
                player.setValue(0);
            }
            for(Player p : group2) {
                Firebase player = ref.child("Player" + p.getLastName() + p.getFirstName()).child("groupNum");
                player.setValue(0);
            }
            for(Player p : group3) {
                Firebase player = ref.child("Player" + p.getLastName() + p.getFirstName()).child("groupNum");
                player.setValue(0);
            }
            for(Player p : group4) {
                Firebase player = ref.child("Player" + p.getLastName() + p.getFirstName()).child("groupNum");
                player.setValue(0);
            }
        }
    };

    public View.OnClickListener startPracticeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(PracticeSetUpActivity.this, PracticeFieldActivity.class);

            startActivity(intent);
        }
    };

    public AdapterView.OnItemLongClickListener listItemClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
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
            View.DragShadowBuilder myShadow = new CustomDragShadowBuilder(view);
            view.startDrag(dragData, myShadow, null, 0);

            return true;
        }
    };

    private static class CustomDragShadowBuilder extends View.DragShadowBuilder {
        private static Drawable shadow;

        public CustomDragShadowBuilder(View v) {
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

    protected class CustomDragEventListener implements View.OnDragListener {

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
                    String droppedItem = item.getText().toString();
                    int index1 = droppedItem.indexOf(':');
                    int index = droppedItem.indexOf(" ");
                    String firstName = droppedItem.substring(index1 + 1, index);
                    String lastName = droppedItem.substring(index + 1);

                    Firebase player = ref.child("Player" + lastName + firstName).child("groupNum");

                    if(v == allPlayers) {
                        player.setValue(0);
                    }
                    else if(v == groupOnePlayers) {
                        player.setValue(1);
                    }
                    else if(v == groupTwoPlayers) {
                        player.setValue(2);
                    }
                    else if(v == groupThreePlayers) {
                        player.setValue(3);
                    }
                    else if(v == groupFourPlayers) {
                        player.setValue(4);
                    }

                    return true;
                case DragEvent.ACTION_DRAG_ENDED:
                    return true;
                default:
                    return false;
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PracticeSetUpActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
