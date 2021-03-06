package com.example.sarah.soccermanagement;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Sarah on 4/7/2016
 */
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
            v = inflater.inflate(R.layout.player_profile_row_layout, null); //uses custom row layout that contains a image view and two text views
        }

        Player p = objects.get(position);

        if(p != null) {
            ImageView iv = (ImageView) v.findViewById(R.id.playerImageView);
            TextView tv = (TextView) v.findViewById(R.id.playerNameTextView);
            TextView tv2 = (TextView) v.findViewById(R.id.timeOnField);

            //fills the first text view with the name of the player
            String name = p.getLastName() + ", " + p.getFirstName() + "\n" + p.getPosition();
            tv.setText(name);
            //fills the second text view with the time the player spent on the field
            int milli = Profiler.getInstance().getDuration(p.getFirstName() + p.getLastName());
            int seconds = (milli / 1000) % 60;
            int minutes = (milli / 1000) / 60;
            String time;
            if(seconds < 10) {
                time = String.valueOf(minutes) + ":0" + String.valueOf(seconds);
            }
            else {
                time = String.valueOf(minutes) + ":" + String.valueOf(seconds);
            }
            tv2.setText(time);
            //populates the image view with either the player's photo or the carthage shield logo
            byte[] bArray = Base64.decode(p.getImage(), Base64.DEFAULT);
            Bitmap bMap = BitmapFactory.decodeByteArray(bArray, 0, bArray.length);
            iv.setImageBitmap(bMap);

        }

        return v;
    }
}