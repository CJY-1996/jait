package com.example.jait;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LookActivity extends AppCompatActivity {
    final static String foldername = Environment.getExternalStorageDirectory().getAbsolutePath()+"/TestLog";
    final static String filename = "logfile.txt";

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
                getSaveFile();
                finish();

            }
        });
    }
    public File getSaveFile() {
        String str = "데이터를 넣으셔";
        // 파일 생성
        File saveFile = new File(foldername); // 저장 경로
// 폴더 생성

        if( !saveFile.exists()) {
            try {   //폴더 없으면 폴더 생성
                saveFile.mkdir();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        File file = new File(foldername+filename);
        try{
            FileOutputStream fos = new FileOutputStream(file);
            BufferedWriter buf = new BufferedWriter(new OutputStreamWriter(fos));

            long now = System.currentTimeMillis(); // 현재시간 받아오기
            Date date = new Date(now); // Date 객체 생성
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String nowTime = sdf.format(date);

            try {
                buf.append(nowTime + " "); // 날짜 쓰기
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            try {
                buf.append(str); // 파일 쓰기
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            try {
                buf.newLine(); // 개행
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            try {
                buf.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } catch(FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return saveFile;
    }


}