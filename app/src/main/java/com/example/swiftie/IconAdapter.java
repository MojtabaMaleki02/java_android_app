package com.example.swiftie;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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
        TextView unownedTextView = convertView.findViewById(R.id.unownedTextView);

        int icon = icons[position];
        imageView.setImageResource(icon);

        if (unlockedIcons.contains(icon)) {
            darkOverlay.setVisibility(View.GONE);
            unownedTextView.setVisibility(View.GONE);
        } else {
            darkOverlay.setVisibility(View.VISIBLE);
            unownedTextView.setVisibility(View.VISIBLE);
        }

        return convertView;
    }
}
