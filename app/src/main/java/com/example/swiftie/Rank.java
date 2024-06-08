package com.example.swiftie;

public class Rank {
    private int SPPoint;

    public Rank(int SPPoint) {
        this.SPPoint = SPPoint;
    }

    public String getRankName() {
        if (SPPoint >= 0 && SPPoint <= 499) {
            return "Firefly";
        } else if (SPPoint >= 500 && SPPoint <= 999) {
            return "Cornelia";
        } else if (SPPoint >= 1000 && SPPoint <= 1499) {
            return "GoldRush";
        } else if (SPPoint >= 1500 && SPPoint <= 1999) {
            return "Lover";
        } else if (SPPoint >= 2000 && SPPoint <= 2499) {
            return "Maroon";
        } else if (SPPoint >= 2500) {
            return "Swiftie";
        } else {
            return "Unknown";
        }
    }

    public void setSPPoint(int SPPoint) {
        this.SPPoint = SPPoint;
    }

    public int getSPPoint() {
        return SPPoint;
    }
}
