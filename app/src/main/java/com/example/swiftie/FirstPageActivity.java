package com.example.swiftie;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.protobuf.StringValue;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FirstPageActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "UserPrefs";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_ICON = "icon";

    TextView textRank;
    Button btnStartQuiz;
    LinearLayout btnShowRank;
    SPCounter spCounter;
    String username;
    ImageView userIcon;
    int[] icons;
    GemCounter gemCounter;
    LinearLayout btnAddGem;

    TextView coinCounter;
    TextView rankNameTextView;
    TextView textUserNameIcon;
    TextView onlineRank;
    ImageView rankBoarder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_page);

        userIcon = findViewById(R.id.userIcon);

        btnAddGem = findViewById(R.id.btnAddGem);
        gemCounter = new GemCounter(this);

        coinCounter = findViewById(R.id.coinsCount);
        rankNameTextView = findViewById(R.id.textPoints);

        textUserNameIcon = findViewById(R.id.textUserName);

        onlineRank = findViewById(R.id.textRankTopRight);

        rankBoarder = findViewById(R.id.userBorder);

        updateGemCount();

        btnAddGem.setOnClickListener(v -> addGem());

        // Load the saved icon resource ID
        int savedIcon = getSavedIcon();
        if (savedIcon != 0) {
            userIcon.setImageResource(savedIcon);
        } else {
            userIcon.setImageResource(R.drawable.icon1); // Default icon
        }

        userIcon.setOnClickListener(v -> showIconSelectionDialog());

        textRank = findViewById(R.id.textRank);
        btnStartQuiz = findViewById(R.id.btnStartQuiz);
        btnShowRank = findViewById(R.id.btnShowRank);
        spCounter = new SPCounter(this);

        // Retrieve username from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        username = sharedPreferences.getString(KEY_USERNAME, null);

        // If username is not set, show the dialog
        if (username == null) {
            showUsernameDialog();
        } else {
            // Retrieve and display the real-time ranking
            showRealtimeRanking();
        }

        btnStartQuiz.setOnClickListener(v -> {
            Intent intent = new Intent(FirstPageActivity.this, MainActivity.class);
            startActivity(intent);
        });

        btnShowRank.setOnClickListener(v -> showUserListDialog());

        // Populate icons array with drawables prefixed with "icon"
        icons = getIconsByPrefix("icon");
    }

    private void updateGemCount() {
        int gemCount = gemCounter.getGems();
        coinCounter.setText(String.valueOf(gemCount));
    }

    private void addGem() {
        gemCounter.addGems(1);
        updateGemCount(); // Update button text after adding gem
    }

    private int getSavedIcon() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_ICON, 0);
    }

    private void saveIcon(int iconResId) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_ICON, iconResId);
        editor.apply();
    }

    private void showIconSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_icon_selection, null);
        builder.setView(dialogView);

        GridView gridViewIcons = dialogView.findViewById(R.id.gridViewIcons);

        // Retrieve unlocked icons from SharedPreferences
        Set<Integer> unlockedIcons = getUnlockedIcons();

        IconAdapter iconAdapter = new IconAdapter(this, icons, unlockedIcons);
        gridViewIcons.setAdapter(iconAdapter);

        final AlertDialog dialog = builder.create();

        gridViewIcons.setOnItemClickListener((parent, view, position, id) -> {
            int selectedIcon = icons[position];
            if (unlockedIcons.contains(selectedIcon)) {
                userIcon.setImageResource(selectedIcon);
                saveIcon(selectedIcon);
                dialog.dismiss();
            } else {
                // Show a dialog to purchase the icon
                showPurchaseIconDialog(selectedIcon, dialog);
            }
        });

        dialog.show();
    }

    private void showPurchaseIconDialog(int selectedIcon, AlertDialog parentDialog) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to purchase this icon for 10 gems?")
                .setPositiveButton("Yes", (dialog, id) -> {
                    if (gemCounter.spendGems(10)) {
                        unlockIcon(selectedIcon);
                        userIcon.setImageResource(selectedIcon);
                        saveIcon(selectedIcon);
                        updateGemCount();
                        parentDialog.dismiss();
                    } else {
                        showNotEnoughGemsDialog();
                    }
                })
                .setNegativeButton("No", (dialog, id) -> dialog.dismiss());
        builder.create().show();
    }

    private void showNotEnoughGemsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You do not have enough gems to purchase this icon.")
                .setPositiveButton("OK", (dialog, id) -> dialog.dismiss());
        builder.create().show();
    }

    private void showUsernameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View dialogView = getLayoutInflater().inflate(R.layout.dialog_username, null);
        builder.setView(dialogView);

        final EditText usernameEditText = dialogView.findViewById(R.id.usernameEditText);
        Button confirmButton = dialogView.findViewById(R.id.confirmButton);

        final AlertDialog dialog = builder.create();

        confirmButton.setOnClickListener(v -> {
            String enteredUsername = usernameEditText.getText().toString().trim();
            if (!TextUtils.isEmpty(enteredUsername)) {
                checkUsernameExistence(enteredUsername, dialog);
            } else {
                usernameEditText.setError("Username cannot be empty");
            }
        });

        dialog.setCancelable(false);
        dialog.show();
    }

    private void unlockIcon(int iconResId) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        Set<String> unlockedIcons = sharedPreferences.getStringSet("unlocked_icons", new HashSet<>());
        unlockedIcons.add(String.valueOf(iconResId));
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("unlocked_icons", unlockedIcons);
        editor.apply();
    }

    private Set<Integer> getUnlockedIcons() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        Set<String> unlockedIconsStr = sharedPreferences.getStringSet("unlocked_icons", new HashSet<>());
        Set<Integer> unlockedIcons = new HashSet<>();
        for (String iconStr : unlockedIconsStr) {
            unlockedIcons.add(Integer.valueOf(iconStr));
        }
        return unlockedIcons;
    }

    private void checkUsernameExistence(final String enteredUsername, final AlertDialog dialog) {
        Query query = FirebaseDatabase.getInstance().getReference("users").orderByKey().equalTo(enteredUsername);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Username already exists
                    ((EditText) dialog.findViewById(R.id.usernameEditText)).setError("Username already taken. Please choose another one.");
                } else {
                    // Username is available
                    saveUsername(enteredUsername);
                    dialog.dismiss();
                    showRealtimeRanking();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    private void saveUsername(String enteredUsername) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USERNAME, enteredUsername);
        editor.apply();
        username = enteredUsername;
    }

    private void showRealtimeRanking() {
        Query query = FirebaseDatabase.getInstance().getReference("users").orderByChild("points");
        Rank nameOfTheRank = new Rank(spCounter.getPoints());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<User> userList = new ArrayList<>();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String user = userSnapshot.getKey();
                    int points = userSnapshot.child("points").getValue(Integer.class);
                    userList.add(new User(user, points));
                }

                // Sort the user list based on points in descending order
                Collections.sort(userList, new Comparator<User>() {
                    @Override
                    public int compare(User u1, User u2) {
                        return Integer.compare(u2.getPoints(), u1.getPoints());
                    }
                });

                // Update the textRank TextView with the rank of the current user
                int rank = 1;
                for (User user : userList) {
                    if (user.getUsername().equals(username)) {
                        StringBuilder rankMessage = new StringBuilder(nameOfTheRank.getRankName()+" "+ spCounter.getPoints());
                        textRank.setText(rankMessage);
                        textUserNameIcon.setText(username);
                        rankNameTextView.setText("You Are "+nameOfTheRank.getRankName());
                        onlineRank.setText(String.valueOf(rank));

                        if (nameOfTheRank.getRankName().toUpperCase().equals("FIREFLY")){
                            rankBoarder.setImageResource(R.drawable.firefly);
                        }else if(nameOfTheRank.getRankName().toUpperCase().equals("CORNELIA")){
                            rankBoarder.setImageResource(R.drawable.cornelia);
                        }else if(nameOfTheRank.getRankName().toUpperCase().equals("GOLDRUSH")){
                            rankBoarder.setImageResource(R.drawable.goldrush);
                        }else if(nameOfTheRank.getRankName().toUpperCase().equals("LOVER")){
                            rankBoarder.setImageResource(R.drawable.lover);
                        }else if(nameOfTheRank.getRankName().toUpperCase().equals("MAROON")){
                            rankBoarder.setImageResource(R.drawable.maroon);
                        }else if(nameOfTheRank.getRankName().toUpperCase().equals("SWIFTIE")){
                            rankBoarder.setImageResource(R.drawable.swiftie);
                        }

                        break;
                    }
                    rank++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    private void showUserListDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_user_list, null);
        builder.setView(dialogView);
        ListView listViewUsers = dialogView.findViewById(R.id.listViewUsers);
        List<User> userList = new ArrayList<>();

        Query query = FirebaseDatabase.getInstance().getReference("users").orderByChild("points");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String user = userSnapshot.getKey();
                    int points = userSnapshot.child("points").getValue(Integer.class);
                    userList.add(new User(user, points));
                }

                // Sort the user list based on points in descending order
                Collections.sort(userList, new Comparator<User>() {
                    @Override
                    public int compare(User u1, User u2) {
                        return Integer.compare(u2.getPoints(), u1.getPoints());
                    }
                });

                // Assigning ranks to users
                for (int i = 0; i < userList.size(); i++) {
                    userList.get(i).setRank(i + 1);
                }

                UserListAdapter adapter = new UserListAdapter(FirstPageActivity.this, userList);
                listViewUsers.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }



    private int[] getIconsByPrefix(String prefix) {
        List<Integer> iconList = new ArrayList<>();
        Field[] fields = R.drawable.class.getFields();
        for (Field field : fields) {
            if (field.getName().startsWith(prefix)) {
                try {
                    iconList.add(field.getInt(null));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        int[] icons = new int[iconList.size()];
        for (int i = 0; i < iconList.size(); i++) {
            icons[i] = iconList.get(i);
        }
        return icons;
    }
}
