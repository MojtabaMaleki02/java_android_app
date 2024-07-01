package com.example.swiftie;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.Set;

public class IconAdapter extends BaseAdapter {
    private Context context;
    private int[] icons;
    private Set<Integer> unlockedIcons;

    public IconAdapter(Context context, int[] icons, Set<Integer> unlockedIcons) {
        this.context = context;
        this.icons = icons;
        this.unlockedIcons = unlockedIcons;
    }

    @Override
    public int getCount() {
        return icons.length;
    }

    @Override
    public Object getItem(int position) {
        return icons[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_icon, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.iconImageView);
        View darkOverlay = convertView.findViewById(R.id.darkOverlay);
        ImageView lockImageView = convertView.findViewById(R.id.lockImageView);

        int icon = icons[position];
        imageView.setImageResource(icon);

        if (unlockedIcons.contains(icon)) {
            darkOverlay.setVisibility(View.GONE);
            lockImageView.setVisibility(View.GONE);
        } else {
            darkOverlay.setVisibility(View.VISIBLE);
            lockImageView.setVisibility(View.VISIBLE);
        }

        return convertView;
    }
}
