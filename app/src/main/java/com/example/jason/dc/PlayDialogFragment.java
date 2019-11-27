/***************************************************************
 * file: PlayDialogFragment.java
 * author: Team Dream Crushers
 * class: CS 2450 - Programming Graphical User Interfaces
 *
 * assignment: Android Studio Project
 * date last modified: 11/27/19
 *
 * purpose: The dialog fragment that prompts the user
 * for the desired amount of words.
 ****************************************************************/

package com.example.jason.ftp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import com.example.jason.ftp.R;

public class PlayDialogFragment extends DialogFragment
{
    //When dialog is created
    @Override
    public Dialog onCreateDialog(Bundle savedInstancedState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.play_dialog)
                .setItems(R.array.play_dialog_string_array, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        String[] optionsArray = getResources().getStringArray(R.array.play_dialog_string_array);

                        Intent nextAct = new Intent(getActivity(), gameActivity.class);
                        nextAct.putExtra("playingValue",getArguments().getBoolean("playingValue"));  // pass extra for music
                        nextAct.putExtra("numWords", Integer.parseInt(optionsArray[i]));    //pass extra for num words
                        startActivity(nextAct);
                    }
                })
                .setNegativeButton(R.string.cancelButton, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        //do nothing when canceled.
                    }
                });

        return builder.create();
    }
}
