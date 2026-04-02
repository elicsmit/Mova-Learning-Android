package com.example.mova;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class WordAdapter extends RecyclerView.Adapter<WordAdapter.WordViewHolder> {

    private final List<Word> wordList;
    private final List<Word> wordListFiltered;

    public WordAdapter(List<Word> wordList) {
        this.wordList = wordList;
        this.wordListFiltered = new ArrayList<>(wordList);
    }

    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_word, parent, false);
        return new WordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WordViewHolder holder, int position) {
        Word word = wordListFiltered.get(position);
        holder.bind(word);
    }

    @Override
    public int getItemCount() {
        return wordListFiltered.size();
    }

    public static class WordViewHolder extends RecyclerView.ViewHolder {
        private final TextView belarusianWord;
        private final TextView russianWord;
        private final TextView englishWord;
        private final ImageView wordImage;
        private final Button btnLearn;
        private final ImageButton btnAudio;

        public WordViewHolder(@NonNull View itemView) {
            super(itemView);
            belarusianWord = itemView.findViewById(R.id.belarusianWord);
            russianWord = itemView.findViewById(R.id.russianWord);
            englishWord = itemView.findViewById(R.id.englishWord);
            wordImage = itemView.findViewById(R.id.wordImage);
            btnLearn = itemView.findViewById(R.id.btnLearn);
            btnAudio = itemView.findViewById(R.id.btnAudio);
        }

        public void bind(Word word) {
            // Установка текста
            belarusianWord.setText(word.getBelarusian());
            russianWord.setText(word.getRussian());
            englishWord.setText(word.getEnglish());

            // Загрузка картинки
            loadWordImage(word);

            // Обработчик клика на весь item
            itemView.setOnClickListener(v -> showWordDetails(word));

            // Кнопка изучения слова
            if (btnLearn != null) {
                btnLearn.setOnClickListener(v -> markWordAsLearned(word));
            }

            // Кнопка аудио
            if (btnAudio != null) {
                btnAudio.setOnClickListener(v -> playWordAudio(word));
            }
        }

        private void loadWordImage(Word word) {
            // TODO: Реализовать загрузку картинок для слов
            // Временная реализация - используем дефолтную картинку
            wordImage.setImageResource(R.drawable.default_word);
        }

        private void showWordDetails(Word word) {
            // Показать детали слова
            Toast.makeText(itemView.getContext(),
                    "Детали слова: " + word.getBelarusian() +
                            "\nРусский: " + word.getRussian() +
                            "\nАнглийский: " + word.getEnglish(),
                    Toast.LENGTH_SHORT).show();
        }

        private void markWordAsLearned(Word word) {
            // Отметить слово как изученное
            Toast.makeText(itemView.getContext(),
                    "Слова \"" + word.getBelarusian() + "\" вывучана!",
                    Toast.LENGTH_SHORT).show();

            // TODO: Добавить логику обновления статуса слова в базе данных
            // Например:
            // word.setLearned(true);
            // DatabaseHelper.markWordAsLearned(word.getId());
        }

        private void playWordAudio(Word word) {
            // Воспроизвести аудио слова
            Toast.makeText(itemView.getContext(),
                    "Воспроизведение аудио для: " + word.getBelarusian(),
                    Toast.LENGTH_SHORT).show();

            // TODO: Реализовать логику воспроизведения аудио
            // Например:
            // AudioManager.playWordAudio(word.getBelarusian());
        }
    }

    // Метод для фильтрации слов
    @SuppressLint("NotifyDataSetChanged")
    public void filter(String text) {
        wordListFiltered.clear();
        if (text.isEmpty()) {
            wordListFiltered.addAll(wordList);
        } else {
            String filterPattern = text.toLowerCase().trim();
            for (Word word : wordList) {
                if (word.getBelarusian().toLowerCase().contains(filterPattern) ||
                        word.getRussian().toLowerCase().contains(filterPattern) ||
                        word.getEnglish().toLowerCase().contains(filterPattern)) {
                    wordListFiltered.add(word);
                }
            }
        }
        notifyDataSetChanged();
    }

    // Метод для обновления списка слов
    // В WordAdapter
    @SuppressLint("NotifyDataSetChanged")
    public void updateWordList(List<Word> newWords) {
        wordList.clear();
        wordList.addAll(newWords);
        notifyDataSetChanged();
    }

    // Метод для получения слова по позиции
    public Word getWordAtPosition(int position) {
        if (position >= 0 && position < wordListFiltered.size()) {
            return wordListFiltered.get(position);
        }
        return null;
    }
}

