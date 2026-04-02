package com.example.mova;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PersonalWordAdapter extends RecyclerView.Adapter<PersonalWordAdapter.WordViewHolder> {

    private List<Word> words;
    private OnWordActionListener actionListener;
    private DatabaseHelper databaseHelper;

    public PersonalWordAdapter(List<Word> words, OnWordActionListener actionListener) {
        this.words = words;
        this.actionListener = actionListener;
    }

    // Конструктор для совместимости
    public PersonalWordAdapter(List<com.example.mova.data.models.Word> recentWords) {
        // Конвертируем при необходимости или оставляем пустым
        this.words = java.util.Collections.emptyList();
    }

    public void setDatabaseHelper(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public interface OnWordActionListener {
        void onRemoveWord(Word word);
        void onPlayAudio(Word word);
        void onWordStatusChanged(Word word, boolean isLearned); // Новый метод для изменения статуса
    }

    public void updateWords(List<Word> newWords) {
        this.words = newWords;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_personal_word_full, parent, false);
        return new WordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WordViewHolder holder, int position) {
        Word word = words.get(position);
        holder.bind(word, position);
    }

    @Override
    public int getItemCount() {
        return words.size();
    }

    class WordViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvBelarusianWord;
        private final TextView tvRussianWord;
        private final TextView tvEnglishWord;
        private final TextView tvExample;
        private final TextView tvStatus;
        private final ImageButton btnAudio;
        private final ImageButton btnRemove;

        public WordViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBelarusianWord = itemView.findViewById(R.id.tv_belarusian_word);
            tvRussianWord = itemView.findViewById(R.id.tv_russian_word);
            tvEnglishWord = itemView.findViewById(R.id.tv_english_word);
            tvExample = itemView.findViewById(R.id.tv_example);
            tvStatus = itemView.findViewById(R.id.tv_status);
            btnAudio = itemView.findViewById(R.id.btn_audio);
            btnRemove = itemView.findViewById(R.id.btn_remove);
        }

        public void bind(Word word, int position) {
            tvBelarusianWord.setText(word.getBelarusian());
            tvRussianWord.setText(word.getRussian());
            tvEnglishWord.setText(word.getEnglish());

            if (word.getExample() != null && !word.getExample().isEmpty()) {
                tvExample.setVisibility(View.VISIBLE);
                tvExample.setText("Прыклад: " + word.getExample());
            } else {
                tvExample.setVisibility(View.GONE);
            }

            // Обновляем отображение статуса
            updateStatusDisplay(word);

            // Обработчик клика на статус - переключение между "вывучаецца" и "вывучана"
            tvStatus.setOnClickListener(v -> {
                toggleWordStatus(word, position);
            });

            // Обработчик клика на все слово (альтернативный способ переключения)
            itemView.setOnClickListener(v -> {
                toggleWordStatus(word, position);
            });

            // Кнопка аудио
            btnAudio.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onPlayAudio(word);
                }
            });

            // Кнопка удаления
            btnRemove.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onRemoveWord(word);
                }
            });
        }

        private void updateStatusDisplay(Word word) {
            if (word.isLearned()) {
                tvStatus.setText("✅ Вывучана");
                tvStatus.setTextColor(itemView.getContext().getColor(android.R.color.holo_green_dark));
                tvStatus.setBackgroundColor(itemView.getContext().getColor(android.R.color.transparent));
            } else {
                tvStatus.setText("📚 Вывучаецца");
                tvStatus.setTextColor(itemView.getContext().getColor(android.R.color.holo_orange_dark));
                tvStatus.setBackgroundColor(itemView.getContext().getColor(android.R.color.transparent));
            }
        }

        private void toggleWordStatus(Word word, int position) {
            boolean newStatus = !word.isLearned();
            word.setLearned(newStatus);

            if (databaseHelper != null) {
                boolean success = databaseHelper.updateWordLearningStatus((int) word.getId(), newStatus);
                if (success) {

                    String statusText = newStatus ? "вывучана" : "вывучаецца";
                    Toast.makeText(itemView.getContext(),
                            "Слова \"" + word.getBelarusian() + "\" цяпер " + statusText,
                            Toast.LENGTH_SHORT).show();
                } else {

                    word.setLearned(!newStatus);
                    Toast.makeText(itemView.getContext(),
                            "Не ўдалося абнавіць статус слова",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
            }


            updateStatusDisplay(word);


            notifyItemChanged(position);

            // Уведомляем активность для обновления статистики
            if (actionListener != null) {
                actionListener.onWordStatusChanged(word, newStatus);
            }
        }
    }

    // Метод для фильтрации слов
    public void filter(String text) {
        List<Word> filteredList = new ArrayList<>();
        if (text.isEmpty()) {
            filteredList.addAll(words);
        } else {
            String filterPattern = text.toLowerCase().trim();
            for (Word word : words) {
                if (word.getBelarusian().toLowerCase().contains(filterPattern) ||
                        word.getRussian().toLowerCase().contains(filterPattern) ||
                        word.getEnglish().toLowerCase().contains(filterPattern)) {
                    filteredList.add(word);
                }
            }
        }
        this.words = filteredList;
        notifyDataSetChanged();
    }

    // Метод для получения текущего списка слов
    public List<Word> getWords() {
        return words;
    }
}