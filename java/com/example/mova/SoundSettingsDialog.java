package com.example.mova;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.annotation.NonNull;

public class SoundSettingsDialog extends Dialog {
    private Context context;
    private SeekBar seekBarVolume;
    private TextView tvVolumeValue;
    private ImageButton btnToggleMusic;
    private boolean isMusicEnabled;
    private int currentVolume;

    private static final String PREFS_NAME = "MusicPrefs";
    private static final String PREF_MUSIC_ENABLED = "music_enabled";
    private static final String PREF_VOLUME = "volume";

    public SoundSettingsDialog(@NonNull Context context) {
        super(context);
        this.context = context;
        loadSettings();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_sound_settings);

        setTitle("Настройки звука");
        initializeViews();
        setupListeners();
        updateUI();
    }

    private void initializeViews() {
        seekBarVolume = findViewById(R.id.seekBarVolume);
        tvVolumeValue = findViewById(R.id.tvVolumeValue);
        btnToggleMusic = findViewById(R.id.btnToggleMusic);

        seekBarVolume.setProgress(currentVolume);
        tvVolumeValue.setText(currentVolume + "%");
    }

    private void setupListeners() {
        // Кнопка включения/выключения музыки
        btnToggleMusic.setOnClickListener(v -> {
            isMusicEnabled = !isMusicEnabled;
            updateMusicState();
            updateUI();
            saveSettings();
        });

        // Ползунок громкости
        seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentVolume = progress;
                tvVolumeValue.setText(progress + "%");
                updateVolume();
                saveSettings();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void updateUI() {
        if (isMusicEnabled) {
            btnToggleMusic.setImageResource(R.drawable.ic_volume_up);
            btnToggleMusic.setBackgroundColor(getContext().getColor(android.R.color.holo_green_dark));
            seekBarVolume.setEnabled(true);
        } else {
            btnToggleMusic.setImageResource(R.drawable.ic_volume_off);
            btnToggleMusic.setBackgroundColor(getContext().getColor(android.R.color.holo_red_dark));
            seekBarVolume.setEnabled(false);
        }
    }

    private void updateMusicState() {
        Intent musicIntent = new Intent(context, MusicService.class);
        if (isMusicEnabled) {
            musicIntent.putExtra("action", "play");
        } else {
            musicIntent.putExtra("action", "pause");
        }
        context.startService(musicIntent);
    }

    private void updateVolume() {
        float volume = currentVolume / 100.0f;
        Intent musicIntent = new Intent(context, MusicService.class);
        musicIntent.putExtra("action", "set_volume");
        musicIntent.putExtra("volume", volume);
        context.startService(musicIntent);
    }

    private void loadSettings() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        isMusicEnabled = prefs.getBoolean(PREF_MUSIC_ENABLED, true);
        currentVolume = prefs.getInt(PREF_VOLUME, 50);
    }

    private void saveSettings() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit()
                .putBoolean(PREF_MUSIC_ENABLED, isMusicEnabled)
                .putInt(PREF_VOLUME, currentVolume)
                .apply();
    }
}
