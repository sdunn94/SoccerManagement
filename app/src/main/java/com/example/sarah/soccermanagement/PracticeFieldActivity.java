package com.example.sarah.soccermanagement;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;

public class PracticeFieldActivity extends AppCompatActivity {

    ListView players;
    RelativeLayout field;
    ItemAdapter itemAdapter;

    Firebase ref;
    private static final String TAG = "MyActivity";
    MyDragEventListener myDragEventListener = new MyDragEventListener();
    ArrayList <Player> playerList = new ArrayList<>();
    ArrayList<Player> inPlayPlayers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_field);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        players = (ListView) findViewById(R.id.fieldPlayersListView);
        field = (RelativeLayout) findViewById(R.id.field);

        Firebase.setAndroidContext(this);

        ref = new Firebase("https://soccer-management.firebaseio.com/Profiles");

        ref.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Player p = dataSnapshot.getValue(Player.class);
                if(p.isInPlay()) {
                    inPlayPlayers.add(p);
                    generateImageViewForPlayer(p, p.getxPos(), p.getyPos());
                }
                else {
                    playerList.add(p);
                }
                itemAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Player p = dataSnapshot.getValue(Player.class);
                if(p.isInPlay() && p.getxPos() != null && p.getyPos() != null) {
                    inPlayPlayers.add(p);
                    for(Player player : playerList) {
                        if(p.getFirstName().equals(player.getFirstName()) && p.getLastName().equals(player.getLastName())) {
                            playerList.remove(player);
                            break;
                        }
                    }
                    generateImageViewForPlayer(p, p.getxPos(), p.getyPos());
                    itemAdapter.notifyDataSetChanged();
                }
                else if(!p.isInPlay()) {
                    for(Player player : inPlayPlayers) {
                        if(p.getFirstName().equals(player.getFirstName()) && p.getLastName().equals(player.getLastName())) {
                            inPlayPlayers.remove(player);
                            break;
                        }
                    }
                    playerList.add(p);
                    itemAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //N/A
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                //N/A
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

    AdapterView.OnItemLongClickListener listItemClickListener = new AdapterView.OnItemLongClickListener(){

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            LinearLayout linearLayoutParent = (LinearLayout) view;
            TextView tv = (TextView) linearLayoutParent.getChildAt(1);
            String text = tv.getText().toString();
            int index1 = text.indexOf(',');
            int index2 = text.indexOf('\n');
            ClipData.Item item = new ClipData.Item(text.substring(index1 + 2, index2) +
                    " " + text.substring(0, index1));

            String[] clipDescription = {ClipDescription.MIMETYPE_TEXT_PLAIN};
            ClipData dragData = new ClipData((CharSequence)view.getTag(), clipDescription,item);
            View.DragShadowBuilder myShadow = new MyDragShadowBuilder(view);
            view.startDrag(dragData, myShadow, null, 0);

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
                    Log.d(TAG, "start");
                    return true;
                case DragEvent.ACTION_DRAG_ENTERED:
                    Log.d(TAG, "entered");
                    return true;
                case DragEvent.ACTION_DRAG_LOCATION:
                    Log.d(TAG, "location");
                    return true;
                case DragEvent.ACTION_DRAG_EXITED:
                    Log.d(TAG, "exited");
                    return true;
                case DragEvent.ACTION_DROP:
                    Log.d(TAG, "drop");
                    ClipData.Item item = event.getClipData().getItemAt(0);

                    String droppedItem = item.getText().toString();
                    int index = droppedItem.indexOf(" ");
                    String firstName = droppedItem.substring(0, index);
                    String lastName = droppedItem.substring(index + 1);

                    Firebase player = ref.child("Player" + lastName + firstName).child("inPlay");
                    player.setValue(true);
                    player = ref.child("Player" + lastName + firstName).child("xPos");
                    player.setValue(event.getX());
                    player = ref.child("Player" + lastName + firstName).child("yPos");
                    player.setValue(event.getY());


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

    }

    public class ItemAdapter extends ArrayAdapter<Player> {

        private ArrayList<Player> objects;

        public ItemAdapter(Context context, int textViewResourceId, ArrayList<Player> objects){
            super(context, textViewResourceId, objects);
            this.objects = objects;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;

            if(v == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.player_profile_row_layout, null);
            }

            Player p = objects.get(position);

            if(p != null) {
                ImageView iv = (ImageView) v.findViewById(R.id.playerImageView);
                TextView tv = (TextView) v.findViewById(R.id.playerNameTextView);

                String name = p.getLastName() + ", " + p.getFirstName() + "\n" + p.getPosition();
                tv.setText(name);

                byte[] bArray = Base64.decode(p.getImage(), Base64.DEFAULT);
                Bitmap bMap = BitmapFactory.decodeByteArray(bArray, 0, bArray.length);
                iv.setImageBitmap(bMap);
            }

            return v;
        }
    }
}
