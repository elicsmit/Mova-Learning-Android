package com.example.mova;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "MovaPrefs";
    private static final String PREF_FIRST_LAUNCH = "first_launch";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);


        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        if (!isFirstLaunch()) {
            Log.d("WelcomeActivity", "Not first launch, going to MainActivity");
            startMainActivity();
            return;
        }

        Log.d("WelcomeActivity", "First launch, showing welcome screen");
        setupViews();
    }

    private void setupViews() {
        Button btnRegister = findViewById(R.id.btnRegister);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView btnSkip = findViewById(R.id.btnSkip);

        btnRegister.setOnClickListener(v -> {
            Log.d("WelcomeActivity", "Register button clicked");
            markFirstLaunchCompleted();
            startActivity(new Intent(WelcomeActivity.this, RegisterActivity.class));
            finish();
        });

        btnLogin.setOnClickListener(v -> {
            Log.d("WelcomeActivity", "Login button clicked");
            markFirstLaunchCompleted();
            startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
            finish();
        });

        btnSkip.setOnClickListener(v -> {
            Log.d("WelcomeActivity", "Skip button clicked");
            markFirstLaunchCompleted();
            startMainActivity();
        });
    }

    private boolean isFirstLaunch() {
        boolean firstLaunch = sharedPreferences.getBoolean(PREF_FIRST_LAUNCH, true);
        Log.d("WelcomeActivity", "isFirstLaunch: " + firstLaunch);
        return firstLaunch;
    }

    private void markFirstLaunchCompleted() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREF_FIRST_LAUNCH, false);
        editor.apply();
        Log.d("WelcomeActivity", "First launch marked as completed");
    }

    private void startMainActivity() {
        Log.d("WelcomeActivity", "Starting MainActivity");
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}