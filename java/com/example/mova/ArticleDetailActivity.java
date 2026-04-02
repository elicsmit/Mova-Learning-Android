package com.example.mova;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;

public class ArticleDetailActivity extends AppCompatActivity {

    private boolean isRead = false;
    private ImageButton btnReadStatus;
    private Button btnMarkRead;
    private String articleTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        // Инициализация элементов
        ImageButton btnBack = findViewById(R.id.btnBack);
        TextView tvTitle = findViewById(R.id.tvArticleTitle);
        TextView tvContent = findViewById(R.id.tvArticleContent);
        Button btnClose = findViewById(R.id.btnClose);
        btnMarkRead = findViewById(R.id.btnMarkRead);
        btnReadStatus = findViewById(R.id.btnReadStatus);

        // Получение данных из интента
        articleTitle = getIntent().getStringExtra("title");
        String content = getIntent().getStringExtra("content");

        // Установка данных
        tvTitle.setText(articleTitle);
        tvContent.setText(content);

        // Обработчики кликов
        btnBack.setOnClickListener(v -> finish());
        btnClose.setOnClickListener(v -> finish());

        // Проверить статус прочтения
        checkArticleReadStatus();

        btnMarkRead.setOnClickListener(v -> {
            if (!isRead) {
                markAsRead();
            } else {
                showAlreadyReadMessage();
            }
        });

        btnReadStatus.setOnClickListener(v -> {
            String message = isRead ? "Прачытана" : "Не прачытана";
            Snackbar.make(v, message, Snackbar.LENGTH_SHORT).show();
        });
    }

    private void checkArticleReadStatus() {
        SharedPreferences prefs = getSharedPreferences("articles", MODE_PRIVATE);
        isRead = prefs.getBoolean(articleTitle, false);
        updateReadStatusUI();
    }

    private void markAsRead() {
        isRead = true;

        // Сохранить статус
        SharedPreferences prefs = getSharedPreferences("articles", MODE_PRIVATE);
        prefs.edit().putBoolean(articleTitle, true).apply();

        // Обновить UI
        updateReadStatusUI();

        // Показать подтверждение
        Snackbar.make(btnMarkRead, "Статья помечена как прочитанная", Snackbar.LENGTH_SHORT).show();
    }

    private void updateReadStatusUI() {
        if (isRead) {
            // Прочитано - открытый глаз и темно-зеленая кнопка
            btnReadStatus.setImageResource(R.drawable.ic_eye_open);
            btnReadStatus.setColorFilter(ContextCompat.getColor(this, R.color.green_secondary));

            btnMarkRead.setText("Прачытана ✓");
            btnMarkRead.setBackgroundResource(R.drawable.button_filled_rounded_read);
        } else {
            // Не прочитано - закрытый глаз и обычная зеленая кнопка
            btnReadStatus.setImageResource(R.drawable.ic_eye_closed);
            btnReadStatus.setColorFilter(ContextCompat.getColor(this, R.color.red_primary));

            btnMarkRead.setText("Прачытана");
            btnMarkRead.setBackgroundResource(R.drawable.button_filled_rounded);
        }
    }

    private void showAlreadyReadMessage() {
        Snackbar.make(btnMarkRead, "Вы уже прочитали эту статью", Snackbar.LENGTH_SHORT).show();
    }
}