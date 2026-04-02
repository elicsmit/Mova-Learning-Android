package com.example.mova;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mova.ui.data.models.Achievement;
import com.example.mova.ui.data.models.DataManager;
import com.example.mova.ui.data.models.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

// ProfileActivity.java // ProfileActivity.java

import androidx.appcompat.app.AlertDialog;

// ProfileActivity.java
// ProfileActivity.java

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.*;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.*;

public class ProfileActivity extends AppCompatActivity implements PersonalWordAdapter.OnWordActionListener {

    private PersonalWordAdapter personalWordAdapter;
    private RecyclerView rvPersonalDictionary;
    private ProgressManager progressManager;
    private DatabaseHelper databaseHelper;
    private int currentUserId;
    private boolean isGuest = false;

    // TextView элементы
    private TextView tvUserName, tvUserLevel, tvCurrentMonth;
    private TextView tvActiveDays, tvCurrentStreak, tvMaxStreak;
    private TextView tvLearnedWords, tvCompletedLessons, tvExperience;
    private TextView tvWordsCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Log.d("ProfileActivity", "ProfileActivity started");

        initViews();
        setupManagers();
        setupPersonalDictionary();
        setupClickListeners();
        loadUserData();
    }

    private void initViews() {
        // Инициализация всех TextView
        tvUserName = findViewById(R.id.tv_user_name);
        tvUserLevel = findViewById(R.id.tv_user_level);
        tvCurrentMonth = findViewById(R.id.tv_current_month);

        tvActiveDays = findViewById(R.id.tv_active_days);
        tvCurrentStreak = findViewById(R.id.tv_current_streak);
        tvMaxStreak = findViewById(R.id.tv_max_streak);
        tvLearnedWords = findViewById(R.id.tv_learned_words);
        tvCompletedLessons = findViewById(R.id.tv_completed_lessons);
        tvExperience = findViewById(R.id.tv_experience);

        tvWordsCount = findViewById(R.id.tv_words_count);
        rvPersonalDictionary = findViewById(R.id.rv_personal_dictionary);

        // Кнопка назад
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> onBackPressed());
    }

    private void setupManagers() {
        databaseHelper = new DatabaseHelper(this);
        progressManager = ProgressManager.getInstance(this);

        // Получаем ID текущего пользователя
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        currentUserId = prefs.getInt("currentUserId", -1);
        isGuest = prefs.getBoolean("isGuest", false);

        Log.d("ProfileActivity", "Current User ID: " + currentUserId);
        Log.d("ProfileActivity", "Is Guest: " + isGuest);

        if (currentUserId == -1 && !isGuest) {
            // Если нет пользователя и не гость, создаем гостя
            createGuestUser();
        }
    }

    private void createGuestUser() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("currentUserId", 0);
        editor.putString("currentUserFirstName", "Госць");
        editor.putBoolean("isGuest", true);
        editor.putBoolean("isLoggedIn", true);
        editor.apply();

        currentUserId = 0;
        isGuest = true;

        Log.d("ProfileActivity", "Guest user created");
    }

    private void setupPersonalDictionary() {
        personalWordAdapter = new PersonalWordAdapter(new ArrayList<>(), this);
        rvPersonalDictionary.setLayoutManager(new LinearLayoutManager(this));
        rvPersonalDictionary.setAdapter(personalWordAdapter);
    }

    private void setupClickListeners() {
        Button btnViewAllWords = findViewById(R.id.btn_view_all_words);
        btnViewAllWords.setOnClickListener(v -> openPersonalDictionary());

        // Кнопка редактирования имени (скрываем для гостя)
        ImageButton btnEditName = findViewById(R.id.btn_edit_name);
        if (isGuest) {
            btnEditName.setVisibility(View.GONE);
        } else {
            btnEditName.setOnClickListener(v -> showEditNameDialog());
        }

        // Кнопка редактирования фото
        ImageButton btnEditPhoto = findViewById(R.id.btn_edit_photo);
        btnEditPhoto.setOnClickListener(v -> {
            Toast.makeText(this, "Функцыя змены фота будзе даступная ў будучых абнаўленнях", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadUserData() {
        if (isGuest) {
            showGuestData();
        } else {
            loadUserInfo();
        }

        loadStreakStats();
        loadUserProgress();
        loadRecentWords();
        loadAchievements();
    }

    private void showGuestData() {
        tvUserName.setText("Госць");
        tvUserLevel.setText("🎓 Узровень: Госць");
        tvCurrentMonth.setText("📅 " + getCurrentMonth());

        // Все данные нулевые для гостя
        updateStatsUI(0, 0, 0);
        tvWordsCount.setText("0 словаў");

        // Стрики тоже нулевые
        tvActiveDays.setText("0");
        tvCurrentStreak.setText("0");
        tvMaxStreak.setText("0");
    }

    private void loadUserInfo() {
        new Thread(() -> {
            try {
                User user = databaseHelper.getUserById(currentUserId);
                runOnUiThread(() -> {
                    if (user != null) {
                        // Используем реальное имя пользователя
                        String userName = user.getFirstName();
                        if (userName == null || userName.isEmpty()) {
                            userName = "Міхась";
                        }

                        tvUserName.setText(userName);
                        tvCurrentMonth.setText("📅 " + getCurrentMonth());
                    } else {
                        showDefaultUserInfo();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(this::showDefaultUserInfo);
            }
        }).start();
    }

    private void showDefaultUserInfo() {
        tvUserName.setText("Міхась");
        tvCurrentMonth.setText("📅 " + getCurrentMonth());
    }

    private void loadStreakStats() {
        new Thread(() -> {
            try {
                // ВРЕМЕННО: ВОЗВРАЩАЕМ НУЛЕВЫЕ ЗНАЧЕНИЯ
                runOnUiThread(() -> {
                    tvActiveDays.setText("0");
                    tvCurrentStreak.setText("0");
                    tvMaxStreak.setText("0");
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    tvActiveDays.setText("0");
                    tvCurrentStreak.setText("0");
                    tvMaxStreak.setText("0");
                });
            }
        }).start();
    }

    private void loadUserProgress() {
        new Thread(() -> {
            try {
                // ВРЕМЕННО: ВОЗВРАЩАЕМ НУЛЕВЫЕ ЗНАЧЕНИЯ
                runOnUiThread(() -> {
                    updateStatsUI(0, 0, 0);
                    updateUserLevel(0);
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    updateStatsUI(0, 0, 0);
                    updateUserLevel(0);
                });
            }
        }).start();
    }

    private void updateStatsUI(int learnedWords, int completedLessons, int experience) {
        tvLearnedWords.setText(String.valueOf(learnedWords));
        tvCompletedLessons.setText(String.valueOf(completedLessons));
        tvExperience.setText(String.valueOf(experience));
    }

    private void updateUserLevel(int totalXp) {
        String level;
        if (totalXp >= 2000) {
            level = "Прасунуты";
        } else if (totalXp >= 1000) {
            level = "Сярэдні";
        } else if (totalXp >= 500) {
            level = "Пачатковы";
        } else {
            level = "Новачок";
        }

        tvUserLevel.setText("🎓 Узровень: " + level);
    }

    private void loadRecentWords() {
        new Thread(() -> {
            try {
                // ВРЕМЕННО: ПУСТОЙ СПИСОК СЛОВ
                List<Word> recentWords = new ArrayList<>();

                runOnUiThread(() -> {
                    personalWordAdapter.updateWords(recentWords);
                    tvWordsCount.setText("0 словаў");
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    tvWordsCount.setText("0 словаў");
                });
            }
        }).start();
    }

    private void loadAchievements() {
        new Thread(() -> {
            try {
                DataManager dataManager = DataManager.getInstance(this);
                List<Achievement> achievements = dataManager.getAchievements();

                // ВРЕМЕННО: ВСЕ ДОСТИЖЕНИЯ НЕ РАЗБЛОКИРОВАНЫ
                for (Achievement achievement : achievements) {
                    achievement.setUnlocked(false);
                }

                runOnUiThread(() -> {
                    GridView gridView = findViewById(R.id.gv_achievements);
                    AchievementAdapter adapter = new AchievementAdapter(this, achievements);
                    gridView.setAdapter(adapter);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private String getCurrentMonth() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", new Locale("be"));
        return sdf.format(new Date());
    }

    private void showEditNameDialog() {
        String currentName = tvUserName.getText().toString();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Змяніць імя");

        final EditText input = new EditText(this);
        input.setText(currentName);
        input.setSelectAllOnFocus(true);
        builder.setView(input);

        builder.setPositiveButton("Захаваць", (dialog, which) -> {
            String newName = input.getText().toString().trim();
            if (!newName.isEmpty() && !newName.equals(currentName)) {
                updateUserName(newName);
            }
        });

        builder.setNegativeButton("Скасаваць", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void updateUserName(String newName) {
        if (currentUserId != -1 && !isGuest) {
            // Сохраняем новое имя в базу данных
            databaseHelper.updateUserName(currentUserId, newName);
        }

        tvUserName.setText(newName);

        // Сохраняем имя в SharedPreferences для быстрого доступа
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        prefs.edit().putString("currentUserFirstName", newName).apply();

        Toast.makeText(this, "Імя паспяхова зменена!", Toast.LENGTH_SHORT).show();
    }

    private void openPersonalDictionary() {
        startActivity(new Intent(this, PersonalDictionaryActivity.class));
    }

    // Реализация методов интерфейса PersonalWordAdapter.OnWordActionListener
    @Override
    public void onRemoveWord(Word word) {
        showRemoveWordDialog(word);
    }

    @Override
    public void onPlayAudio(Word word) {
        playWordAudio(word);
    }

    @Override
    public void onWordStatusChanged(Word word, boolean isLearned) {
        // Пока ничего не делаем при изменении статуса слова
    }

    private void showRemoveWordDialog(Word word) {
        new AlertDialog.Builder(this)
                .setTitle("Выдаліць слова")
                .setMessage("Вы ўпэўнены, што хочаце выдаліць слова \"" + word.getBelarusian() + "\" з асабістага слоўніка?")
                .setPositiveButton("Выдаліць", (dialog, which) -> removeWordFromDictionary(word))
                .setNegativeButton("Скасаваць", null)
                .show();
    }

    private void removeWordFromDictionary(Word word) {
        new Thread(() -> {
            boolean success = databaseHelper.removeWordFromPersonalDictionary(currentUserId, (int) word.getId());
            runOnUiThread(() -> {
                if (success) {
                    Toast.makeText(this, "Слова выдалена", Toast.LENGTH_SHORT).show();
                    loadRecentWords();
                } else {
                    Toast.makeText(this, "Памылка выдалення", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private void playWordAudio(Word word) {
        // Воспроизведение аудио слова
        Toast.makeText(this, "Аўдыё: " + word.getBelarusian(), Toast.LENGTH_SHORT).show();
        // TODO: Реализовать логику воспроизведения аудио
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Обновляем данные при возвращении на экран
        if (currentUserId != -1) {
            loadUserData();
        }
    }
}