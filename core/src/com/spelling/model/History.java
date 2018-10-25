package com.spelling.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * A history of words spelled by a student.
 * Each Student creates a History object after every game played.
 */
public class History {
    private String gamePlayed;
    private ArrayList<String> wordsSpelled;
    private Date date;
    private String timestamp;

    public History(String gamePlayed) {
        this.gamePlayed = gamePlayed;
        wordsSpelled = new ArrayList<String>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd HH:mm");
        date = new Date();
        timestamp = dateFormat.format(date);
    }

    public String getGamePlayed() {
        return gamePlayed;
    }

    public ArrayList<String> getWordsSpelled() {
        return wordsSpelled;
    }

    public Date getDate() {
        return date;
    }

    public String getDateString() {
        return timestamp;
    }

    public void addWord(String word) {
        wordsSpelled.add(word);
    }
}
