/***
 * file: HSActivity.java
 * author: Team Dream Crushers
 * class: CS 2450 - Programming Graphical User Interfaces
 *
 * assignment: Android Studio Project
 * date last modified: 11/27/19
 *
 * purpose: This creates the high score screen, and creates the files used to store the different
 * high scores for each word count.
 *
 **/

package com.example.jason.ftp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class HSActivity extends AppCompatActivity {

    private final int NUM_OF_SCORES = 5;
    private File file;
    private int numWords;
    private FileInputStream fis;
    private String contents;
    private Scanner kb;
    private String[] highScores;
    private FileOutputStream fos;
    private int newScore;
    private TextView score1;
    private TextView score2;
    private TextView score3;
    private TextView score4;
    private TextView score5;
    private ImageButton disableMusic;
    private boolean playing;
    private Intent svc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hs);

        disableMusic = (ImageButton) findViewById(R.id.disableMusic); //speaker button on top
        playing = getIntent().getExtras().getBoolean("playingValue");

        svc =new Intent(this, MusicService.class);
        svc.setAction("com.example.jason.ftp.MusicService");
        startService(svc); //start music by default

        if(playing ==true){

        }
        else{
            stopService(svc);
        }

        setHighScores(); //method for obtaining and displaying scores to screen

        ((Button)findViewById(R.id.menuButton)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(HSActivity.this, Manager.class);
                startActivity(i);

            }


        });
    }

    private void setHighScores() {
        highScores = new String[NUM_OF_SCORES];

        score1 = (TextView) findViewById(R.id.score1); //variables for actual TextView objects on screen
        score2 = (TextView) findViewById(R.id.score2);
        score3 = (TextView) findViewById(R.id.score3);
        score4 = (TextView) findViewById(R.id.score4);
        score5 = (TextView) findViewById(R.id.score5);

        newScore = getIntent().getIntExtra("score", 0);
        numWords = getIntent().getIntExtra("numWords", 0);

        switch (numWords) { //determines which file to access based on NumWords from user
            case 2:
                file = new File(this.getFilesDir(), "scores2.txt");
                break;
            case 3:
                file = new File(this.getFilesDir(), "scores3.txt");
                break;
            case 4:
                file = new File(this.getFilesDir(), "scores4.txt");
                break;
            case 5:
                file = new File(this.getFilesDir(), "scores5.txt");
                break;
            case 6:
                file = new File(this.getFilesDir(), "scores6.txt");
                break;
            case 7:
                file = new File(this.getFilesDir(), "scores7.txt");
                break;
            case 8:
                file = new File(this.getFilesDir(), "scores8.txt");
                break;
            case 9:
                file = new File(this.getFilesDir(), "scores9.txt");
                break;
            case 10:
                file = new File(this.getFilesDir(), "scores10.txt");
                break;
            default:
                break;
        }


        try { //initialization of file if not created yet
            if (file.length() == 0 || !file.exists()) {
                fos = new FileOutputStream(file);
                fis = new FileInputStream(file);
                fos.write("ABC...5\nABC...4\nABC...3\nABC...2\nABC...1".getBytes());
            }

            fis = new FileInputStream(file);

            int length = (int) file.length();
            byte[] bytes = new byte[length];
            fis.read(bytes);
            contents = new String(bytes);
            kb = new Scanner(contents); //places contents in String for easy access
        } catch (IOException e) {
            e.getMessage();
        }

        for (int i = 0; i < highScores.length; i++) {
            highScores[i] = kb.nextLine(); //loads data into array
        }

        score1.setText(highScores[0]); //sets list from array
        score2.setText(highScores[1]);
        score3.setText(highScores[2]);
        score4.setText(highScores[3]);
        score5.setText(highScores[4]);

        ((ImageButton) findViewById(R.id.disableMusic)).setOnClickListener(new View.OnClickListener() { //speaker button

            @Override
            public void onClick(View v) {
                if (playing == true) {
                    stopService(svc);
                    playing = false;
                } else {
                    startService(svc);
                    playing = true;
                }
                ((Button) findViewById(R.id.menuButton)).setOnClickListener(new View.OnClickListener() { //menu

                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(com.example.jason.ftp.HSActivity.this, Manager.class);
                        i.putExtra("playingValue", playing);
                        startActivity(i); //goes back to main menu and takes music with activity

                    }


                });
            }
        });
    }
}
