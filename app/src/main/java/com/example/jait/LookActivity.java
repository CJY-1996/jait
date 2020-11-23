package com.example.jait;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class LookActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_look);
        String title = "";
        String contents = "";


        Bundle extras = getIntent().getExtras();

        title = extras.getString("title");
        contents = extras.getString("contents");



        TextView textView = (TextView) findViewById(R.id.look_title);
        TextView contentsView =(TextView) findViewById(R.id.look_contents);

        textView.setText(title);
        contentsView.setText(contents);

        findViewById(R.id.look_getchat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LookActivity.this,ChatActivity.class));
            }
        });
    }
}