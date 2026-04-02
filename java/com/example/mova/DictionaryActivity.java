package com.example.mova;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DictionaryActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private RecyclerView wordsRecyclerView;
    private WordAdapter wordAdapter;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);

        dbHelper = new DatabaseHelper(this);
        wordsRecyclerView = findViewById(R.id.wordsRecyclerView);
        wordsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Настройка эмблем тем
        setupThemeEmblems();

        // Кнопка назад
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    private void setupThemeEmblems() {
        ViewGroup themesContainer = findViewById(R.id.themesContainer);
        List<String> categories = dbHelper.getAllCategories();

        // ID изображений для эмблем тем
        int[] themeIcons = {
                R.drawable.icon_greetings,  // Прывітанні
                R.drawable.icon_food,       // Ежа
                R.drawable.icon_family,     // Сям'я
                R.drawable.icon_nature,     // Прырода
                R.drawable.icon_animals     // Жывёлы
        };

        for (int i = 0; i < categories.size(); i++) {
            View themeView = LayoutInflater.from(this).inflate(R.layout.item_theme, themesContainer, false);

            ImageView themeIcon = themeView.findViewById(R.id.themeIcon);
            TextView themeName = themeView.findViewById(R.id.themeName);

            themeIcon.setImageResource(themeIcons[i]);
            themeName.setText(categories.get(i));

            final String category = categories.get(i);
            themeView.setOnClickListener(v -> showWordsForCategory(category));

            themesContainer.addView(themeView);
        }
    }

    private void showWordsForCategory(String category) {
        List<Word> words = dbHelper.getWordsWithImagesByCategory(category);
        wordAdapter = new WordAdapter(words, new WordAdapter.OnWordActionListener() {
            @Override
            public void onAudioClick(Word word) {
                playWordAudio(word.getAudioResourceId());
            }

            @Override
            public void onWordLearned(Word word) {
                markWordAsLearned(word);
            }
        });
        wordsRecyclerView.setAdapter(wordAdapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void markWordAsLearned(Word word) {
        // Инициализируем databaseHelper правильно
        DatabaseHelper databaseHelper = new DatabaseHelper(this); // или используйте существующий экземпляр

        // Проверяем, нет ли уже слова в личном словаре
        if (databaseHelper.isWordInPersonalDictionary(word.getBelarusian())) {
            Toast.makeText(this, "Слова ўжо ў асабістым слоўніку!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Добавляем слово в личный словарь
        boolean success = databaseHelper.addWordToPersonalDictionary(word);

        if (success) {
            word.setLearned(true);
            wordAdapter.notifyDataSetChanged();
            updateWordStats();
            Toast.makeText(this, "Слова дададзена ў асабісты слоўнік!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Памылка дадавання слова", Toast.LENGTH_SHORT).show();
        }
    }


    private void updateWordStats() {
        // Обновляем статистику изученных слов
        SharedPreferences statsPrefs = getSharedPreferences("UserStats", MODE_PRIVATE);
        int currentWords = statsPrefs.getInt("learned_words", 0);
        int currentExp = statsPrefs.getInt("experience", 0);

        // Начисляем опыт за слово (5 очков)
        SharedPreferences.Editor editor = statsPrefs.edit();
        editor.putInt("learned_words", currentWords + 1);
        editor.putInt("experience", currentExp + 5);
        editor.apply();

        // Обновляем день активности
        updateActiveDay();
    }

    private void updateActiveDay() {
        String today = getTodayDate();
        SharedPreferences statsPrefs = getSharedPreferences("UserStats", MODE_PRIVATE);
        Set<String> activeDays = statsPrefs.getStringSet("active_days", new HashSet<String>());

        // Создаем новый Set т.к. нельзя изменять возвращаемый
        Set<String> newActiveDays = new HashSet<>(activeDays);

        if (!newActiveDays.contains(today)) {
            newActiveDays.add(today);
            SharedPreferences.Editor editor = statsPrefs.edit();
            editor.putStringSet("active_days", newActiveDays);
            editor.apply();
        }

        // Обновляем стрик
        updateStreak();
    }


    public int addWordToPersonalDictionary(Word word) {
        // реализация добавления объекта Word
        return addWordToPersonalDictionary(word.getId()); // или прямая реализация
    }

    public int addWordToPersonalDictionary(long wordId) {
        // реализация добавления по ID
        // возвращает true при успехе, false если слово уже есть
        return 0;
    }

    public List<Word> getPersonalDictionaryWords() {
        // версия без параметров
        return getPersonalDictionaryWords(0); // или прямая реализация
    }

    public List<Word> getPersonalDictionaryWords(int filter) {
        // реализация с фильтром
        // 0 = все слова
        return Collections.emptyList();
    }
    private void updateStreak() {
        String today = getTodayDate();
        SharedPreferences statsPrefs = getSharedPreferences("UserStats", MODE_PRIVATE);
        String lastActiveDate = statsPrefs.getString("last_active_date", "");
        int currentStreak = statsPrefs.getInt("current_streak", 0);
        int maxStreak = statsPrefs.getInt("max_streak", 0);

        if (lastActiveDate.isEmpty()) {
            // Первый вход
            currentStreak = 1;
        } else if (isYesterday(lastActiveDate)) {
            // Вошел вчера - увеличиваем стрик
            currentStreak++;
        } else if (!today.equals(lastActiveDate)) {
            // Пропустил день - сбрасываем стрик
            currentStreak = 1;
        }

        if (currentStreak > maxStreak) {
            maxStreak = currentStreak;
        }

        SharedPreferences.Editor editor = statsPrefs.edit();
        editor.putString("last_active_date", today);
        editor.putInt("current_streak", currentStreak);
        editor.putInt("max_streak", maxStreak);
        editor.apply();
    }

    private String getTodayDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return year + "-" + month + "-" + day;
    }

    private boolean isYesterday(String date) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        String yesterday = year + "-" + month + "-" + day;

        return date.equals(yesterday);
    }

    private void playWordAudio(int audioResourceId) {
        // Останавливаем предыдущее воспроизведение
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        try {
            mediaPlayer = MediaPlayer.create(this, audioResourceId);
            if (mediaPlayer != null) {
                mediaPlayer.setOnCompletionListener(mp -> {
                    mediaPlayer.release();
                    mediaPlayer = null;
                });
                mediaPlayer.start();
            } else {
                Toast.makeText(this, "Аудио не найдено", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка воспроизведения", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        dbHelper.close();
    }

    // Адаптер для карточек слов с аудио и кнопкой изучения
    private static class WordAdapter extends RecyclerView.Adapter<WordAdapter.WordViewHolder> {
        private List<Word> words;
        private OnWordActionListener actionListener;

        public interface OnWordActionListener {
            void onAudioClick(Word word);
            void onWordLearned(Word word);
        }

        public WordAdapter(List<Word> words, OnWordActionListener actionListener) {
            this.words = words;
            this.actionListener = actionListener;
        }

        @Override
        public WordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_word_with_image, parent, false);
            return new WordViewHolder(view);
        }

        @Override
        public void onBindViewHolder(WordViewHolder holder, int position) {
            Word word = words.get(position);
            holder.belarusianWord.setText(word.getBelarusian());
            holder.russianWord.setText(word.getRussian());
            holder.englishWord.setText(word.getEnglish());

            // Устанавливаем картинку для слова
            int imageResId = getImageForWord(word.getBelarusian());
            holder.wordImage.setImageResource(imageResId);

            // Обработчик нажатия на кнопку аудио
            holder.btnAudio.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onAudioClick(word);
                }
            });

            // Обработчик нажатия на кнопку изучения
            holder.btnLearn.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onWordLearned(word);
                }
            });

            // Меняем вид кнопки в зависимости от статуса изучения
            if (word.isLearned()) {
                holder.btnLearn.setText("✓ Вывучана");
                holder.btnLearn.setBackgroundColor(Color.parseColor("#4CAF50"));
                holder.btnLearn.setTextColor(Color.WHITE);
            } else {
                holder.btnLearn.setText("Вывучыць");
                holder.btnLearn.setBackgroundColor(Color.parseColor("#2196F3"));
                holder.btnLearn.setTextColor(Color.WHITE);
            }
        }

        @Override
        public int getItemCount() {
            return words.size();
        }

        class WordViewHolder extends RecyclerView.ViewHolder {
            ImageView wordImage;
            TextView belarusianWord, russianWord, englishWord;
            ImageButton btnAudio;
            Button btnLearn;

            public WordViewHolder(View itemView) {
                super(itemView);
                wordImage = itemView.findViewById(R.id.wordImage);
                belarusianWord = itemView.findViewById(R.id.belarusianWord);
                russianWord = itemView.findViewById(R.id.russianWord);
                englishWord = itemView.findViewById(R.id.englishWord);
                btnAudio = itemView.findViewById(R.id.btnAudio);
                btnLearn = itemView.findViewById(R.id.btnLearn);
            }
        }

        // Метод для получения картинки по слову
        private int getImageForWord(String belarusianWord) {
            switch (belarusianWord) {
                // Прывітанні
                case "дзякуй": return R.drawable.thank_you;
                case "прывітанне": return R.drawable.hello;
                case "добрай раніцы": return R.drawable.good_morning;
                case "да пабачэння": return R.drawable.goodbye;
                case "добры дзень": return R.drawable.good_afternoon;
                case "добры вечар": return R.drawable.good_evening;
                case "як справы?": return R.drawable.how_are_you;
                case "усё добра": return R.drawable.everything_ok;
                case "калі ласка": return R.drawable.please;

                // Ежа
                case "хлеб": return R.drawable.bread;
                case "малако": return R.drawable.milk;
                case "яблык": return R.drawable.apple;
                case "вада": return R.drawable.water;
                case "мяса": return R.drawable.meat;
                case "рыба": return R.drawable.fish;
                case "сыр": return R.drawable.cheese;
                case "яйкі": return R.drawable.eggs;
                case "кава": return R.drawable.coffee;
                case "чай": return R.drawable.tea;

                // Сям'я
                case "маці": return R.drawable.mother;
                case "бацька": return R.drawable.father;
                case "сястра": return R.drawable.sister;
                case "брат": return R.drawable.brother;
                case "дзядзька": return R.drawable.uncle;
                case "цётка": return R.drawable.aunt;
                case "бабуля": return R.drawable.grandmother;
                case "дзед": return R.drawable.grandfather;
                case "дачка": return R.drawable.daughter;
                case "сын": return R.drawable.son;

                // Прырода
                case "дрэва": return R.drawable.tree;
                case "кветка": return R.drawable.flower;
                case "рака": return R.drawable.river;
                case "горы": return R.drawable.mountains;
                case "возера": return R.drawable.lake;
                case "лес": return R.drawable.forest;
                case "сонца": return R.drawable.sun;
                case "дождж": return R.drawable.rain;
                case "снег": return R.drawable.snow;
                case "неба": return R.drawable.sky;

                // Жывёлы
                case "кот": return R.drawable.cat;
                case "сабака": return R.drawable.dog;
                case "конь": return R.drawable.horse;
                case "птушка": return R.drawable.bird;
                case "вожык": return R.drawable.hedgehog;
                case "змяя": return R.drawable.snake;
                case "свіння": return R.drawable.pig;
                case "мядзведзь": return R.drawable.bear;
                case "мыш": return R.drawable.mouse;
                case "гусь": return R.drawable.goose;

                default: return R.drawable.default_word;
            }
        }
    }
}
