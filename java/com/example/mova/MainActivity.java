package com.example.mova;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.graphics.Color;
import android.view.View;
import android.widget.Toast;

import com.example.mova.ArticlesActivity;
import com.example.mova.DictionaryActivity;
import com.example.mova.GrammarActivity;
import com.example.mova.ProfileActivity;
import com.example.mova.TrainingActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ImageButton btnProfile;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "=== MAIN ACTIVITY STARTED ===");

        initViews();
        setupClickListeners();
        startBackgroundMusic();
    }

    private void initViews() {
        btnProfile = findViewById(R.id.btnProfile);

        // Получаем ID пользователя если есть
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        currentUserId = prefs.getInt("currentUserId", -1);

        Log.d(TAG, "Current user ID: " + currentUserId);
    }

    private void setupClickListeners() {
        btnProfile.setOnClickListener(v -> {
            Log.d(TAG, "Profile button clicked");
            startActivity(new Intent(this, ProfileActivity.class));
        });

        // Карточка словаря
        findViewById(R.id.cardDictionary).setOnClickListener(v -> {
            startActivity(new Intent(this, DictionaryActivity.class));
        });

        // Карточка грамматики
        findViewById(R.id.cardGrammar).setOnClickListener(v -> {
            startActivity(new Intent(this, GrammarActivity.class));
        });

        // Карточка статей
        findViewById(R.id.cardArticles).setOnClickListener(v -> {
            startActivity(new Intent(this, ArticlesActivity.class));
        });

        // Карточка тренировок
        findViewById(R.id.cardTraining).setOnClickListener(v -> {
            startActivity(new Intent(this, TrainingActivity.class));
        });

        // Настройки звука
        findViewById(R.id.btnSoundSettings).setOnClickListener(v -> {
            SoundSettingsDialog dialog = new SoundSettingsDialog(MainActivity.this);
            dialog.show();
        });
    }

    private void startBackgroundMusic() {
        SharedPreferences prefs = getSharedPreferences("MusicPrefs", MODE_PRIVATE);
        boolean isMusicEnabled = prefs.getBoolean("music_enabled", true);

        if (isMusicEnabled) {
            Intent musicIntent = new Intent(this, MusicService.class);
            musicIntent.putExtra("action", "play");
            startService(musicIntent);
        }
    }
}