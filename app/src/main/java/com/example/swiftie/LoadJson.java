package com.example.swiftie;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class LoadJson {

    private Context context;
    private int questionIndexToBeAsked;
    SPCounter spCounter;

    public LoadJson(Context context, int questionIndexToBeAsked) {
        this.context = context;
        this.questionIndexToBeAsked = questionIndexToBeAsked;
    }

    public Question loadJson() {
        Question questionData = null;

        spCounter = new SPCounter(context);

        Rank rank = new Rank(spCounter.getPoints());

        try {
            InputStream inputStream = context.getAssets().open(rank.getRankName()+".json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();

            String json = new String(buffer, StandardCharsets.UTF_8);
            JSONArray jsonArray = new JSONArray(json);

            JSONObject jsonObject = jsonArray.getJSONObject(questionIndexToBeAsked);

            String question = jsonObject.getString("question");
            JSONArray optionsArray = jsonObject.getJSONArray("options");
            String[] options = new String[optionsArray.length()];
            for (int i = 0; i < optionsArray.length(); i++) {
                options[i] = optionsArray.getString(i);
            }
            int answer = jsonObject.getInt("answer");
            String difficulty = jsonObject.getString("difficulty");
            String category = jsonObject.getString("category");

            questionData = new Question(question, options, answer, difficulty, category);

            Log.e("TAG", "The Question is: " + question);

        } catch (Exception e) {
            Log.e("TAG", "LoadJson: error " + e);
        }
        return questionData;
    }

    static class Question {
        String question;
        String[] options;
        int answer;
        String difficulty;
        String category;

        public Question(String question, String[] options, int answer, String difficulty, String category) {
            this.question = question;
            this.options = options;
            this.answer = answer;
            this.difficulty = difficulty;
            this.category = category;
        }
    }
}
