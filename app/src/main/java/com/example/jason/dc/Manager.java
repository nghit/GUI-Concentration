/***************************************************************
 * file: Manager.java
 * author: Team Dream Crushers
 * class: CS 2450 - Programming Graphical User Interfaces
 *
 * assignment: Android Studio Project
 * date last modified: 11/27/19
 *
 * purpose: The menu screen activity that consists of navigation
 * between the high score activity and the gameActivity.
 ****************************************************************/

package com.example.jason.ftp;

import java.util.List;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.DialogFragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;

public class Manager extends Activity {

	private static Object lock = new Object();

	private TableLayout mainTable;
	TableLayout myLayout;
	AnimationDrawable animationDrawable;
	Animation frombottom;
	Animation fromtop;
	Button playButton;
	Button hsButton;
	Button disableMusic;
	TextView mainTitle;
	Intent svc;
	public boolean playing;


	//when activity is created, perform operations.
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		playing = true;
		Intent intent = getIntent();

		Bundle extras = intent.getExtras();

		svc =new Intent(this, MusicService.class);
		svc.setAction("com.example.jason.ftp.MusicService");
		startService(svc);



		setContentView(R.layout.main);

		myLayout = (TableLayout)findViewById(R.id.bg);

		animationDrawable = (AnimationDrawable)myLayout.getBackground();
		animationDrawable.setEnterFadeDuration(4000);
		animationDrawable.setExitFadeDuration(4000);
		animationDrawable.start();

		playButton = (Button)findViewById(R.id.Play);
		hsButton = (Button)findViewById(R.id.Menu);
		mainTitle = (TextView) findViewById(R.id.mainTitle);
		disableMusic = (Button) findViewById(R.id.disableMusic);

		frombottom = AnimationUtils.loadAnimation(this,R.anim.frombottom);
		fromtop = AnimationUtils.loadAnimation(this,R.anim.fromtop);

		playButton.setAnimation(frombottom);
		hsButton.setAnimation(frombottom);
		mainTitle.setAnimation(fromtop);
		disableMusic.setAnimation(frombottom);

		//checks if music is playing or not to set the text button
		if(extras !=null){
			if (extras.containsKey("playingValue")) {
				boolean isNew = extras.getBoolean("playingValue");
				if(isNew){

					disableMusic.setText("Disable Music");
					playing = true;
				}
				else {
					stopService(svc);
					disableMusic.setText("Enable Music");
					playing = false;
				}
			}
		}

		//DISABLE MUSIC BUTTON
		disableMusic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view)
			{

				if (playing ==true) {
					disableMusic.setText("Enable Music");
					stopService(svc);
					playing =false;
				}
				else if (playing ==false) {
					disableMusic.setText("Disable Music");
					startService(svc);
					playing = true;
				}
			}
		});

		//PLAY BUTTON
		((Button)findViewById(R.id.Play)).setOnClickListener(new OnClickListener() {

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

		//HIGH SCORE BUTTON
		((Button)findViewById(R.id.Menu)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DialogFragment dialog = new HighScoreDialogFragment();

				Bundle bundle = new Bundle();
				bundle.putBoolean("playingValue", playing);
				dialog.setArguments(bundle);

				dialog.show(getFragmentManager(), "highscore");


			}


		});


	}

	//when activity is resumed, call super
	@Override
	public void onResume()
	{
		super.onResume();
	}

	//when activity is destroyed, call super
	@Override
	public void onDestroy()
	{
		super.onDestroy();
	}

	//when backspace (ancestral nav) is pressed, remove all flags and exit.
	@Override
	public void onBackPressed() {
		stopService(svc);
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
		System.exit(0);
	}


	//when activity is paused, stop music.
	@Override
	public void onPause() {
		if (isApplicationSentToBackground(this)){
			stopService(svc);
		}
		super.onPause();
	}

	@Override
	public void onStop()
	{
		super.onStop();
	}

	//handles determining if home button is pressed
	public boolean isApplicationSentToBackground(final Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
		if (!tasks.isEmpty()) {
			ComponentName topActivity = tasks.get(0).topActivity;
			if (!topActivity.getPackageName().equals(context.getPackageName())) {
				return true;
			}
		}
		return false;
	}



}
