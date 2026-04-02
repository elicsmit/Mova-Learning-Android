package com.example.mova;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$");

    // Ключ для шифрования (в реальном приложении должен храниться в keystore)
    private static final String ENCRYPTION_KEY = "MovaAppSecureKey2024!";

    private EditText etFirstName, etLastName, etEmail, etPassword, etConfirmPassword;
    private CheckBox cbAgreement, cbPrivacyPolicy;
    private Button btnRegister, btnLogin;
    private View btnBack;
    private ProgressBar progressBar;

    // Переменные для хранения данных для email
    private String userPlainPassword;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        setupClickListeners();

        // Защита от скриншотов (опционально)
        getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_SECURE,
                android.view.WindowManager.LayoutParams.FLAG_SECURE);
    }

    private void initViews() {
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        cbAgreement = findViewById(R.id.cbAgreement);
        cbPrivacyPolicy = findViewById(R.id.cbPrivacyPolicy);
        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLogin);
        btnBack = findViewById(R.id.btnBack);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupClickListeners() {
        // Кнопка назад
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Регистрация
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Скрываем клавиатуру при нажатии
                hideKeyboard();
                attemptRegistration();
            }
        });

        // Вход (переход на логин)
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });

        // Политика конфиденциальности
        findViewById(R.id.tvPrivacyPolicy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPrivacyPolicyDialog();
            }
        });
    }

    private void attemptRegistration() {
        // Сбрасываем ошибки
        resetErrors();

        String firstName = sanitizeInput(etFirstName.getText().toString().trim());
        String lastName = sanitizeInput(etLastName.getText().toString().trim());
        String email = etEmail.getText().toString().trim().toLowerCase();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        boolean hasError = false;

        // Проверка имени
        if (TextUtils.isEmpty(firstName)) {
            etFirstName.setError("Увядзіце ваша імя");
            hasError = true;
        } else if (firstName.length() < 2) {
            etFirstName.setError("Імя павінна быць не карацей за 2 літары");
            hasError = true;
        } else if (!isValidName(firstName)) {
            etFirstName.setError("Імя павінна ўтрымліваць толькі літары");
            hasError = true;
        }

        // Проверка фамилии
        if (TextUtils.isEmpty(lastName)) {
            etLastName.setError("Увядзіце ваша прозвішча");
            hasError = true;
        } else if (lastName.length() < 2) {
            etLastName.setError("Прозвішча павінна быць не карацей за 2 літары");
            hasError = true;
        } else if (!isValidName(lastName)) {
            etLastName.setError("Прозвішча павінна ўтрымліваць толькі літары");
            hasError = true;
        }

        // Проверка email
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Увядзіце email адрас");
            hasError = true;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Увядзіце правільны email адрас");
            hasError = true;
        } else if (isEmailRegistered(email)) {
            etEmail.setError("Гэты email ужо зарэгістраваны");
            hasError = true;
        }

        // Проверка пароля
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Увядзіце пароль");
            hasError = true;
        } else if (password.length() < MIN_PASSWORD_LENGTH) {
            etPassword.setError("Пароль павінен быць не менш за " + MIN_PASSWORD_LENGTH + " знакаў");
            hasError = true;
        } else if (!isStrongPassword(password)) {
            etPassword.setError("Пароль павінен утрымліваць літары ў верхнім і ніжнім рэгістры, лічбы і спецсімвалы");
            hasError = true;
        }

        // Проверка подтверждения пароля
        if (TextUtils.isEmpty(confirmPassword)) {
            etConfirmPassword.setError("Паўтарыце пароль");
            hasError = true;
        } else if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Паролі не супадаюць");
            hasError = true;
        }

        // Проверка соглашений
        if (!cbAgreement.isChecked()) {
            Toast.makeText(this, "Калі ласка, прыміце ўмовы выкарыстання", Toast.LENGTH_SHORT).show();
            hasError = true;
        }

        if (!cbPrivacyPolicy.isChecked()) {
            Toast.makeText(this, "Калі ласка, пазнаёмцеся з палітыкай прыватнасці", Toast.LENGTH_SHORT).show();
            hasError = true;
        }

        if (!hasError) {
            // Сохраняем пароль для отправки в email
            userPlainPassword = password;
            userEmail = email;

            // Все проверки пройдены - запускаем безопасную регистрацию
            startSecureRegistration(firstName, lastName, email, password);
        }
    }

    private void startSecureRegistration(String firstName, String lastName, String email, String password) {
        showProgress(true);

        // Имитируем безопасную регистрацию в фоновом потоке
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Задержка для имитации сетевого запроса
                    Thread.sleep(2000);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            registerUserSecurely(firstName, lastName, email, password);
                        }
                    });
                } catch (InterruptedException e) {
                    Log.e(TAG, "Registration interrupted", e);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showProgress(false);
                            Toast.makeText(RegisterActivity.this, "Памылка рэгістрацыі", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    private void registerUserSecurely(String firstName, String lastName, String email, String password) {
        try {
            DatabaseHelper dbHelper = new DatabaseHelper(this);

            // Проверяем, не зарегистрирован ли уже email
            if (dbHelper.isEmailRegistered(email)) {
                showProgress(false);
                etEmail.setError("Гэты email ужо зарэгістраваны");
                return;
            }

            // Генерируем соль и хешируем пароль
            byte[] salt = generateSalt();
            String hashedPassword = hashPassword(password, salt);

            // Регистрируем пользователя в базе данных
            boolean success = dbHelper.registerUser(email, firstName, lastName,
                    hashedPassword, Base64.encodeToString(salt, Base64.NO_WRAP));

            if (success) {
                // Отправляем email
                sendRegistrationEmail(firstName, email, userPlainPassword);

                showProgress(false);
                Toast.makeText(this, "Регистрация успешна!", Toast.LENGTH_LONG).show();

                startActivity(new Intent(this, LoginActivity.class));
                finish();
            } else {
                showProgress(false);
                Toast.makeText(this, "Ошибка регистрации. Попробуйте еще раз.", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Log.e(TAG, "Registration failed", e);
            showProgress(false);
            Toast.makeText(this, "Ошибка регистрация", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendRegistrationEmail(String firstName, String email, String password) {
        try {
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("message/rfc822");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Добро пожаловать в Mova! Ваши данныя для входа");

            String emailBody = createEmailBody(firstName, email, password);
            emailIntent.putExtra(Intent.EXTRA_TEXT, emailBody);

            // Создаем chooser для выбора email клиента
            Intent chooser = Intent.createChooser(emailIntent, "Отправить данныя для входа");

            if (emailIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(chooser);
                Log.i(TAG, "Email intent started successfully");
            } else {
                Log.w(TAG, "No email app found");
                // Если нет email приложения, показываем данные пользователю
                showCredentialsDialog(firstName, email, password);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error sending email", e);
            // В случае ошибки тоже показываем данные
            showCredentialsDialog(firstName, email, password);
        }
    }

    private String createEmailBody(String firstName, String email, String password) {
        return "Поздравляем, " + firstName + "! 🎉\n\n" +
                "Спасибо за регистрацию в приложении Mova - вашем помощнике в изучении белорусского языка!\n\n" +
                "📋 Ваши данные для входа:\n" +
                "----------------------------------------\n" +
                "👤 Email: " + email + "\n" +
                "🔑 Пароль: " + password + "\n" +
                "----------------------------------------\n\n" +
                "📝 Полезные советы:\n" +
                "• Сохраните это письмо в безопасном месте\n" +
                "• Не передавайте ваш пароль другим\n" +
                "• Вы можете изменить пароль в профиле\n" +
                "• Проверьте правильность email адреса\n\n" +
                "🚀 Начните изучение:\n" +
                "• Добавляйте слова в личный словарь\n" +
                "• Проходите тематические тренировки\n" +
                "• Следите за вашим прогрессом\n" +
                "• Изучайте грамматику белорусского языка\n\n" +
                "Если у вас возникнут вопросы, обращайтесь в поддержку.\n\n" +
                "С уважением,\n" +
                "Команда Mova 💙🤍\n\n" +
                "P.S. Это автоматическое сообщение, пожалуйста, не отвечайте на него.";
    }

    private void showCredentialsDialog(String firstName, String email, String password) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String message = "Сохраните ваши данные для входа:\n\n" +
                        "👤 Email: " + email + "\n" +
                        "🔑 Пароль: " + password + "\n\n" +
                        "Рекомендуем сделать скриншот или записать в безопасном месте.";

                new androidx.appcompat.app.AlertDialog.Builder(RegisterActivity.this)
                        .setTitle("Ваши данные для входа")
                        .setMessage(message)
                        .setPositiveButton("Понятно", null)
                        .setNeutralButton("Скопировать пароль", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                ClipData clip = ClipData.newPlainText("password", password);
                                clipboard.setPrimaryClip(clip);
                                Toast.makeText(RegisterActivity.this, "Пароль скопирован", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();
            }
        });
    }

    // ==================== БЕЗОПАСНОСТЬ ====================

    private String sanitizeInput(String input) {
        // Удаляем потенциально опасные символы
        return input.replaceAll("[<>\"'&;]", "");
    }

    private boolean isValidName(String name) {
        // Проверяем, что имя содержит только буквы и пробелы
        return name.matches("^[a-zA-Zа-яА-ЯёЁ\\s]+$");
    }

    private boolean isStrongPassword(String password) {
        // Проверка сложности пароля
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    private boolean isEmailRegistered(String email) {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String storedEmail = prefs.getString("encrypted_email", null);
        if (storedEmail != null) {
            try {
                String decryptedEmail = decryptData(storedEmail);
                return decryptedEmail.equalsIgnoreCase(email);
            } catch (Exception e) {
                Log.e(TAG, "Error checking email", e);
            }
        }
        return false;
    }

    private byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    private String hashPassword(String password, byte[] salt) throws Exception {
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] hash = factory.generateSecret(spec).getEncoded();
        return Base64.encodeToString(hash, Base64.NO_WRAP);
    }

    private String encryptData(String data) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(ENCRYPTION_KEY.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encrypted = cipher.doFinal(data.getBytes());
        return Base64.encodeToString(encrypted, Base64.NO_WRAP);
    }

    private String decryptData(String encryptedData) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(ENCRYPTION_KEY.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] decoded = Base64.decode(encryptedData, Base64.NO_WRAP);
        byte[] decrypted = cipher.doFinal(decoded);
        return new String(decrypted);
    }

    private void saveUserDataSecurely(String encryptedFirstName, String encryptedLastName,
                                      String encryptedEmail, String hashedPassword, byte[] salt) {
        SharedPreferences.Editor editor = getSharedPreferences("UserPrefs", MODE_PRIVATE).edit();

        editor.putString("encrypted_first_name", encryptedFirstName);
        editor.putString("encrypted_last_name", encryptedLastName);
        editor.putString("encrypted_email", encryptedEmail);
        editor.putString("hashed_password", hashedPassword);
        editor.putString("salt", Base64.encodeToString(salt, Base64.NO_WRAP));
        editor.putBoolean("is_registered", true);
        editor.putLong("registration_date", System.currentTimeMillis());

        // Применяем атомарно
        editor.apply();

        // Логируем успешную регистрацию (без чувствительных данных)
        Log.i(TAG, "User registered securely: " + encryptedEmail.substring(0, Math.min(10, encryptedEmail.length())) + "...");
    }

    // ==================== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ====================

    private void resetErrors() {
        etFirstName.setError(null);
        etLastName.setError(null);
        etEmail.setError(null);
        etPassword.setError(null);
        etConfirmPassword.setError(null);
    }

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnRegister.setEnabled(!show);
        btnLogin.setEnabled(!show);
        btnBack.setEnabled(!show);
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            android.view.inputmethod.InputMethodManager imm =
                    (android.view.inputmethod.InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void showPrivacyPolicyDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Палітыка прыватнасці")
                .setMessage("Мы абараняем вашыя асабістыя даныя:\n\n" +
                        "• Шыфруем імёны, email і паролі\n" +
                        "• Не перадаем даныя трэцім асобам\n" +
                        "• Выкарыстоўваем бяспечнае захаванне\n" +
                        "• Даем магчымасць выдаліць акаўнт\n\n" +
                        "Поўны тэкст палітыкі даступны на нашим сайце.")
                .setPositiveButton("Зразумела", null)
                .show();
    }

    @SuppressLint("GestureBackNavigation")
    @Override
    protected void onDestroy() {
        // Очищаем чувствительные данные из памяти
        if (etPassword != null) etPassword.setText("");
        if (etConfirmPassword != null) etConfirmPassword.setText("");
        userPlainPassword = null; // Очищаем пароль из памяти
        super.onDestroy();
    }

    @SuppressLint("GestureBackNavigation")
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}