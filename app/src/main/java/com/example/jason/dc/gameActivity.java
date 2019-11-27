/***************************************************************
 * file: gameActivity.java
 * author: Team Dream Crushers
 * class: CS 2450 - Programming Graphical User Interfaces
 *
 * assignment: Android Studio Project
 * date last modified: 11/27/19
 *
 * purpose: Handles the game interface, fully functional game
 * that keeps track of score.
 ****************************************************************/

package com.example.jason.ftp;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class gameActivity extends AppCompatActivity
{

    private int ROW_COUNT;
    private int COL_COUNT;
    private Context context;
    private Drawable backImage;
    private int[][] cards;
    private boolean[][] revealedCards;
    private List<Drawable> images;
    private Card firstCard;
    private Card secondCard;
    private ButtonListener buttonListener;
    private View[][] buttons;
    private TableRow[] buttonRows;

    private static Object lock = new Object();

    private int numWords;
    private TableLayout mainTable;
    private UpdateCardsHandler handler;
    private int score;
    private int name;
    Intent svc;
    boolean playing;
    ImageButton disableMusic;

    //When backspace is pressed, return to menu.
    @Override
    public void onBackPressed() {
        Intent i = new Intent(gameActivity.this, Manager.class);
        i.putExtra("playingValue", playing);
        startActivity(i);
    }

    //When activity is created, perform the following:
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);

        //find music button
        disableMusic = (ImageButton) findViewById(R.id.disableMusic);

        //determine if music is playing or not
        playing = getIntent().getExtras().getBoolean("playingValue");


        //handles music
        svc =new Intent(this, MusicService.class);
        svc.setAction("com.example.jason.ftp.MusicService");
        startService(svc);

        //conditionals that allow same music state across activities
        if(playing ==true){

        }
        else{
            stopService(svc);
        }


        //access number of words and score from previous activity.
        //Log.i("NUMWORDS IS ", String.valueOf(getIntent().getIntExtra("numwords", 0)));
        numWords = getIntent().getIntExtra("numWords", 10);
        score = getIntent().getIntExtra("score", 0);

        //updates handler to take care of images
        handler = new UpdateCardsHandler();
        loadImages();
        backImage = getResources().getDrawable(R.drawable.icon);

        //switch statement to determine board dimension
        switch(numWords)
        {
            case 2:
                newGame(2, 2);
                break;
            case 3:
                newGame(2, 3);
                break;
            case 4:
                newGame(2, 4);
                break;
            case 5:
                newGame(5, 2);
                break;
            case 6:
                newGame(3, 4);
                break;
            case 7:
                newGame(7, 2);
                break;
            case 8:
                newGame(4, 4);
                break;
            case 9:
                newGame(6, 3);
                break;
            case 10:
                newGame(5, 4);
                break;
            default:
                break;
        }

        //find music icon
        ((ImageButton) findViewById(R.id.disableMusic)).setOnClickListener(new View.OnClickListener()
        {

            //when clicked, stop or play music
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

        //search for try again button
        ((Button) findViewById(R.id.button1)).setOnClickListener(new View.OnClickListener()
        {

            //if first and second card are chosen, flip image
            @Override
            public void onClick(View v)
            {
                if(firstCard!=null & secondCard !=null) {
                    buttons[firstCard.x][firstCard.y].setBackgroundDrawable(backImage);
                    buttons[secondCard.x][secondCard.y].setBackgroundDrawable(backImage);
                    firstCard = null;
                    secondCard = null;
                }

            }


        });

        //find new game button
        ((Button)findViewById(R.id.button2)).setOnClickListener(new View.OnClickListener() {

            //prompts dialog fragment for determining new game
            @Override
            public void onClick(View view)
            {
                DialogFragment dialog = new PlayDialogFragment();

                Bundle bundle = new Bundle();
                bundle.putBoolean("playingValue", playing);
                dialog.setArguments(bundle);

                dialog.show(getFragmentManager(), "play");
            }
        });

        //find exit game button
        ((Button) findViewById(R.id.button3)).setOnClickListener(new View.OnClickListener()
        {

            //when clicked, reveal all cards
            @Override
            public void onClick(View v)
            {
                for(int i = 0; i < buttons.length; i++)
                {
                    for(int j = 0; j < buttons[i].length; j++)
                    {
                        buttons[i][j].setBackgroundDrawable(images.get(cards[i][j]));
                        buttons[i][j].setClickable(false);
                    }
                }

                //do not allow click when game is ended
                findViewById(R.id.button1).setClickable(false);
                findViewById(R.id.button2).setClickable(false);
                findViewById(R.id.button3).setClickable(false);

                //allow user to see answers with a delay
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        Log.i("Sleeping ", "maybe");
                        Intent i = new Intent(com.example.jason.ftp.gameActivity.this, scoreScreenActivity.class);
                        i.putExtra("numWords", numWords);
                        i.putExtra("score", score);
                        i.putExtra("playingValue", playing);
                        firstCard = null;
                        secondCard = null;
                        score = 0;
                        i.putExtra("numWords", numWords);
                        i.putExtra("score", score);
                        startActivity(i);
                    }
                }, 2000);   //2 seconds

            }


        });

    }

    //MUSIC OVERIDDEN METHODS

    @Override
    protected void onStop() {
        super.onStop();
    }

    protected void onResume() {
        super.onResume();
    }

    protected void onPause() {
        super.onPause();
    }

    //SAVE THE STATE OF THE CARDS FOR ROTATION
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        savedInstanceState.putSerializable("cards", cards);
        savedInstanceState.putSerializable("revealedCards", revealedCards);
        savedInstanceState.putSerializable("firstCard", firstCard);
        savedInstanceState.putSerializable("secondCard", secondCard);
        //savedInstanceState.putSerializable("buttons", buttons);

        //for(int i = 0; i < buttonRows.length; i++)
        //{
        //    buttonRows[i].removeAllViews();
        //}

        savedInstanceState.putInt("score", score);
        Log.d("The score is ", "" + score);
        Log.i("THE INSTANCE ", "HAS BEEN SAVED");

        super.onSaveInstanceState(savedInstanceState);
    }

    //When instance is restored, restore the cards from before rotation
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        cards = (int[][]) savedInstanceState.getSerializable("cards");
        revealedCards = (boolean[][]) savedInstanceState.getSerializable("revealedCards");
        //buttons = (View[][]) savedInstanceState.getSerializable("buttons");

        //mainTable.removeAllViews();

        //for(int i = 0; i < buttons.length; i++)
        //{
        //    buttonRows[i] = new TableRow(context);
        //    Log.d("Parent is ", "" + buttons[0][0].getParent());
        //    buttonRows[i].setHorizontalGravity(Gravity.CENTER);

        //    for(int j = 0; j < buttons[i].length; j++)
        //    {
        //        buttonRows[i].addView(buttons[i][j]);
        //    }
        //    mainTable.addView(buttonRows[i]);
        //}

        for(int i = 0; i < buttons.length; i++)
        {
            for(int j = 0; j < buttons[i].length; j++)
            {
                if(revealedCards[i][j] == true)
                {
                    buttons[i][j].setClickable(false);
                    buttons[i][j].setBackgroundDrawable(images.get((cards[i][j])));
                }
            }
        }

        firstCard = (Card) savedInstanceState.getSerializable("firstCard");
        if(firstCard != null)
        {
            buttons[firstCard.x][firstCard.y].setBackgroundDrawable(images.get(cards[firstCard.x][firstCard.y]));
        }

        secondCard = (Card) savedInstanceState.getSerializable("secondCard");
        if(secondCard != null)
        {
            buttons[secondCard.x][secondCard.y].setBackgroundDrawable(images.get(cards[secondCard.x][secondCard.y]));
        }

        score = savedInstanceState.getInt("score");
        ((TextView) findViewById(R.id.tv1)).setText("Score: " + score);
        Log.d("The score is ", "" + score);
        Log.i("THE INSTANCE ", "HAS BEEN LOADED");
    }


    //CREATE NEW GAME METHOD
    public void newGame(int c, int r)
    {


        ROW_COUNT = r;
        COL_COUNT = c;

        setContentView(R.layout.game);
        buttonListener = new ButtonListener();

        mainTable = (TableLayout) findViewById(R.id.TableLayout03);


        context = mainTable.getContext();

        cards = new int[COL_COUNT][ROW_COUNT];
        revealedCards = new boolean[COL_COUNT][ROW_COUNT];
        buttons = new View[COL_COUNT][ROW_COUNT];
        buttonRows = new TableRow[COL_COUNT];

        TableRow tr = ((TableRow) findViewById(R.id.TableRow03));
        tr.removeAllViews();

        mainTable = new TableLayout(context);
        tr.addView(mainTable);

        for (int y = 0; y < ROW_COUNT; y++)
        {
            mainTable.addView(createRow(y));
        }

        //mainTable.removeAllViewsInLayout();

        //HANDLES ADDING THE BUTTONS TO THE TABLEROWS

        for(int i = 0; i < buttons.length; i++)
        {
            buttonRows[i] = new TableRow(context);
            Log.d("Parent is ", "" + buttons[0][0].getParent());
            buttonRows[i].setHorizontalGravity(Gravity.CENTER);

            for(int j = 0; j < buttons[i].length; j++)
            {
                buttonRows[i].addView(buttons[i][j]);
            }
            mainTable.addView(buttonRows[i]);
        }

        firstCard = null;
        loadCards();

        //UPDATE SCORE
        ((TextView) findViewById(R.id.tv1)).setText("Score: " + score);


    }

    //LOAD ALL IMAGES
    private void loadImages()
    {
        images = new ArrayList<Drawable>();

        images.add(getResources().getDrawable(R.drawable.card1));
        images.add(getResources().getDrawable(R.drawable.card2));
        images.add(getResources().getDrawable(R.drawable.card3));
        images.add(getResources().getDrawable(R.drawable.card4));
        images.add(getResources().getDrawable(R.drawable.card5));
        images.add(getResources().getDrawable(R.drawable.card6));
        images.add(getResources().getDrawable(R.drawable.card7));
        images.add(getResources().getDrawable(R.drawable.card8));
        images.add(getResources().getDrawable(R.drawable.card9));
        images.add(getResources().getDrawable(R.drawable.card10));

    }

    //LOAD CARDS, PERFORM NECESSARY ARITHMETICS
    private void loadCards()
    {
        try
        {
            int size = ROW_COUNT * COL_COUNT;

            Log.i("loadCards()", "size=" + size);

            ArrayList<Integer> list = new ArrayList<>();

            for (int i = 0; i < size; i++)
            {
                list.add(new Integer(i));
            }


            Random r = new Random();

            for (int i = size - 1; i >= 0; i--)
            {
                int t = 0;

                if (i > 0)
                {
                    t = r.nextInt(i);
                }

                t = list.remove(t).intValue();
                cards[i % COL_COUNT][i / COL_COUNT] = t % (size / 2);
                revealedCards[i % COL_COUNT][i / COL_COUNT] = false;

                Log.i("loadCards()", "card[" + (i % COL_COUNT) +
                        "][" + (i / COL_COUNT) + "]=" + cards[i % COL_COUNT][i / COL_COUNT]);
            }
        }
        catch (Exception e)
        {
            Log.e("loadCards()", e + "");
        }

    }

    //CREATES ROWS IN THE TABLEROWS
    private TableRow createRow(int y)
    {
        TableRow row = new TableRow(context);
        row.setHorizontalGravity(Gravity.CENTER);

        for (int x = 0; x < COL_COUNT; x++)
        {
            row.addView(createImageButton(x, y));
        }
        row.removeAllViews();
        return row;
    }

    //CREATES IMAGE BUTTONS(CARDS)
    private View createImageButton(int x, int y)
    {
        Button button = new Button(context);
        button.setBackgroundDrawable(backImage);
        button.setId(100 * x + y);
        button.setOnClickListener(buttonListener);
        buttons[x][y] = button;

        return button;
    }

    //CUSTOM BUTTON LISTENER FOR HANDLING CARD TURNS
    class ButtonListener implements View.OnClickListener
    {

        @Override
        public void onClick(View v)
        {

            synchronized (lock)
            {
                if (firstCard != null && secondCard != null)
                {
                    return;
                }
                int id = v.getId();
                int x = id / 100;
                int y = id % 100;
                turnCard((Button) v, x, y);
            }

        }

        private void turnCard(Button button, int x, int y)
        {
            if (firstCard == null)
            {
                firstCard = new Card(button, x, y);
                button.setBackgroundDrawable(images.get(cards[x][y]));
            }
            else
            {

                if (firstCard.x == x && firstCard.y == y)
                {
                    return; //the user pressed the same card
                }

                secondCard = new Card(button, x, y);
                revealedCards[x][y] = true;
                button.setBackgroundDrawable(images.get(cards[x][y]));

                //turns++;
                //((TextView)findViewById(R.id.tv1)).setText("Score: "+score);

                //turns++;
                //((TextView) findViewById(R.id.tv1)).setText("Score: " + score);


                TimerTask tt = new TimerTask()
                {

                    @Override
                    public void run()
                    {
                        try
                        {
                            synchronized (lock)
                            {
                                handler.sendEmptyMessage(0);
                            }
                        }
                        catch (Exception e)
                        {
                            Log.e("E1", e.getMessage());
                        }
                    }
                };

                Timer t = new Timer(false);
                t.schedule(tt, 500);
            }


        }

    }

    //HANDLES CARD UPDATES
    class UpdateCardsHandler extends Handler
    {

        @Override
        public void handleMessage(Message msg)
        {
            synchronized (lock)
            {
                checkCards();
            }
        }

        //METHOD CHECKS CORRECTNESS OF CHOSEN CARDS
        public void checkCards()
        {
            boolean gameOver = true;
            if(firstCard !=null && secondCard !=null) {
                if (cards[firstCard.x][firstCard.y] == cards[secondCard.x][secondCard.y]) {
                    score += 2;
                    ((TextView) findViewById(R.id.tv1)).setText("Score: " + score);

                    //firstCard.button.setVisibility(View.INVISIBLE);
                    buttons[firstCard.x][firstCard.y].setClickable(false);
                    revealedCards[firstCard.x][firstCard.y] = true;

                    //secondCard.button.setVisibility(View.INVISIBLE);
                    buttons[secondCard.x][secondCard.y].setClickable(false);
                    revealedCards[secondCard.x][secondCard.y] = true;
                    firstCard = null;
                    secondCard = null;
                } else {
                    if (score > 0) {
                        score--;
                        ((TextView) findViewById(R.id.tv1)).setText("Score: " + score);
                    }

                    //buttons[firstCard.x][firstCard.y].setBackgroundDrawable(backImage);
                    revealedCards[firstCard.x][firstCard.y] = false;

                    //buttons[secondCard.x][secondCard.y].setBackgroundDrawable(backImage);
                    revealedCards[secondCard.x][secondCard.y] = false;

                }
            }



            //DETERMINES IF GAME IS OVER
            for (int i = 0; i < revealedCards.length; i++)
            {
                for (int j = 0; j < revealedCards[i].length; j++)
                {
                    if (!revealedCards[i][j])
                    {
                        gameOver = false;
                    }
                }
            }

            //HANDLES PASSING OF VALUES TO NEXT ACTIVITY AND CREATES NEW SCORE SCREEN ACTIVITY
            if (gameOver)
            {
                Intent i = new Intent(gameActivity.this,scoreScreenActivity.class);
                i.putExtra("score", score);
                i.putExtra("numWords", numWords);
                i.putExtra("playingValue", playing);
                startActivity(i);
            }
        }
    }
}
