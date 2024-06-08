package com.example.swiftie;

import android.content.Context;
import android.content.SharedPreferences;

public class GemCounter {
    private static final String PREFS_NAME = "UserPrefs";
    private static final String KEY_GEMS = "gems";

    private SharedPreferences sharedPreferences;

    public GemCounter(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public int getGems() {
        return sharedPreferences.getInt(KEY_GEMS, 0);
    }

    public void addGems(int amount) {
        int currentGems = getGems();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_GEMS, currentGems + amount);
        editor.apply();
    }

    public void FirstPageActivity(int amount) {
        int currentGems = getGems();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_GEMS, currentGems - amount);
        editor.apply();
    }

    public boolean spendGems(int amount) {
        int currentGems = getGems();
        if (currentGems >= amount) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(KEY_GEMS, currentGems - amount);
            editor.apply();
            return true;
        } else {
            return false;
        }
    }

}
