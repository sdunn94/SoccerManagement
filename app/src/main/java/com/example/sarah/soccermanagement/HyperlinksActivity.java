package com.example.sarah.soccermanagement;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.concurrent.ConcurrentLinkedDeque;

public class HyperlinksActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hyperlinks);

        TextView roster = (TextView) findViewById(R.id.text);
        roster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://athletics.carthage.edu/roster.aspx?path=msoc"));
                startActivity(intent);

            }
        });

        TextView insideSoccer = (TextView) findViewById(R.id.text3);
        insideSoccer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://www.insidesoccer.com/"));
                startActivity(intent);

            }
        });

        TextView docs = (TextView) findViewById(R.id.text5);
        docs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://accounts.google.com/ServiceLogin?service=wise&amp;passive=true&amp;continue=http%3A%2F%2Fdrive.google.com%2F%3Futm_source%3Den%26utm_medium%3Dbutton%26utm_campaign%3Dweb%26utm_content%3Dgotodrive%26usp%3Dgtd%26ltmpl%3Ddrive&amp;urp=https%3A%2F%2Fwww.google.com%2F#identifier"));
                startActivity(intent);

            }
        });
    }
}
