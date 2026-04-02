package com.example.mova;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class PersonalDictionaryActivity extends AppCompatActivity {

    private RecyclerView rvWords;
    private WordAdapter adapter;
    private List<Word> wordList;
    private DatabaseHelper databaseHelper;
    private TextView tvEmptyState, tvTotalWords, tvLearnedWords, tvLearningWords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_dictionary);

        databaseHelper = new DatabaseHelper(this);
        initializeViews();
        setupRecyclerView();
        loadPersonalDictionaryWords();
    }

    private void initializeViews() {
        rvWords = findViewById(R.id.rvWords);
        tvEmptyState = findViewById(R.id.tvEmptyState);

        // Находим TextView для статистики
        tvTotalWords = findViewById(R.id.tvTotalWords);
        tvLearnedWords = findViewById(R.id.tvLearnedWords);
        tvLearningWords = findViewById(R.id.tvLearningWords);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // ВРЕМЕННО ОТКЛЮЧАЕМ КНОПКИ ДОБАВЛЕНИЯ
        ImageButton btnAddWord = findViewById(R.id.btnAddWord);
        btnAddWord.setVisibility(View.GONE);

        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        fabAdd.setVisibility(View.GONE);
    }

    private void setupRecyclerView() {
        wordList = new ArrayList<>();
        adapter = new WordAdapter(wordList);
        rvWords.setLayoutManager(new LinearLayoutManager(this));
        rvWords.setAdapter(adapter);
    }

    private void loadPersonalDictionaryWords() {
        new Thread(() -> {
            try {
                // Сначала проверим структуру таблицы
                databaseHelper.debugTableStructure();

                List<Word> personalWords = databaseHelper.getPersonalDictionaryWords();

                runOnUiThread(() -> {
                    // остальной код без изменений
                    if (personalWords != null && !personalWords.isEmpty()) {
                        wordList.clear();
                        wordList.addAll(personalWords);
                        adapter.updateWordList(personalWords);
                        updateStatistics();
                        tvEmptyState.setVisibility(View.GONE);
                        rvWords.setVisibility(View.VISIBLE);
                    } else {
                        showEmptyState();
                    }
                });
            } catch (Exception e) {
                Log.e("DEBUG", "Ошибка загрузки: " + e.getMessage());
                runOnUiThread(this::showEmptyState);
            }
        }).start();
    }
    private void updateStatistics() {
        // Используем adapter.getItemCount() или wordList.size()
        int total = adapter.getItemCount(); // или wordList.size()
        int learned = 0;
        int learning = 0;

        for (Word word : wordList) {
            if (word.isLearned()) {
                learned++;
            } else {
                learning++;
            }
        }

        tvTotalWords.setText(String.valueOf(total));
        tvLearnedWords.setText(String.valueOf(learned));
        tvLearningWords.setText(String.valueOf(learning));

        Log.d("DEBUG", "Статистика: всего=" + total + ", изучено=" + learned + ", на изучении=" + learning);
    }

    private void showEmptyState() {
        tvEmptyState.setVisibility(View.VISIBLE);
        rvWords.setVisibility(View.GONE);
        tvEmptyState.setText("Словарь пуст\n\nДобавляйте слова через раздел 'Словарь' кнопкой 'Вывучыць'");

        // Обнуляем статистику
        tvTotalWords.setText("0");
        tvLearnedWords.setText("0");
        tvLearningWords.setText("0");
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPersonalDictionaryWords();
    }
}