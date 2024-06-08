package com.example.swiftie;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class RankOnline {

    public interface RankListener {
        void onRankReceived(int points);
        void onRankError(String errorMessage);
    }

    public void updatePoints(String username, int points) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("points", points);

        FirebaseDatabase.getInstance()
                .getReference("users")
                .child(username)
                .updateChildren(updates);
    }


    public void getPoints(String username, RankListener listener) {
        FirebaseDatabase.getInstance()
                .getReference("users")
                .child(username)
                .child("points")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Integer points = dataSnapshot.getValue(Integer.class);
                        if (points != null) {
                            listener.onRankReceived(points);
                        } else {
                            listener.onRankError("Failed to retrieve points for user: " + username);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        listener.onRankError(databaseError.getMessage());
                    }
                });
    }

    public void getAllUsers() {
        Query query = FirebaseDatabase.getInstance().getReference("users").orderByChild("points");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("UserList", "Retrieved User List:");
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String username = userSnapshot.getKey();
                    int points = userSnapshot.child("points").getValue(Integer.class);
                    Log.d("UserList", "User: " + username + ", Points: " + points);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("UserList", "Error retrieving user list: " + databaseError.getMessage());
            }
        });
    }
}
