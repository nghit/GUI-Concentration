/***************************************************************
 * file: MusicService.java
 * author: Team Dream Crushers
 * class: CS 2450 - Programming Graphical User Interfaces
 *
 * assignment: Android Studio Project
 * date last modified: 11/27/19
 *
 * purpose: Service that allows music to be played across
 * multiple activities.
 ****************************************************************/

package com.example.jason.ftp;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;


public class MusicService extends Service {
    private static final String TAG = null;
    MediaPlayer player;

    public IBinder onBind(Intent arg0) {

        return null;
    }

    //When service is created, start playing music.
    @Override
    public void onCreate() {
        super.onCreate();
        player = MediaPlayer.create(this, R.raw.background);
        player.setLooping(true);
        player.setVolume(100,100);

    }
    public int onStartCommand(Intent intent, int flags, int startId) {
        player.start();
        return 1;
    }

    public void onStart(Intent intent, int startId) {
        player.start();
    }
    public IBinder onUnBind(Intent arg0) {
        return null;
    }

    public void onStop() {
        player.stop();
    }
    public void onPause() {
        player.stop();
    }


    //When app is destroyed, stop and release music.
    @Override
    public void onDestroy() {
        player.stop();
        player.release();
    }

    //optional override method
    @Override
    public void onLowMemory() {

    }
}
