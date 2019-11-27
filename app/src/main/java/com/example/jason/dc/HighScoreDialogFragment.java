/***************************************************************
 * file: HighScoreDialogFragment.java
 * author: Team Dream Crushers
 * class: CS 2450 - Programming Graphical User Interfaces
 *
 * assignment: Android Studio Project
 * date last modified: 11/27/19
 *
 * purpose: The dialog fragment when high score is clicked.
 * Allows the user to choose which high score they want displayed
 * based on the number of words.
 ****************************************************************/

package com.example.jason.ftp;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

public class HighScoreDialogFragment extends DialogFragment
{
    //overidden method that pops up dialog
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

                        Intent nextAct = new Intent(getActivity(), HSActivity.class);
                        nextAct.putExtra("numWords", Integer.parseInt(optionsArray[i]));        //pass extras for num words and music is playing boolean
                        nextAct.putExtra("playingValue",getArguments().getBoolean("playingValue"));
                        startActivity(nextAct);
                    }
                })
                .setNegativeButton(R.string.cancelButton, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                            //do nothing
                    }
                });

        return builder.create();
    }
}
