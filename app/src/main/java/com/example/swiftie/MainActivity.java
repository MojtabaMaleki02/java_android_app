package com.example.swiftie;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    TextView question;
    Button answerA;
    Button answerB;
    Button answerC;
    Button answerD;
    LinearLayout deactivateAnswers; // New button to deactivate incorrect answers
    SPCounter spCounter;
    ProgressBar progressBar;
    LinearLayout addTime;

    private int questionIndexToBeAsked = 0;
    private FileHelper fileHelper;
    private LoadJson loadJson;
    private LoadJson.Question questionData;
    private int initialPoints;
    private boolean isAnswered = false;
    private boolean isDeactivatedUsed = false; // New flag to track if deactivation has been used
    private CountDownTimer countDownTimer;
    private RankOnline rankOnline;
    private String username;
    private long timeLeftInMillis; // Time left in milliseconds for the current question
    private final long initialTimeInMillis = 10000; // Initial time for each question
    private final long countdownInterval = 100; // Countdown interval in milliseconds
    private GemCounter gemCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        question = findViewById(R.id.Question);
        answerA = findViewById(R.id.answerA);
        answerB = findViewById(R.id.answerB);
        answerC = findViewById(R.id.answerC);
        answerD = findViewById(R.id.answerD);
        deactivateAnswers = findViewById(R.id.btnDeactivateAnswers); // New button to deactivate incorrect answers
        progressBar = findViewById(R.id.progressBar);
        addTime = findViewById(R.id.btnAddTime);

        spCounter = new SPCounter(this);
        initialPoints = spCounter.getPoints();
        gemCounter = new GemCounter(this);

        fileHelper = new FileHelper(this);

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username", null);

        String fileContent = fileHelper.readFromFile("AskedQuestions.txt");
        if (fileContent != null && !fileContent.isEmpty()) {
            try {
                questionIndexToBeAsked = Integer.parseInt(fileContent.trim());
            } catch (NumberFormatException e) {
                Log.e("QuestionTAG", "Failed to parse question index from file: " + e.getMessage());
                questionIndexToBeAsked = 0;
            }
        }

        if (questionIndexToBeAsked > 9) {
            fileHelper.writeToFile("AskedQuestions.txt", "");
            questionIndexToBeAsked = 0;
        }

        loadJson = new LoadJson(this, questionIndexToBeAsked);
        loadNextQuestion();

        rankOnline = new RankOnline();

        View.OnClickListener answerClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isAnswered) {
                    isAnswered = true;
                    if (countDownTimer != null) {
                        countDownTimer.cancel();
                    }
                    Button clickedButton = (Button) v;
                    String selectedAnswer = clickedButton.getText().toString();

                    if (selectedAnswer.equals(questionData.options[questionData.answer])) {
                        spCounter.increasePoints();
                        Log.d("AnswerClicked", "Correct answer selected!");
                    } else {
                        spCounter.decreasePoints();
                        Log.d("AnswerClicked", "Incorrect answer selected!");
                    }

                    rankOnline.updatePoints(username, spCounter.getPoints());

                    questionIndexToBeAsked++;
                    fileHelper.writeToFile("AskedQuestions.txt", String.valueOf(questionIndexToBeAsked));

                    if (questionIndexToBeAsked >= 10) {
                        EndScreenActivity.start(MainActivity.this, initialPoints, spCounter.getPoints());
                        finish();
                    } else {
                        loadNextQuestion();
                    }

                    rankOnline.getPoints(username, new RankOnline.RankListener() {
                        @Override
                        public void onRankReceived(int points) {
                            Log.d("UserRank", "Points for user " + username + ": " + points);
                        }

                        @Override
                        public void onRankError(String errorMessage) {
                            Log.e("UserRank", errorMessage);
                        }
                    });
                }
            }
        };

        answerA.setOnClickListener(answerClickListener);
        answerB.setOnClickListener(answerClickListener);
        answerC.setOnClickListener(answerClickListener);
        answerD.setOnClickListener(answerClickListener);

        deactivateAnswers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeactivateConfirmationDialog();
            }
        });

        addTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmationDialog();
            }
        });
    }

    private void showConfirmationDialog() {
        int gemsNeeded = 1; // Assume 1 gem is needed to add time
        if (gemCounter.getGems() >= gemsNeeded) {
            new AlertDialog.Builder(this)
                    .setTitle("Add Time")
                    .setMessage("Are you sure you want to spend 1 gem to add 10 seconds?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (gemCounter.spendGems(gemsNeeded)) {
                                addTimeToTimer(10000); // Add 10 seconds
                            } else {
                                Log.d("AddTime", "Error: Not enough gems to add time.");
                            }
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        } else {
            // Show alert that there are not enough gems
            new AlertDialog.Builder(this)
                    .setTitle("Not Enough Gems")
                    .setMessage("You don't have enough gems to add time.")
                    .setPositiveButton("OK", null)
                    .show();
        }
    }

    private void showDeactivateConfirmationDialog() {
        int gemsNeeded = 1; // Assume 1 gem is needed to deactivate answers
        if (gemCounter.getGems() >= gemsNeeded) {
            new AlertDialog.Builder(this)
                    .setTitle("Deactivate Answers")
                    .setMessage("Are you sure you want to spend 1 gem to deactivate 2 incorrect answers?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (gemCounter.spendGems(gemsNeeded)) {
                                if (!isDeactivatedUsed) {
                                    deactivateTwoIncorrectAnswers();
                                    isDeactivatedUsed = true;
                                }
                            } else {
                                Log.d("DeactivateAnswers", "Error: Not enough gems to deactivate answers.");
                            }
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        } else {
            // Show alert that there are not enough gems
            new AlertDialog.Builder(this)
                    .setTitle("Not Enough Gems")
                    .setMessage("You don't have enough gems to deactivate answers.")
                    .setPositiveButton("OK", null)
                    .show();
        }
    }

    private void loadNextQuestion() {
        isAnswered = false;
        isDeactivatedUsed = false; // Reset the flag for the new question
        loadJson = new LoadJson(this, questionIndexToBeAsked);
        questionData = loadJson.loadJson();

        // Reset the buttons for the new question
        resetAnswerButtons();

        if (questionData != null) {
            question.setText(questionData.question);
            answerA.setText(questionData.options[0]);
            answerB.setText(questionData.options[1]);
            answerC.setText(questionData.options[2]);
            answerD.setText(questionData.options[3]);
        } else {
            question.setText("No more questions available.");
            answerA.setVisibility(View.GONE);
            answerB.setVisibility(View.GONE);
            answerC.setVisibility(View.GONE);
            answerD.setVisibility(View.GONE);
        }

        // Reset time left to the initial value for each new question
        timeLeftInMillis = initialTimeInMillis;
        startCountdownTimer(timeLeftInMillis);
    }

    private void resetAnswerButtons() {
        answerA.setEnabled(true);
        answerB.setEnabled(true);
        answerC.setEnabled(true);
        answerD.setEnabled(true);
    }

    private void startCountdownTimer(long millisInFuture) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        // Update the maximum value of the ProgressBar
        progressBar.setMax((int) (millisInFuture / countdownInterval));

        countDownTimer = new CountDownTimer(millisInFuture, countdownInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                int progress = (int) (millisUntilFinished / countdownInterval);
                progressBar.setProgress(progress);
            }

            @Override
            public void onFinish() {
                if (!isAnswered) {
                    isAnswered = true;
                    spCounter.decreasePoints();
                    rankOnline.updatePoints(username, spCounter.getPoints());
                    questionIndexToBeAsked++;
                    fileHelper.writeToFile("AskedQuestions.txt", String.valueOf(questionIndexToBeAsked));
                    if (questionIndexToBeAsked >= 10) {
                        EndScreenActivity.start(MainActivity.this, initialPoints, spCounter.getPoints());
                        finish();
                    } else {
                        loadNextQuestion();
                    }

                    rankOnline.getPoints(username, new RankOnline.RankListener() {
                        @Override
                        public void onRankReceived(int points) {
                            Log.d("UserRank", "Points for user " + username + ": " + points);
                        }

                        @Override
                        public void onRankError(String errorMessage) {
                            Log.e("UserRank", errorMessage);
                        }
                    });
                }
            }
        }.start();
    }

    private void addTimeToTimer(long millisToAdd) {
        timeLeftInMillis += millisToAdd;
        startCountdownTimer(timeLeftInMillis);
    }

    private void deactivateTwoIncorrectAnswers() {
        ArrayList<Button> answerButtons = new ArrayList<>();
        answerButtons.add(answerA);
        answerButtons.add(answerB);
        answerButtons.add(answerC);
        answerButtons.add(answerD);

        ArrayList<Button> incorrectButtons = new ArrayList<>();
        for (Button button : answerButtons) {
            if (!button.getText().toString().equals(questionData.options[questionData.answer])) {
                incorrectButtons.add(button);
            }
        }

        // Randomize the incorrect answers and disable the first two
        Collections.shuffle(incorrectButtons);
        incorrectButtons.get(0).setEnabled(false);
        incorrectButtons.get(1).setEnabled(false);
    }
}
