package com.example.mova;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

    public class MusicService extends Service {
        private static final String TAG = "MusicService";
        private MediaPlayer mediaPlayer;
        private boolean isPaused = false;

        @Override
        public void onCreate() {
            super.onCreate();
            Log.d(TAG, "MusicService created");

            // Создаем MediaPlayer с вашей музыкой (добавьте файл в res/raw/)
            mediaPlayer = MediaPlayer.create(this, R.raw.background_music);
            mediaPlayer.setLooping(true);
            mediaPlayer.setVolume(0.5f, 0.5f); // Начальная громкость 50%
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            if (intent != null) {
                String action = intent.getStringExtra("action");

                if ("play".equals(action)) {
                    playMusic();
                } else if ("pause".equals(action)) {
                    pauseMusic();
                } else if ("stop".equals(action)) {
                    stopMusic();
                } else if ("set_volume".equals(action)) {
                    float volume = intent.getFloatExtra("volume", 0.5f);
                    setVolume(volume);
                }
            }
            return START_STICKY;
        }

        private void playMusic() {
            if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                if (isPaused) {
                    mediaPlayer.start();
                    isPaused = false;
                } else {
                    mediaPlayer.start();
                }
                Log.d(TAG, "Music started");
            }
        }

        private void pauseMusic() {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                isPaused = true;
                Log.d(TAG, "Music paused");
            }
        }

        private void stopMusic() {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                Log.d(TAG, "Music stopped");
            }
            stopSelf();
        }

        private void setVolume(float volume) {
            if (mediaPlayer != null) {
                mediaPlayer.setVolume(volume, volume);
                Log.d(TAG, "Volume set to: " + volume);
            }
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
            Log.d(TAG, "MusicService destroyed");
        }

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }

