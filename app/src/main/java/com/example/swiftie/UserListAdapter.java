package com.example.swiftie;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class UserListAdapter extends ArrayAdapter<User> {
    private Context context;
    private List<User> users;

    public UserListAdapter(Context context, List<User> users) {
        super(context, R.layout.item_user, users);
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        }

        User user = users.get(position);

        TextView textRank = convertView.findViewById(R.id.textRank);
        TextView textUsername = convertView.findViewById(R.id.textUsername);
        TextView textPoints = convertView.findViewById(R.id.textPoints);

        // Set rank number
        textRank.setText(String.valueOf(position + 1)+". ");

        textUsername.setText(user.getUsername());
        textPoints.setText(String.valueOf(user.getPoints()));

        return convertView;
    }
}
