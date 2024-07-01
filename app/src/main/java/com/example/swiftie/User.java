package com.example.swiftie;

public class User {
    private String username;
    private int points;
    private int rank; // Add rank field

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, int points) {
        this.username = username;
        this.points = points;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    // Add setRank method if necessary
    public void setRank(int rank) {
        this.rank = rank;
    }

    // Add getRank method
    public int getRank() {
        return rank;
    }
}
