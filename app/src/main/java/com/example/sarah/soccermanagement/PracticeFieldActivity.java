package com.example.sarah.soccermanagement;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class PracticeFieldActivity extends AppCompatActivity {

    ListView players;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_field);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        String[] names = {"fred", "george", "harry", "ron", "hermione", "ginny", "dean", "hagrid", "dumbledore"};
        ArrayAdapter <String> myAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, names);

        players = (ListView) findViewById(R.id.listView);
        players.setAdapter(myAdapter);
    }


}
