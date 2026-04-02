package com.example.mova;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mova.ui.data.models.Achievement;

import java.util.List;

public class AchievementAdapter extends BaseAdapter {
    private Context context;
    private List<Achievement> achievements;

    public AchievementAdapter(Context context, List<Achievement> achievements) {
        this.context = context;
        this.achievements = achievements;
    }

    @Override
    public int getCount() {
        return achievements.size();
    }

    @Override
    public Object getItem(int position) {
        return achievements.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_achievement, parent, false);
        }

        Achievement achievement = achievements.get(position);

        ImageView icon = convertView.findViewById(R.id.iv_achievement_icon);
        TextView name = convertView.findViewById(R.id.tv_achievement_name);
        View lockedOverlay = convertView.findViewById(R.id.locked_overlay);

        icon.setImageResource(achievement.getIconRes());
        name.setText(achievement.getName());

        if (achievement.isUnlocked()) {
            lockedOverlay.setVisibility(View.GONE);
            icon.setAlpha(1.0f);
        } else {
            lockedOverlay.setVisibility(View.VISIBLE);
            icon.setAlpha(0.3f);
        }

        return convertView;
    }
}
