package com.example.mova;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.List;

public class ArticleListAdapter extends ArrayAdapter<String> {

    private Context context;
    private String[] titles;
    private String[] contents;

    public ArticleListAdapter(Context context, String[] titles, String[] contents) {
        super(context, R.layout.item_article_list, titles);
        this.context = context;
        this.titles = titles;
        this.contents = contents;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_article_list, parent, false);
            holder = new ViewHolder();
            holder.tvArticleTitle = convertView.findViewById(R.id.tvArticleTitle);
            holder.ivReadStatus = convertView.findViewById(R.id.ivReadStatus);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String title = titles[position];
        holder.tvArticleTitle.setText(title);

        // Проверяем статус прочтения
        checkArticleReadStatus(title, holder.ivReadStatus);

        return convertView;
    }

    private void checkArticleReadStatus(String articleTitle, ImageView ivReadStatus) {
        SharedPreferences prefs = context.getSharedPreferences("articles", Context.MODE_PRIVATE);
        boolean isRead = prefs.getBoolean(articleTitle, false);

        if (isRead) {
            ivReadStatus.setImageResource(R.drawable.ic_eye_open);
            ivReadStatus.setColorFilter(ContextCompat.getColor(context, R.color.green_secondary));
        } else {
            ivReadStatus.setImageResource(R.drawable.ic_eye_closed);
            ivReadStatus.setColorFilter(ContextCompat.getColor(context, R.color.red_primary));
        }
    }

    public String getContent(int position) {
        return contents[position];
    }

    private static class ViewHolder {
        TextView tvArticleTitle;
        ImageView ivReadStatus;
    }
}