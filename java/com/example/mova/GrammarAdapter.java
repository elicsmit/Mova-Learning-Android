package com.example.mova;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.List;

public class GrammarAdapter extends RecyclerView.Adapter<GrammarAdapter.ViewHolder> {
    private List<GrammarTopic> topics;
    private OnItemClickListener listener;

    // Добавляем интерфейс для кликов
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public GrammarAdapter(List<GrammarTopic> topics) {
        this.topics = topics;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_grammar_topic, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GrammarTopic topic = topics.get(position);
        holder.tvTitle.setText(topic.getTitle());
        holder.tvDescription.setText(topic.getDescription());

        // Устанавливаем сердечко
        int favoriteIcon = topic.isFavorite()
                ? R.drawable.ic_favorite_filled
                : R.drawable.ic_favorite_outline;
        holder.ivFavorite.setImageResource(favoriteIcon);

        // Обработчик клика на сердечко
        holder.ivFavorite.setOnClickListener(v -> {
            boolean newFavoriteState = !topic.isFavorite();
            topic.setFavorite(newFavoriteState);

            // Обновляем иконку
            int newIcon = newFavoriteState
                    ? R.drawable.ic_favorite_filled
                    : R.drawable.ic_favorite_outline;
            holder.ivFavorite.setImageResource(newIcon);

            // Уведомляем об изменении
            notifyItemChanged(position);
        });

        // Клик на всю карточку
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return topics.size();
    }

    public void updateTopics(List<GrammarTopic> newTopics) {
        this.topics.clear();
        this.topics.addAll(newTopics);
        notifyDataSetChanged();
    }

    public GrammarTopic getTopicAtPosition(int position) {
        if (position >= 0 && position < topics.size()) {
            return topics.get(position);
        }
        return null;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription;
        ImageView ivFavorite;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTopicTitle);
            tvDescription = itemView.findViewById(R.id.tvTopicDescription);
            ivFavorite = itemView.findViewById(R.id.ivFavorite);
        }
    }
}