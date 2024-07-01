package com.example.swiftie;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class UserListAdapter extends ArrayAdapter<User> {
    private Context context;
    private List<User> users;
    private String currentUsername;

    public UserListAdapter(Context context, List<User> users) {
        super(context, R.layout.item_user, users);
        this.context = context;
        this.users = users;

        // Retrieve current username from SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        this.currentUsername = sharedPreferences.getString("username", "");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        }

        User user = users.get(position);

        TextView textRank = convertView.findViewById(R.id.textRank);
        TextView textUsername = convertView.findViewById(R.id.textUsername);
        TextView textPoints = convertView.findViewById(R.id.textPoints);
        LinearLayout textRankLinearLayout = convertView.findViewById(R.id.textRankLinearLayout);

        // Set rank number
        textRank.setText(position + 1 + ". ");

        textUsername.setText(user.getUsername());
        textPoints.setText(String.valueOf(user.getPoints()));

        // Check if the username matches the current user's username
        if (user.getUsername().equals(currentUsername)) {
            textUsername.setTextColor(context.getResources().getColor(R.color.TextPink));
            textUsername.setTypeface(null, Typeface.BOLD);
        } else {
            textUsername.setTextColor(context.getResources().getColor(R.color.black));
            textUsername.setTypeface(null, Typeface.NORMAL);
        }

        return convertView;
    }
}
