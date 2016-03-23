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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseListAdapter;

import java.util.ArrayList;

public class PracticeFieldActivity extends AppCompatActivity {

    ListView players;
    RelativeLayout field;

    FirebaseListAdapter adapter;
    ItemAdapter iAdapter;

    MyDragEventListener myDragEventListener = new MyDragEventListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_field);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        players = (ListView) findViewById(R.id.fieldPlayersListView);
        field = (RelativeLayout) findViewById(R.id.field);

        Firebase.setAndroidContext(this);

        Firebase ref = new Firebase("https://soccer-management.firebaseio.com/Profiles");

//        adapter = new FirebaseListAdapter<Player>(this, Player.class, R.layout.player_profile_row_layout, ref) {
//
//            @Override
//            protected void populateView(View view, Player player, int i) {
//                String name = player.getLastName() + ", " + player.getFirstName() + "\n" + player.getPosition();
//                ((TextView)view.findViewById(R.id.playerNameTextView)).setText(name);
//                byte[] bArray = Base64.decode(player.getImage(), Base64.DEFAULT);
//                Bitmap bMap = BitmapFactory.decodeByteArray(bArray, 0, bArray.length);
//                ((ImageView) view.findViewById(R.id.playerImageView)).setImageBitmap(bMap);
//                PlayerLists.allPlayers.add(player);
//            }
//        };

        iAdapter = new ItemAdapter(this, R.layout.player_profile_row_layout, PlayerLists.allPlayers);

        players.setAdapter(iAdapter);
        players.setOnItemLongClickListener(listItemClickListener);
        players.setOnDragListener(myDragEventListener);
        field.setOnDragListener(myDragEventListener);
    }

    AdapterView.OnItemLongClickListener listItemClickListener = new AdapterView.OnItemLongClickListener(){

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            ClipData.Item item = new ClipData.Item(PlayerLists.allPlayers.get(position).getFirstName() +
                    " " + PlayerLists.allPlayers.get(position).getLastName());

            String[] clipDescription = {ClipDescription.MIMETYPE_TEXT_PLAIN};
            ClipData dragData = new ClipData((CharSequence)view.getTag(), clipDescription,item);
            View.DragShadowBuilder myShadow = new MyDragShadowBuilder(view);
            view.startDrag(dragData, myShadow, PlayerLists.allPlayers.get(position), 0);

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
                    Log.d("ACTION_DRAG_STARTED", "start");
                    return true;
                case DragEvent.ACTION_DRAG_ENTERED:
                    Log.d("ACTION_DRAG_ENTERED", "entered");
                    return true;
                case DragEvent.ACTION_DRAG_LOCATION:
                    Log.d("ACTION_DRAG_LOCATION", "location");
                    return true;
                case DragEvent.ACTION_DRAG_EXITED:
                    Log.d("ACTION_DRAG_EXITED", "exited");
                    return true;
                case DragEvent.ACTION_DROP:
                    Log.d("ACTION_DROP", "drop");
                    ClipData.Item item = event.getClipData().getItemAt(0);

                    String droppedItem = item.getText().toString();
                    int index = droppedItem.indexOf(" ");
                    String firstName = droppedItem.substring(0, index);
                    String lastName = droppedItem.substring(index + 1);
                    for(int i = 0; i < PlayerLists.allPlayers.size(); i++) {
                        if(PlayerLists.allPlayers.get(i).getFirstName().equals(firstName) && PlayerLists.allPlayers.get(i).getLastName().equals(lastName)) {
                            generateImageViewForPlayer(PlayerLists.allPlayers.get(i), event.getX(), event.getY());

                            PlayerLists.inPlayPlayers.add(PlayerLists.allPlayers.get(i));
                            //also start timer for this player
                            PlayerLists.allPlayers.remove(i);
                            iAdapter.notifyDataSetChanged();
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
        byte[] bArray = Base64.decode(p.getImage(), Base64.DEFAULT);
        Bitmap bMap = BitmapFactory.decodeByteArray(bArray, 0, bArray.length);
        newImageView.setImageBitmap(bMap);
        p.setxPos(x);
        p.setyPos(y);
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
