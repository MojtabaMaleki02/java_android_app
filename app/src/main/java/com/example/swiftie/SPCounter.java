package com.example.swiftie;

import android.content.Context;
import android.content.SharedPreferences;

class SPCounter {

    private int points;
    private int skillRating;
    private SharedPreferences sharedPreferences;

    public SPCounter(Context context) {
        sharedPreferences = context.getSharedPreferences("Rank", Context.MODE_PRIVATE);
        this.points = sharedPreferences.getInt("points", 0);
        this.skillRating = sharedPreferences.getInt("skillRating", 0);
    }

    public void increasePoints() {
        this.points += 10;
        this.skillRating += 5;
        saveRank();
    }

    public void decreasePoints() {
        this.points = Math.max(0, this.points - 5);
        this.skillRating = Math.max(0, this.skillRating - 2);
        saveRank();
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getSkillRating() {
        return skillRating;
    }

    private void saveRank() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("points", points);
        editor.putInt("skillRating", skillRating);
        editor.apply();
    }
}
