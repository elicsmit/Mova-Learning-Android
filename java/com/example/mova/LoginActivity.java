package com.example.mova;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mova.ui.data.models.User;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Log.d(TAG, "=== LOGIN ACTIVITY STARTED ===");

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> {
            hideKeyboard();
            attemptLogin();
        });

        // Кнопка назад
        findViewById(R.id.btnBack).setOnClickListener(v -> onBackPressed());

        // Регистрация
        findViewById(R.id.btnRegister).setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        });
    }

    private void attemptLogin() {
        resetErrors();

        String email = etEmail.getText().toString().trim().toLowerCase();
        String password = etPassword.getText().toString();

        boolean hasError = false;

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Увядзіце email адрас");
            hasError = true;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Увядзіце правільны email адрас");
            hasError = true;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Увядзіце пароль");
            hasError = true;
        }

        if (!hasError) {
            startSecureLogin(email, password);
        }
    }

    private void startSecureLogin(String email, String password) {
        showProgress(true);

        new Thread(() -> {
            try {
                // Имитация сетевого запроса
                Thread.sleep(1500);

                runOnUiThread(() -> {
                    authenticateUser(email, password);
                });
            } catch (InterruptedException e) {
                runOnUiThread(() -> {
                    showProgress(false);
                    Toast.makeText(LoginActivity.this, "Памылка аўтэнтыфікацыі", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void authenticateUser(String email, String password) {
        try {
            DatabaseHelper dbHelper = new DatabaseHelper(this);

            // Сначала получаем соль пользователя из базы данных
            String userSalt = dbHelper.getUserSalt(email);
            if (userSalt == null) {
                showProgress(false);
                etEmail.setError("Карыстальнік не знойдзены");
                Log.d(TAG, "✗ User not found: " + email);
                return;
            }

            // Хешируем введенный пароль с правильной солью
            byte[] salt = Base64.decode(userSalt, Base64.NO_WRAP);
            String inputHashedPassword = hashPassword(password, salt);

            // Аутентифицируем пользователя
            User user = dbHelper.authenticateUser(email, inputHashedPassword);

            if (user != null) {
                Log.d(TAG, "✓ Authentication successful for: " + email);
                loginUserSuccessfully(user);
            } else {
                showProgress(false);
                etPassword.setError("Няправільны email або пароль");
                Log.d(TAG, "✗ Authentication failed for: " + email);
            }

        } catch (Exception e) {
            Log.e(TAG, "Authentication error", e);
            showProgress(false);
            Toast.makeText(this, "Памылка аўтэнтыфікацыі", Toast.LENGTH_SHORT).show();
        }
    }

    private void loginUserSuccessfully(User user) {
        Log.d(TAG, "=== LOGIN SUCCESSFUL ===");

        // Сохраняем данные пользователя
        saveCurrentUser(user);

        // Обновляем статистику входа
        updateLoginStats();

        showProgress(false);
        Toast.makeText(this, "Уваход паспяховы! Вітаем, " + user.getFirstName() + "!", Toast.LENGTH_SHORT).show();

        // Переходим на главный экран
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void saveCurrentUser(User user) {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // ОЧИЩАЕМ ПРЕЖДЕ ЧЕМ СОХРАНИТЬ НОВЫЕ ДАННЫЕ
        editor.clear();

        // Сохраняем с правильными ключами
        editor.putInt("currentUserId", user.getId());
        editor.putString("currentUserEmail", user.getEmail());
        editor.putString("currentUserFirstName", user.getFirstName());
        editor.putBoolean("isLoggedIn", true);
        editor.putLong("lastLogin", System.currentTimeMillis());

        // Используем commit() для немедленного сохранения
        boolean success = editor.commit();

        Log.d(TAG, "=== SAVING USER TO SHAREDPREFS ===");
        Log.d(TAG, "User ID: " + user.getId());
        Log.d(TAG, "User Name: " + user.getFirstName());
        Log.d(TAG, "Save successful: " + success);
        Log.d(TAG, "All prefs after save: " + prefs.getAll().toString());
    }

    private String hashPassword(String password, byte[] salt) throws Exception {
        javax.crypto.spec.PBEKeySpec spec = new javax.crypto.spec.PBEKeySpec(
                password.toCharArray(), salt, 65536, 256);
        javax.crypto.SecretKeyFactory factory = javax.crypto.SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] hash = factory.generateSecret(spec).getEncoded();
        return Base64.encodeToString(hash, Base64.NO_WRAP);
    }

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!show);
    }

    private void resetErrors() {
        etEmail.setError(null);
        etPassword.setError(null);
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void updateLoginStats() {
        SharedPreferences statsPrefs = getSharedPreferences("UserStats", MODE_PRIVATE);
        int streak = statsPrefs.getInt("streak_days", 0);
        long lastLogin = statsPrefs.getLong("last_login_date", 0);

        // Логика подсчета дней подряд
        SharedPreferences.Editor editor = statsPrefs.edit();
        editor.putInt("streak_days", streak + 1);
        editor.putLong("last_login_date", System.currentTimeMillis());
        editor.apply();
    }
}