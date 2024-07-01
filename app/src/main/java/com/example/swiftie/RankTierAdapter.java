package com.example.swiftie;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class RankTierAdapter extends BaseAdapter {
    private Context context;
    private List<RankTier> rankTiers;

    public RankTierAdapter(Context context, List<RankTier> rankTiers) {
        this.context = context;
        this.rankTiers = rankTiers;
    }

    @Override
    public int getCount() {
        return rankTiers.size();
    }

    @Override
    public Object getItem(int position) {
        return rankTiers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.rank_user, parent, false);
        }

        RankTier rankTier = rankTiers.get(position);

        TextView rankIndex = convertView.findViewById(R.id.rankIndex);
        TextView rankName = convertView.findViewById(R.id.rankName);
        TextView rankPoints = convertView.findViewById(R.id.rankPoints);

        rankIndex.setText(position + 1 + ". ");
        rankName.setText(rankTier.getRankName());
        rankPoints.setText(String.valueOf(rankTier.getRequiredPoints()));

        if (rankTier.isCurrentRank()) {
            int highlightColor = context.getResources().getColor(R.color.TextPink);
            rankIndex.setTextColor(highlightColor);
            rankName.setTextColor(highlightColor);
            rankPoints.setTextColor(highlightColor);
        } else {
            int defaultColor = context.getResources().getColor(android.R.color.black);
            rankIndex.setTextColor(defaultColor);
            rankName.setTextColor(defaultColor);
            rankPoints.setTextColor(defaultColor);
        }

        return convertView;
    }
}


class RankTier {
    private String rankName;
    private int requiredPoints;
    private boolean isCurrentRank;

    public RankTier(String rankName, int requiredPoints, boolean isCurrentRank) {
        this.rankName = rankName;
        this.requiredPoints = requiredPoints;
        this.isCurrentRank = isCurrentRank;
    }

    public String getRankName() {
        return rankName;
    }

    public int getRequiredPoints() {
        return requiredPoints;
    }

    public boolean isCurrentRank() {
        return isCurrentRank;
    }

    public void setCurrentRank(boolean currentRank) {
        isCurrentRank = currentRank;
    }
}
