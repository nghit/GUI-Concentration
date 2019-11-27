/***
 * file: scoreScreenActivity.java
 * author: Team Dream Crushers
 * class: CS 2450 - Programming Graphical User Interfaces
 *
 * assignment: Android Studio Project
 * date last modified: 11/27/19
 *
 * purpose: This class creates the score screen after the game, and determines where to save the
 * score if it is a new high score
 *
 **/

package com.example.jason.ftp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class scoreScreenActivity extends AppCompatActivity {

    private File file;
    private int numWords;
    private final int NUM_OF_SCORES = 5;
    private FileOutputStream fos;
    private FileInputStream fis;
    private String contents;
    private Scanner kb;
    private String[] highScores;
    private int[] HSInts;
    private int score;
    private AlertDialog.Builder builder;
    private EditText input;
    private String m_Text = "";
    private String newEntry;
    private Intent svc;
    private ImageButton disableMusic;
    private Boolean playing;

    @Override
    protected void onCreate(Bundle savedInstanceState) { //activity constructor
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_screen);

        disableMusic = (ImageButton) findViewById(R.id.disableMusic); //button for adjusting music
        playing = getIntent().getExtras().getBoolean("playingValue");

        svc = new Intent(this, MusicService.class);
        svc.setAction("com.example.jason.ftp.MusicService");
        startService(svc); //plays music by default


        if(playing ==true){

        }
        else{
            stopService(svc); //do nothing if it is playing, but stop the service if it isn't
        }
        highScores = new String[NUM_OF_SCORES];
        HSInts = new int[NUM_OF_SCORES];

        numWords = getIntent().getIntExtra("numWords", 0);

        switch(numWords) //chooses which high score file to use based on numWords selected by user
        {
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

        try {
            if(file.length() == 0 || !file.exists()) //creates file if it hasn't been already
            {
                fos = new FileOutputStream(file);
                fos.write("ABC...5\nABC...4\nABC...3\nABC...2\nABC...1".getBytes());
            }

            fis = new FileInputStream(file);

            int length = (int) file.length();
            byte[] bytes = new byte[length];
            fis.read(bytes);
            contents = new String(bytes);
            kb = new Scanner(contents); //takes the contents of the file and places them in a String for easy management
        }
        catch(IOException e){
            e.getMessage();
        }

        for (int i = 0; i < highScores.length; i++) //places the data into arrays
        {
            highScores[i] = kb.nextLine();
            HSInts[i] = Integer.parseInt(highScores[i].replaceAll("[\\D]", ""));
        }

        score = getIntent().getIntExtra("score", 0);
        TextView scoreLabel = findViewById(R.id.scoreLabel);
        Bundle extras = getIntent().getExtras();
        StringBuilder sb = new StringBuilder();
        sb.append(extras.getInt("score"));

        scoreLabel.setText(sb.toString()); //sets scores on score screen display
        builder = new AlertDialog.Builder(this);
        input = new EditText(this);

        ((ImageButton) findViewById(R.id.disableMusic)).setOnClickListener(new View.OnClickListener() //speaker in corner for music
        {

            @Override
            public void onClick(View v)
            {
                if(playing ==true){
                    stopService(svc);
                    playing=false;
                }
                else{
                    startService(svc);
                    playing=true;
                }

            }


        });

        ((Button)findViewById(R.id.scoreMenuButton)).setOnClickListener(new View.OnClickListener() { //menu

            @Override
            public void onClick(View view)
            {
                if (score >= HSInts[HSInts.length - 1])
                {

                    builder.setTitle("    New High Score! Enter your name: \n               Limit: Three characters");

                    // Set up the input

                    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                    input.setFilters(new InputFilter[] { new InputFilter.LengthFilter(3) });
                    builder.setView(input); //can only enter 3 characters

                    // Set up the buttons
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            m_Text = input.getText().toString();
                            newEntry = m_Text + "..." + score;
                            try { updateHighScore(newEntry, score); } catch (IOException e) { e.getMessage(); }
                            startActivity(new Intent(scoreScreenActivity.this, Manager.class));
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            startActivity(new Intent(scoreScreenActivity.this, Manager.class));
                        }
                    });

                    builder.show();
                }
                else
                {
                    Intent i = new Intent(com.example.jason.ftp.scoreScreenActivity.this, Manager.class);
                    i.putExtra("playingValue", playing);
                    startActivity(i);                } //go back to menu, carry music with activity

            }
        });
    }

    private void updateHighScore(String newEntry, int score) throws IOException
    {
        int count = 0; boolean found = false;
        while (!found && count < highScores.length)
        {
            if (score >= HSInts[count]) //if user score is greater than lowest score, sort in
            {
                String temp;
                for (int i = count; i < highScores.length; i++) //rotates variables out of list
                {
                    temp = highScores[i];
                    highScores[i] = newEntry;
                    newEntry = temp;
                }
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < highScores.length; i++)
                {
                    if (i == highScores.length - 1)
                        sb.append(highScores[i]);
                    else
                        sb.append(highScores[i] + "\n");
                }
                fos = new FileOutputStream(file);
                fos.write(sb.toString().getBytes()); //writes new score list into file
                found = true;
            }
            count++;
        }

        fos.close();
        fis.close();
    }
    protected void onStop() {
        super.onStop();
    }

    protected void onPause() {
        super.onPause();
    }
    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, Manager.class);
        i.putExtra("playingValue", playing);
        startActivity(i);
    }
}
