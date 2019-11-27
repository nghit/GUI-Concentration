/***
 * file: Card.java
 * author: Team FTP
 * class: CS 245 - Programming Graphical User Interfaces
 *
 * assignment: Android Studio Project
 * date last modified: 12/5/17
 *
 * purpose: This is the basic class that allows us to manage the cards during the memory game
 *
 **/

package com.example.jason.ftp;

import android.widget.Button;

import java.io.Serializable;

public class Card implements Serializable{

	public int x;
	public int y;
	public transient Button button; //used to select the card on screen
	
	public Card(Button button, int x,int y) { //each card has a button and x and y coords
		this.x = x;
		this.y=y;
		this.button=button;
	}
}
