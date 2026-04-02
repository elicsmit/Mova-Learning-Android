package com.example.mova;

import static com.google.android.material.transition.MaterialSharedAxis.X;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.mova.ui.data.models.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class DatabaseHelper extends SQLiteOpenHelper {

    // В класс DatabaseHelper добавьте константы:
    private static final String TABLE_USER_PROGRESS = "user_progress";
    private static final String TABLE_USER_STREAKS = "user_streaks";

    // Столбцы для таблицы прогресса
    private static final String COLUMN_UP_ID = "id";
    private static final String COLUMN_UP_USER_ID = "user_id";
    private static final String COLUMN_UP_LEARNED_WORDS = "learned_words";
    private static final String COLUMN_UP_COMPLETED_LESSONS = "completed_lessons";
    private static final String COLUMN_UP_COMPLETED_ARTICLES = "completed_articles";
    private static final String COLUMN_UP_GRAMMAR_EXERCISES = "grammar_exercises";
    private static final String COLUMN_UP_TOTAL_XP = "total_xp";
    private static final String COLUMN_UP_LAST_UPDATED = "last_updated";

    // Столбцы для таблицы стриков
    private static final String COLUMN_US_ID = "id";
    private static final String COLUMN_US_USER_ID = "user_id";
    private static final String COLUMN_US_CURRENT_STREAK = "current_streak";
    private static final String COLUMN_US_MAX_STREAK = "max_streak";
    private static final String COLUMN_US_LAST_ACTIVITY_DATE = "last_activity_date";
    private static final String COLUMN_US_LAST_LOGIN_DATE = "last_login_date";
    private static final String DATABASE_NAME = "mova.db";
    private static final int DATABASE_VERSION = 3;
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_USER_EMAIL = "email";
    private static final String COLUMN_USER_FIRST_NAME = "first_name";
    private static final String COLUMN_USER_LAST_NAME = "last_name";
    private static final String COLUMN_USER_PASSWORD_HASH = "password_hash";
    private static final String COLUMN_USER_SALT = "salt";
    private static final String COLUMN_USER_CREATED_AT = "created_at";
    private static final String COLUMN_USER_LAST_LOGIN = "last_login";
    private static final String COLUMN_USER_IS_ACTIVE = "is_active";
    // Увеличиваем версию до 3

    // Таблица тем
    private static final String TABLE_CATEGORIES = "categories";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";

    // Таблица слов
    private static final String TABLE_WORDS = "words";
    private static final String COLUMN_BELARUSIAN = "belarusian";
    private static final String COLUMN_RUSSIAN = "russian";
    private static final String COLUMN_ENGLISH = "english";
    private static final String COLUMN_AUDIO_RESOURCE = "audio_resource";
    private static final String COLUMN_CATEGORY_ID = "category_id";

    // Таблица личного словаря
    private static final String TABLE_PERSONAL_DICTIONARY = "personal_dictionary";
    private static final String COLUMN_PD_ID = "id";
    private static final String COLUMN_PD_BELARUSIAN = "belarusian";
    private static final String COLUMN_PD_RUSSIAN = "russian";
    private static final String COLUMN_PD_ENGLISH = "english";
    private static final String COLUMN_PD_EXAMPLE = "example";
    private static final String COLUMN_PD_STATUS = "status";
    private static final String COLUMN_PD_ADDED_DATE = "added_date";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void debugTableStructure() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("PRAGMA table_info(personal_dictionary)", null);

        Log.d("DEBUG", "=== СТРУКТУРА ТАБЛИЦЫ personal_dictionary ===");
        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String columnName = cursor.getString(cursor.getColumnIndex("name"));
                @SuppressLint("Range") String columnType = cursor.getString(cursor.getColumnIndex("type"));
                Log.d("DEBUG", "Колонка: " + columnName + " | Тип: " + columnType);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
    }

    public boolean isActiveDayRecorded(int userId, String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                "user_activity",
                new String[]{"id"},
                "user_id = ? AND activity_date = ?",
                new String[]{String.valueOf(userId), date},
                null, null, null
        );
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public void addActiveDay(int userId, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("activity_date", date);
        db.insert("user_activity", null, values);
    }

    // DatabaseHelper.java
    public Set<String> getUnlockedAchievements(int userId) {
        Set<String> unlockedAchievements = new HashSet<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        try {
            cursor = db.query(
                    "user_achievements", // имя таблицы с достижениями пользователя
                    new String[]{"achievement_id"},
                    "user_id = ?",
                    new String[]{String.valueOf(userId)},
                    null, null, null
            );

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String achievementId = cursor.getString(cursor.getColumnIndexOrThrow("achievement_id"));
                    unlockedAchievements.add(achievementId);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return unlockedAchievements;
    }
    public int getUserActiveDays(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(DISTINCT activity_date) FROM user_activity WHERE user_id = ?",
                new String[]{String.valueOf(userId)}
        );
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    // Методы для работы с стриками
    public int getCurrentStreak(int userId) {
        return getIntValue(userId, "current_streak");
    }

    public int getMaxStreak(int userId) {
        return getIntValue(userId, "max_streak");
    }

    public void updateStreak(int userId, int currentStreak, int maxStreak) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("current_streak", currentStreak);
        values.put("max_streak", maxStreak);
        db.update("user_stats", values, "user_id = ?", new String[]{String.valueOf(userId)});
    }

    public String getLastActiveDate(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT MAX(activity_date) FROM user_activity WHERE user_id = ?",
                new String[]{String.valueOf(userId)}
        );
        String date = null;
        if (cursor.moveToFirst()) {
            date = cursor.getString(0);
        }
        cursor.close();
        return date;
    }

    private int getIntValue(int userId, String column) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                "user_stats",
                new String[]{column},
                "user_id = ?",
                new String[]{String.valueOf(userId)},
                null, null, null
        );
        int value = 0;
        if (cursor.moveToFirst()) {
            value = cursor.getInt(0);
        }
        cursor.close();
        return value;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // Создаем таблицу тем
        String createCategoriesTable = "CREATE TABLE " + TABLE_CATEGORIES + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_NAME + " TEXT)";
        db.execSQL(createCategoriesTable);

        String createUsersTable = "CREATE TABLE " + TABLE_USERS + "(" +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_USER_EMAIL + " TEXT UNIQUE NOT NULL," +
                COLUMN_USER_FIRST_NAME + " TEXT NOT NULL," +
                COLUMN_USER_LAST_NAME + " TEXT NOT NULL," +
                COLUMN_USER_PASSWORD_HASH + " TEXT NOT NULL," +
                COLUMN_USER_SALT + " TEXT NOT NULL," +
                COLUMN_USER_CREATED_AT + " INTEGER NOT NULL," +
                COLUMN_USER_LAST_LOGIN + " INTEGER," +
                COLUMN_USER_IS_ACTIVE + " INTEGER DEFAULT 1" +
                ")";
        db.execSQL(createUsersTable);

        // Создаем таблицу слов с аудио
        String createWordsTable = "CREATE TABLE " + TABLE_WORDS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_BELARUSIAN + " TEXT," +
                COLUMN_RUSSIAN + " TEXT," +
                COLUMN_ENGLISH + " TEXT," +
                COLUMN_AUDIO_RESOURCE + " INTEGER," +
                COLUMN_CATEGORY_ID + " INTEGER)";
        db.execSQL(createWordsTable);

        // Создаем таблицу личного словаря
        String createPersonalDictionaryTable = "CREATE TABLE " + TABLE_PERSONAL_DICTIONARY + "(" +
                COLUMN_PD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_PD_BELARUSIAN + " TEXT," +
                COLUMN_PD_RUSSIAN + " TEXT," +
                COLUMN_PD_ENGLISH + " TEXT," +
                COLUMN_PD_EXAMPLE + " TEXT," +
                COLUMN_PD_STATUS + " TEXT DEFAULT 'learning'," +
                COLUMN_PD_ADDED_DATE + " TEXT" +
                ")";
        db.execSQL(createPersonalDictionaryTable);

        String createUserProgressTable = "CREATE TABLE " + TABLE_USER_PROGRESS + "(" +
                COLUMN_UP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_UP_USER_ID + " INTEGER NOT NULL," +
                COLUMN_UP_LEARNED_WORDS + " INTEGER DEFAULT 0," +
                COLUMN_UP_COMPLETED_LESSONS + " INTEGER DEFAULT 0," +
                COLUMN_UP_COMPLETED_ARTICLES + " INTEGER DEFAULT 0," +
                COLUMN_UP_GRAMMAR_EXERCISES + " INTEGER DEFAULT 0," +
                COLUMN_UP_TOTAL_XP + " INTEGER DEFAULT 0," +
                COLUMN_UP_LAST_UPDATED + " INTEGER," +
                "FOREIGN KEY(" + COLUMN_UP_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ")" +
                ")";
        db.execSQL(createUserProgressTable);

        // Таблица стриков пользователя
        String createUserStreaksTable = "CREATE TABLE " + TABLE_USER_STREAKS + "(" +
                COLUMN_US_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_US_USER_ID + " INTEGER NOT NULL," +
                COLUMN_US_CURRENT_STREAK + " INTEGER DEFAULT 0," +
                COLUMN_US_MAX_STREAK + " INTEGER DEFAULT 0," +
                COLUMN_US_LAST_ACTIVITY_DATE + " TEXT," +
                COLUMN_US_LAST_LOGIN_DATE + " TEXT," +
                "FOREIGN KEY(" + COLUMN_US_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ")" +
                ")";
        db.execSQL(createUserStreaksTable);
        String CREATE_USER_ACHIEVEMENTS_TABLE = "CREATE TABLE user_achievements (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER," +
                "achievement_id TEXT," +
                "unlocked_date TEXT," +
                "FOREIGN KEY(user_id) REFERENCES users(id)" +
                ");";
        db.execSQL(CREATE_USER_ACHIEVEMENTS_TABLE);

        // Добавляем начальные данные
        insertInitialData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


        if (oldVersion < X) { // замените X на актуальную версию
            String CREATE_USER_ACHIEVEMENTS_TABLE = "CREATE TABLE user_achievements (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "user_id INTEGER," +
                    "achievement_id TEXT," +
                    "unlocked_date TEXT," +
                    "FOREIGN KEY(user_id) REFERENCES users(id)" +
                    ");";
            db.execSQL(CREATE_USER_ACHIEVEMENTS_TABLE);
        }
        if (oldVersion < 2) { // увеличи версию базы в конструкторе
            db.execSQL("ALTER TABLE words ADD COLUMN learned INTEGER DEFAULT 0");
        }
        if (oldVersion < 3) {
            // Создаем таблицу personal_dictionary если ее нет
            String createPersonalDictionaryTable = "CREATE TABLE IF NOT EXISTS " + TABLE_PERSONAL_DICTIONARY + "(" +
                    COLUMN_PD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_PD_BELARUSIAN + " TEXT," +
                    COLUMN_PD_RUSSIAN + " TEXT," +
                    COLUMN_PD_ENGLISH + " TEXT," +
                    COLUMN_PD_EXAMPLE + " TEXT," +
                    COLUMN_PD_STATUS + " TEXT DEFAULT 'learning'," +
                    COLUMN_PD_ADDED_DATE + " TEXT" +
                    ")";
            db.execSQL(createPersonalDictionaryTable);
        }
        if (oldVersion < 4) {
            String createUsersTable = "CREATE TABLE IF NOT EXISTS " + TABLE_USERS + "(" +
                    COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_USER_EMAIL + " TEXT UNIQUE NOT NULL," +
                    COLUMN_USER_FIRST_NAME + " TEXT NOT NULL," +
                    COLUMN_USER_LAST_NAME + " TEXT NOT NULL," +
                    COLUMN_USER_PASSWORD_HASH + " TEXT NOT NULL," +
                    COLUMN_USER_SALT + " TEXT NOT NULL," +
                    COLUMN_USER_CREATED_AT + " INTEGER NOT NULL," +
                    COLUMN_USER_LAST_LOGIN + " INTEGER," +
                    COLUMN_USER_IS_ACTIVE + " INTEGER DEFAULT 1" +
                    ")";
            db.execSQL(createUsersTable);
        }

    }

    // DatabaseHelper.java - добавьте эти методы
    public int getTotalExperience(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT total_xp FROM user_stats WHERE user_id = ?",
                new String[]{String.valueOf(userId)}
        );

        int totalXp = 0;
        if (cursor.moveToFirst()) {
            totalXp = cursor.getInt(0);
        }
        cursor.close();
        return totalXp;
    }

    public void addLearnedWords(int userId, int count) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Получаем текущее количество изученных слов
        int currentWords = getLearnedWordsCount(userId);

        ContentValues values = new ContentValues();
        values.put("learned_words", currentWords + count);

        db.update("user_stats", values, "user_id = ?", new String[]{String.valueOf(userId)});
    }

    public void addExperience(int userId, int experience) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Получаем текущий опыт
        int currentXp = getTotalExperience(userId);

        ContentValues values = new ContentValues();
        values.put("total_xp", currentXp + experience);

        db.update("user_stats", values, "user_id = ?", new String[]{String.valueOf(userId)});
    }

    public void addCompletedLesson(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Получаем текущее количество уроков
        int currentLessons = getCompletedLessonsCount(userId);

        ContentValues values = new ContentValues();
        values.put("completed_lessons", currentLessons + 1);

        db.update("user_stats", values, "user_id = ?", new String[]{String.valueOf(userId)});
    }

    public void updateUserName(int userId, String newName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("first_name", newName);

        db.update("users", values, "id = ?", new String[]{String.valueOf(userId)});
    }

    public int getLearnedWordsCount(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT learned_words FROM user_stats WHERE user_id = ?",
                new String[]{String.valueOf(userId)}
        );

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public int getCompletedLessonsCount(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT completed_lessons FROM user_stats WHERE user_id = ?",
                new String[]{String.valueOf(userId)}
        );

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public int getRealLearnedWordsCount(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM personal_words WHERE user_id = ? AND is_learned = 1",
                new String[]{String.valueOf(userId)}
        );

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    // Метод для получения реального количества завершенных уроков
    public int getRealCompletedLessonsCount(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM completed_lessons WHERE user_id = ?",
                new String[]{String.valueOf(userId)}
        );

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    // Метод для получения реального количества слов в словаре
    public int getRealPersonalWordsCount(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM personal_words WHERE user_id = ?",
                new String[]{String.valueOf(userId)}
        );

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    // Метод для получения реального опыта
    public int getRealTotalExperience(int userId) {
        // Опыт можно рассчитывать из различных активностей
        int wordsExp = getRealLearnedWordsCount(userId) * 10;
        int lessonsExp = getRealCompletedLessonsCount(userId) * 50;
        int streakExp = getCurrentStreak(userId) * 5;

        return wordsExp + lessonsExp + streakExp;
    }

    // Метод для получения пользователя по ID
    public User getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                "users",
                new String[]{"id", "first_name", "email", "created_date"},
                "id = ?",
                new String[]{String.valueOf(userId)},
                null, null, null
        );

        User user = null;
        if (cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            user.setFirstName(cursor.getString(cursor.getColumnIndexOrThrow("first_name")));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
            // ... другие поля
        }
        cursor.close();
        return user;
    }
    public void initializeUserStats(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Проверяем, существует ли уже запись
        Cursor cursor = db.rawQuery(
                "SELECT id FROM user_stats WHERE user_id = ?",
                new String[]{String.valueOf(userId)}
        );

        if (!cursor.moveToFirst()) {
            // Создаем новую запись со значениями по умолчанию
            ContentValues values = new ContentValues();
            values.put("user_id", userId);
            values.put("learned_words", 0);
            values.put("completed_lessons", 0);
            values.put("total_xp", 0);
            values.put("current_streak", 0);
            values.put("max_streak", 0);

            db.insert("user_stats", null, values);
        }
        cursor.close();
    }

    private void insertInitialData(SQLiteDatabase db) {
        // Добавляем темы
        String[] categories = {"Прывітанні", "Ежа", "Сям'я", "Прырода", "Жывёлы"};
        for (String category : categories) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME, category);
            db.insert(TABLE_CATEGORIES, null, values);
        }

        // Добавляем слова с аудио
        insertWords(db);
    }

    // В класс DatabaseHelper добавляем метод:
    public boolean updateWordLearningStatus(int wordId, boolean isLearned) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("is_learned", isLearned ? 1 : 0);

            int rowsAffected = db.update(
                    "personal_dictionary",
                    values,
                    "word_id = ?",
                    new String[]{String.valueOf(wordId)}
            );

            return rowsAffected > 0;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error updating word learning status", e);
            return false;
        } finally {
            db.close();
        }
    }

    private void insertWords(SQLiteDatabase db) {
        // Прывітанні (ID категории 1)
        insertWord(db, "дзякуй", "спасибо", "thank you", R.raw.thank_you_audio, 1);
        insertWord(db, "прывітанне", "привет", "hello", R.raw.hello_audio, 1);
        insertWord(db, "добрай раніцы", "доброе утро", "good morning", R.raw.good_morning_audio, 1);
        insertWord(db, "да пабачэння", "до свидания", "goodbye", R.raw.goodbye_audio, 1);
        insertWord(db, "добры дзень", "добрый день", "good afternoon", R.raw.good_afternoon_audio, 1);
        insertWord(db, "добры вечар", "добрый вечер", "good evening", R.raw.good_evening_audio, 1);
        insertWord(db, "як справы?", "как дела?", "how are you?", R.raw.how_are_you_audio, 1);
        insertWord(db, "усё добра", "всё хорошо", "everything is fine", R.raw.everything_is_fine_audio, 1);
        insertWord(db, "калі ласка", "пожалуйста", "please", R.raw.please_audio, 1);

        // Ежа (ID категории 2)
        insertWord(db, "хлеб", "хлеб", "bread", R.raw.bread_audio, 2);
        insertWord(db, "малако", "молоко", "milk", R.raw.milk_audio, 2);
        insertWord(db, "яблык", "яблоко", "apple", R.raw.apple_audio, 2);
        insertWord(db, "вада", "вода", "water", R.raw.water_audio, 2);
        insertWord(db, "мяса", "мясо", "meat", R.raw.meat_audio, 2);
        insertWord(db, "рыба", "рыба", "fish", R.raw.fish_audio, 2);
        insertWord(db, "сыр", "сыр", "cheese", R.raw.cheese_audio, 2);
        insertWord(db, "яйкі", "яйца", "eggs", R.raw.eggs_audio, 2);
        insertWord(db, "кава", "кофе", "coffee", R.raw.coffee_audio, 2);
        insertWord(db, "чай", "чай", "tea", R.raw.tea_audio, 2);

        // Сям'я (ID категории 3)
        insertWord(db, "маці", "мать", "mother", R.raw.mother_audio, 3);
        insertWord(db, "бацька", "отец", "father", R.raw.father_audio, 3);
        insertWord(db, "сястра", "сестра", "sister", R.raw.sister_audio, 3);
        insertWord(db, "брат", "брат", "brother", R.raw.brother_audio, 3);
        insertWord(db, "дзядзька", "дядя", "uncle", R.raw.uncle_audio, 3);
        insertWord(db, "цётка", "тётя", "aunt", R.raw.aunt_audio, 3);
        insertWord(db, "бабуля", "бабушка", "grandmother", R.raw.grandmother_audio, 3);
        insertWord(db, "дзед", "дедушка", "grandfather", R.raw.grandfather_audio, 3);
        insertWord(db, "дачка", "дочь", "daughter", R.raw.daughter_audio, 3);
        insertWord(db, "сын", "сын", "son", R.raw.son_audio, 3);

        // Прырода (ID категории 4)
        insertWord(db, "дрэва", "дерево", "tree", R.raw.tree_audio, 4);
        insertWord(db, "кветка", "цветок", "flower", R.raw.flower_audio, 4);
        insertWord(db, "рака", "река", "river", R.raw.river_audio, 4);
        insertWord(db, "горы", "горы", "mountains", R.raw.mountains_audio, 4);
        insertWord(db, "возера", "озеро", "lake", R.raw.lake_audio, 4);
        insertWord(db, "лес", "лес", "forest", R.raw.forest_audio, 4);
        insertWord(db, "сонца", "солнце", "sun", R.raw.sun_audio, 4);
        insertWord(db, "дождж", "дождь", "rain", R.raw.rain_audio, 4);
        insertWord(db, "снег", "снег", "snow", R.raw.snow_audio, 4);
        insertWord(db, "неба", "небо", "sky", R.raw.sky_audio, 4);

        // Жывёлы (ID категории 5)
        insertWord(db, "кот", "кот", "cat", R.raw.cat_audio, 5);
        insertWord(db, "сабака", "собака", "dog", R.raw.dog_audio, 5);
        insertWord(db, "конь", "лошадь", "horse", R.raw.horse_audio, 5);
        insertWord(db, "птушка", "птица", "bird", R.raw.bird_audio, 5);
        insertWord(db, "вожык", "ежик", "hedgehog", R.raw.hedgehog_audio, 5);
        insertWord(db, "змяя", "змея", "snake", R.raw.snake_audio, 5);
        insertWord(db, "свіння", "свинья", "pig", R.raw.pig_audio, 5);
        insertWord(db, "мядзведзь", "медведь", "bear", R.raw.bear_audio, 5);
        insertWord(db, "мыш", "мышь", "mouse", R.raw.mouse_audio, 5);
        insertWord(db, "гусь", "гусь", "goose", R.raw.goose_audio, 5);
    }

    private void insertWord(SQLiteDatabase db, String belarusian, String russian, String english, int audioResId, int categoryId) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_BELARUSIAN, belarusian);
        values.put(COLUMN_RUSSIAN, russian);
        values.put(COLUMN_ENGLISH, english);
        values.put(COLUMN_AUDIO_RESOURCE, audioResId);
        values.put(COLUMN_CATEGORY_ID, categoryId);
        db.insert(TABLE_WORDS, null, values);
    }

    // ============ МЕТОДЫ ДЛЯ ЛИЧНОГО СЛОВАРЯ ============

    // Добавить слово в личный словарь


    // В класс DatabaseHelper добавьте этот метод:
    public String getUserSalt(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_USER_SALT + " FROM " + TABLE_USERS +
                " WHERE " + COLUMN_USER_EMAIL + " = ? AND " +
                COLUMN_USER_IS_ACTIVE + " = 1";

        Cursor cursor = db.rawQuery(query, new String[]{email.toLowerCase()});

        String salt = null;
        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range")
            String saltValue = cursor.getString(cursor.getColumnIndex(COLUMN_USER_SALT));
            salt = saltValue;
            cursor.close();
        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return salt;
    }
    // Проверить, есть ли слово в личном словаре
    public boolean isWordInPersonalDictionary(String belarusianWord) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_PERSONAL_DICTIONARY +
                " WHERE " + COLUMN_PD_BELARUSIAN + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{belarusianWord});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count > 0;
    }

    // Получить количество слов в личном словаре
    public int getPersonalWordsCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_PERSONAL_DICTIONARY;
        Cursor cursor = db.rawQuery(query, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    // Получить все слова из личного словаря


    // Получить последние слова для профиля
    public List<Word> getRecentPersonalWords(int limit, int i) {
        List<Word> words = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_PERSONAL_DICTIONARY +
                " ORDER BY " + COLUMN_PD_ADDED_DATE + " DESC LIMIT ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(limit)});

        if (cursor.moveToFirst()) {
            do {
                Word word = cursorToPersonalDictionaryWord(cursor);
                words.add(word);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return words;
    }

    // Удалить слово из личного словаря
    public boolean removeWordFromPersonalDictionary(long wordId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_PERSONAL_DICTIONARY, COLUMN_PD_ID + " = ?",
                new String[]{String.valueOf(wordId)});
        return result > 0;
    }

    // Поиск в личном словаре
    public List<Word> searchPersonalDictionary(String query) {
        List<Word> words = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "SELECT * FROM " + TABLE_PERSONAL_DICTIONARY +
                " WHERE " + COLUMN_PD_BELARUSIAN + " LIKE ? OR " +
                COLUMN_PD_RUSSIAN + " LIKE ? OR " +
                COLUMN_PD_ENGLISH + " LIKE ? " +
                "ORDER BY " + COLUMN_PD_ADDED_DATE + " DESC";

        String searchPattern = "%" + query + "%";
        Cursor cursor = db.rawQuery(sql, new String[]{searchPattern, searchPattern, searchPattern});

        if (cursor.moveToFirst()) {
            do {
                Word word = cursorToPersonalDictionaryWord(cursor);
                words.add(word);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return words;
    }

    // ============ ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ============

    @SuppressLint("Range")
    private Word cursorToPersonalDictionaryWord(Cursor cursor) {
        Word word = new Word(
                cursor.getString(cursor.getColumnIndex(COLUMN_PD_BELARUSIAN)),
                cursor.getString(cursor.getColumnIndex(COLUMN_PD_RUSSIAN)),
                cursor.getString(cursor.getColumnIndex(COLUMN_PD_ENGLISH))
        );
        word.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_PD_ID)));
        word.setExample(cursor.getString(cursor.getColumnIndex(COLUMN_PD_EXAMPLE)));
        word.setStatus(cursor.getString(cursor.getColumnIndex(COLUMN_PD_STATUS)));
        word.setAddedDate(cursor.getString(cursor.getColumnIndex(COLUMN_PD_ADDED_DATE)));

        return word;
    }


    // ============ СТАРЫЕ МЕТОДЫ (оставить без изменений) ============

    // Получить все темы

    // В DatabaseHelper.java добавьте этот метод:
    public int getWordsCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM words", null);
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
            return 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }

    // DatabaseHelper.java
    public boolean removeWordFromPersonalDictionary(int userId, int wordId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete("personal_words",
                "user_id = ? AND word_id = ?",
                new String[]{String.valueOf(userId), String.valueOf(wordId)});
        return result > 0;
    }
    public List<String> getAllCategories() {
        List<String> categories = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CATEGORIES, new String[]{COLUMN_NAME},
                null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                categories.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return categories;
    }

    // Получить слова по категории с аудио
    public List<Word> getWordsWithImagesByCategory(String category) {
        List<Word> words = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT " + COLUMN_BELARUSIAN + ", " + COLUMN_RUSSIAN + ", " + COLUMN_ENGLISH + ", " + COLUMN_AUDIO_RESOURCE +
                " FROM " + TABLE_WORDS + " WHERE " + COLUMN_CATEGORY_ID + " = (" +
                "SELECT " + COLUMN_ID + " FROM " + TABLE_CATEGORIES + " WHERE " + COLUMN_NAME + " = ?)";

        Cursor cursor = db.rawQuery(query, new String[]{category});

        if (cursor.moveToFirst()) {
            do {
                Word word = new Word(
                        cursor.getString(0), // belarusian
                        cursor.getString(1), // russian
                        cursor.getString(2), // english
                        cursor.getInt(3)     // audio resource
                );
                words.add(word);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return words;
    }

    public List<Word> getAllWords() {
        List<Word> words = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT " + COLUMN_BELARUSIAN + ", " + COLUMN_RUSSIAN + ", " + COLUMN_ENGLISH + ", " + COLUMN_AUDIO_RESOURCE +
                " FROM " + TABLE_WORDS;

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Word word = new Word(
                        cursor.getString(0), // belarusian
                        cursor.getString(1), // russian
                        cursor.getString(2), // english
                        cursor.getInt(3)     // audio resource
                );
                words.add(word);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return words;
    }

    // Поиск слов с аудио

    public void updateUserActivity(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            // Получаем текущую дату
            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            // Получаем стрик пользователя
            UserStreak streak = getUserStreak(userId);

            if (streak == null) {
                // Если стрик не существует, создаем его
                createUserStreak(userId);
                streak = getUserStreak(userId);
            }

            if (streak != null) {
                String lastActivityDate = streak.getLastActivityDate();

                // Проверяем, была ли активность сегодня
                if (!currentDate.equals(lastActivityDate)) {
                    // Проверяем, был ли вчера активность для продолжения стрика
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.DAY_OF_YEAR, -1);
                    String yesterday = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());

                    if (yesterday.equals(lastActivityDate)) {
                        // Продолжаем стрик
                        streak.setCurrentStreak(streak.getCurrentStreak() + 1);
                    } else {
                        // Сбрасываем стрик (пропущен день)
                        streak.setCurrentStreak(1);
                    }

                    // Обновляем максимальный стрик
                    if (streak.getCurrentStreak() > streak.getMaxStreak()) {
                        streak.setMaxStreak(streak.getCurrentStreak());
                    }

                    // Обновляем дату последней активности
                    streak.setLastActivityDate(currentDate);

                    // Сохраняем изменения
                    updateUserStreak(streak);
                }
            }

        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error updating user activity", e);
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    /**
     * Получает общее количество активных дней пользователя
     */
    public int getTotalActiveDays(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        int totalDays = 0;

        try {
            String query = "SELECT COUNT(DISTINCT " + COLUMN_US_LAST_ACTIVITY_DATE + ") " +
                    "FROM " + TABLE_USER_STREAKS + " " +
                    "WHERE " + COLUMN_US_USER_ID + " = ? AND " +
                    COLUMN_US_LAST_ACTIVITY_DATE + " IS NOT NULL";

            Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

            if (cursor != null && cursor.moveToFirst()) {
                totalDays = cursor.getInt(0);
                cursor.close();
            }

        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error getting total active days", e);
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }

        return totalDays;
    }

    // DatabaseHelper.java - простые методы для работы с нулевыми данными


    /**
     * Получает статистику активности пользователя
     */
    public Map<String, Integer> getUserActivityStats(int userId) {
        Map<String, Integer> stats = new HashMap<>();

        // Обновляем активность
        updateUserActivity(userId);

        // Получаем обновленный стрик
        UserStreak streak = getUserStreak(userId);

        if (streak != null) {
            stats.put("current_streak", streak.getCurrentStreak());
            stats.put("max_streak", streak.getMaxStreak());
            stats.put("total_active_days", getTotalActiveDays(userId));
        } else {
            stats.put("current_streak", 0);
            stats.put("max_streak", 0);
            stats.put("total_active_days", 0);
        }

        return stats;
    }
    public List<Word> searchWords(String query) {
        List<Word> words = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "SELECT " + COLUMN_BELARUSIAN + ", " + COLUMN_RUSSIAN + ", " + COLUMN_ENGLISH + ", " + COLUMN_AUDIO_RESOURCE +
                " FROM " + TABLE_WORDS + " WHERE " +
                COLUMN_BELARUSIAN + " LIKE ? OR " +
                COLUMN_RUSSIAN + " LIKE ? OR " +
                COLUMN_ENGLISH + " LIKE ?";

        String searchQuery = "%" + query + "%";
        Cursor cursor = db.rawQuery(sql, new String[]{searchQuery, searchQuery, searchQuery});

        if (cursor.moveToFirst()) {
            do {
                Word word = new Word(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getInt(3)
                );
                words.add(word);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return words;
    }

    // Получить случайные слова для тренировки с аудио
    public List<Word> getRandomWords(int limit) {
        List<Word> words = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT " + COLUMN_BELARUSIAN + ", " + COLUMN_RUSSIAN + ", " + COLUMN_ENGLISH + ", " + COLUMN_AUDIO_RESOURCE +
                " FROM " + TABLE_WORDS + " ORDER BY RANDOM() LIMIT ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(limit)});

        if (cursor.moveToFirst()) {
            do {
                Word word = new Word(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getInt(3)
                );
                words.add(word);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return words;
    }

    // Статистика (временно возвращаем 0 чтобы не было ошибок)
    public int getLearnedWordsCount() {
        return 0; // Временно
    }

    public int getCompletedLessonsCount() {
        return 0; // Временно
    }

    public User getFirstUser() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                "users",
                new String[]{"id", "first_name", "email"},
                null, null, null, null, "id ASC", "1"
        );

        User user = null;
        if (cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            user.setFirstName(cursor.getString(cursor.getColumnIndexOrThrow("first_name")));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
        }
        cursor.close();
        return user;
    }

    // DatabaseHelper.java
    public long addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("first_name", user.getFirstName());
        values.put("email", user.getEmail());
        values.put("created_date", getCurrentDateTime());

        long userId = db.insert("users", null, values);

        // Создаем запись статистики для нового пользователя
        if (userId != -1) {
            initializeUserStats((int) userId);
        }

        return userId;
    }

    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }
    public List<Word> getPersonalDictionaryWords() {
        List<Word> wordList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Сначала посмотрим структуру таблицы
        debugTableStructure();

        Cursor cursor = null;
        try {
            // Простой запрос без сортировки
            cursor = db.rawQuery("SELECT * FROM personal_dictionary", null);

            Log.d("DEBUG", "Колонки в курсоре: " + (cursor != null ? cursor.getColumnCount() : "cursor is null"));

            if (cursor != null) {
                // Логируем названия всех колонок
                String[] columnNames = cursor.getColumnNames();
                Log.d("DEBUG", "Доступные колонки:");
                for (String columnName : columnNames) {
                    Log.d("DEBUG", "- " + columnName);
                }

                if (cursor.moveToFirst()) {
                    do {
                        Word word = new Word();

                        // Безопасное получение индексов колонок
                        int idIndex = cursor.getColumnIndex("id");
                        int originalIndex = cursor.getColumnIndex("original");
                        int translationIndex = cursor.getColumnIndex("translation");
                        int learnedIndex = cursor.getColumnIndex("is_learned");

                        Log.d("DEBUG", "Индексы колонок - id:" + idIndex + ", original:" + originalIndex +
                                ", translation:" + translationIndex + ", is_learned:" + learnedIndex);

                        // Заполняем только если колонки существуют
                        if (idIndex != -1) {
                            word.setId(cursor.getInt(idIndex));
                        }

                        if (originalIndex != -1) {
                            word.setOriginal(cursor.getString(originalIndex));
                        } else {
                            // Попробуем другие возможные названия
                            originalIndex = cursor.getColumnIndex("word");
                            if (originalIndex != -1) {
                                word.setOriginal(cursor.getString(originalIndex));
                            } else {
                                originalIndex = cursor.getColumnIndex("text");
                                if (originalIndex != -1) {
                                    word.setOriginal(cursor.getString(originalIndex));
                                }
                            }
                        }

                        if (translationIndex != -1) {
                            word.setTranslation(cursor.getString(translationIndex));
                        } else {
                            // Попробуем другие возможные названия
                            translationIndex = cursor.getColumnIndex("meaning");
                            if (translationIndex != -1) {
                                word.setTranslation(cursor.getString(translationIndex));
                            } else {
                                translationIndex = cursor.getColumnIndex("translation_text");
                                if (translationIndex != -1) {
                                    word.setTranslation(cursor.getString(translationIndex));
                                }
                            }
                        }

                        if (learnedIndex != -1) {
                            word.setLearned(cursor.getInt(learnedIndex) == 1);
                        }

                        wordList.add(word);
                        Log.d("DEBUG", "Добавлено слово: " + word.getOriginal() + " - " + word.getTranslation());

                    } while (cursor.moveToNext());
                } else {
                    Log.d("DEBUG", "Курсор пуст - нет данных в таблице");
                }
            }

        } catch (Exception e) {
            Log.e("DEBUG", "Ошибка получения слов: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }

        Log.d("DEBUG", "Всего загружено слов: " + wordList.size());
        return wordList;
    }

    public boolean addWordToPersonalDictionary(Word word) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_PD_BELARUSIAN, word.getBelarusian());
        values.put(COLUMN_PD_RUSSIAN, word.getRussian());
        values.put(COLUMN_PD_ENGLISH, word.getEnglish());
        values.put(COLUMN_PD_EXAMPLE, word.getExample() != null ? word.getExample() : "");
        values.put(COLUMN_PD_STATUS, "learning");
        values.put(COLUMN_PD_ADDED_DATE, getCurrentDateTime());

        long result = db.insert(TABLE_PERSONAL_DICTIONARY, null, values);

        Log.d("DEBUG", "Добавление слова: " + word.getBelarusian() +
                " | Результат: " + (result != -1 ? "УСПЕХ" : "ОШИБКА"));

        return result != -1;
    }

    // ============ МЕТОДЫ ДЛЯ РАБОТЫ С ПОЛЬЗОВАТЕЛЯМИ ============

    // Регистрация нового пользователя
    public boolean registerUser(String email, String firstName, String lastName,
                                String passwordHash, String salt) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_USER_EMAIL, email.toLowerCase());
        values.put(COLUMN_USER_FIRST_NAME, firstName);
        values.put(COLUMN_USER_LAST_NAME, lastName);
        values.put(COLUMN_USER_PASSWORD_HASH, passwordHash);
        values.put(COLUMN_USER_SALT, salt);
        values.put(COLUMN_USER_CREATED_AT, System.currentTimeMillis());
        values.put(COLUMN_USER_IS_ACTIVE, 1);

        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    // Аутентификация пользователя
    public User authenticateUser(String email, String passwordHash) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_USERS +
                " WHERE " + COLUMN_USER_EMAIL + " = ? AND " +
                COLUMN_USER_PASSWORD_HASH + " = ? AND " +
                COLUMN_USER_IS_ACTIVE + " = 1";

        Cursor cursor = db.rawQuery(query, new String[]{email.toLowerCase(), passwordHash});

        if (cursor != null && cursor.moveToFirst()) {
            User user = cursorToUser(cursor);
            cursor.close();

            // Обновляем время последнего входа
            updateLastLogin(user.getId());

            return user;
        }

        if (cursor != null) {
            cursor.close();
        }
        return null;
    }

    // Проверка существования email
    public boolean isEmailRegistered(String email) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT COUNT(*) FROM " + TABLE_USERS +
                " WHERE " + COLUMN_USER_EMAIL + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{email.toLowerCase()});
        int count = 0;

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();

        return count > 0;
    }

    // Получение пользователя по ID


    // Обновление времени последнего входа
    private void updateLastLogin(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_LAST_LOGIN, System.currentTimeMillis());

        db.update(TABLE_USERS, values, COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(userId)});
    }

    // Вспомогательный метод для создания объекта User из Cursor
    @SuppressLint("Range")
    private User cursorToUser(Cursor cursor) {
        User user = new User();
        user.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_USER_ID)));
        user.setEmail(cursor.getString(cursor.getColumnIndex(COLUMN_USER_EMAIL)));
        user.setFirstName(cursor.getString(cursor.getColumnIndex(COLUMN_USER_FIRST_NAME)));
        user.setLastName(cursor.getString(cursor.getColumnIndex(COLUMN_USER_LAST_NAME)));
        user.setCreatedAt(cursor.getLong(cursor.getColumnIndex(COLUMN_USER_CREATED_AT)));
        user.setLastLogin(cursor.getLong(cursor.getColumnIndex(COLUMN_USER_LAST_LOGIN)));
        user.setActive(cursor.getInt(cursor.getColumnIndex(COLUMN_USER_IS_ACTIVE)) == 1);

        return user;
    }

    // ============ МЕТОДЫ ДЛЯ РАБОТЫ С ПРОГРЕССОМ ============

    // Создать прогресс для нового пользователя
    public boolean createUserProgress(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_UP_USER_ID, userId);
        values.put(COLUMN_UP_LAST_UPDATED, System.currentTimeMillis());

        long result = db.insert(TABLE_USER_PROGRESS, null, values);
        db.close();
        return result != -1;
    }

    // Получить прогресс пользователя
    public UserProgress getUserProgress(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_USER_PROGRESS +
                " WHERE " + COLUMN_UP_USER_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        UserProgress progress = null;
        if (cursor != null && cursor.moveToFirst()) {
            progress = cursorToUserProgress(cursor);
            cursor.close();
        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return progress;
    }

    // Обновить прогресс пользователя
    public void updateUserProgress(UserProgress progress) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_UP_LEARNED_WORDS, progress.getLearnedWords());
        values.put(COLUMN_UP_COMPLETED_LESSONS, progress.getCompletedLessons());
        values.put(COLUMN_UP_COMPLETED_ARTICLES, progress.getCompletedArticles());
        values.put(COLUMN_UP_GRAMMAR_EXERCISES, progress.getGrammarExercises());
        values.put(COLUMN_UP_TOTAL_XP, progress.getTotalXp());
        values.put(COLUMN_UP_LAST_UPDATED, System.currentTimeMillis());

        int result = db.update(TABLE_USER_PROGRESS, values,
                COLUMN_UP_USER_ID + " = ?",
                new String[]{String.valueOf(progress.getUserId())});
        db.close();
    }

// ============ МЕТОДЫ ДЛЯ РАБОТЫ СО СТРИКАМИ ============

    // Создать стрик для нового пользователя
    public boolean createUserStreak(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_US_USER_ID, userId);
        values.put(COLUMN_US_LAST_ACTIVITY_DATE, new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
        values.put(COLUMN_US_LAST_LOGIN_DATE, new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));

        long result = db.insert(TABLE_USER_STREAKS, null, values);
        db.close();
        return result != -1;
    }

    // Получить стрик пользователя
    public UserStreak getUserStreak(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_USER_STREAKS +
                " WHERE " + COLUMN_US_USER_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        UserStreak streak = null;
        if (cursor != null && cursor.moveToFirst()) {
            streak = cursorToUserStreak(cursor);
            cursor.close();
        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return streak;
    }

    // Обновить стрик пользователя
    public boolean updateUserStreak(UserStreak streak) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_US_CURRENT_STREAK, streak.getCurrentStreak());
        values.put(COLUMN_US_MAX_STREAK, streak.getMaxStreak());
        values.put(COLUMN_US_LAST_ACTIVITY_DATE, streak.getLastActivityDate());
        values.put(COLUMN_US_LAST_LOGIN_DATE, streak.getLastLoginDate());

        int result = db.update(TABLE_USER_STREAKS, values,
                COLUMN_US_USER_ID + " = ?",
                new String[]{String.valueOf(streak.getUserId())});
        db.close();
        return result > 0;
    }

// ============ ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ============

    @SuppressLint("Range")
    private UserProgress cursorToUserProgress(Cursor cursor) {
        UserProgress progress = new UserProgress();

        progress.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_UP_ID)));
        progress.setUserId(cursor.getInt(cursor.getColumnIndex(COLUMN_UP_USER_ID)));
        progress.setLearnedWords(cursor.getInt(cursor.getColumnIndex(COLUMN_UP_LEARNED_WORDS)));
        progress.setCompletedLessons(cursor.getInt(cursor.getColumnIndex(COLUMN_UP_COMPLETED_LESSONS)));
        progress.setCompletedArticles(cursor.getInt(cursor.getColumnIndex(COLUMN_UP_COMPLETED_ARTICLES)));
        progress.setGrammarExercises(cursor.getInt(cursor.getColumnIndex(COLUMN_UP_GRAMMAR_EXERCISES)));
        progress.setTotalXp(cursor.getInt(cursor.getColumnIndex(COLUMN_UP_TOTAL_XP)));
        progress.setLastUpdated(cursor.getLong(cursor.getColumnIndex(COLUMN_UP_LAST_UPDATED)));

        return progress;
    }

    @SuppressLint("Range")
    private UserStreak cursorToUserStreak(Cursor cursor) {
        UserStreak streak = new UserStreak();

        streak.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_US_ID)));
        streak.setUserId(cursor.getInt(cursor.getColumnIndex(COLUMN_US_USER_ID)));
        streak.setCurrentStreak(cursor.getInt(cursor.getColumnIndex(COLUMN_US_CURRENT_STREAK)));
        streak.setMaxStreak(cursor.getInt(cursor.getColumnIndex(COLUMN_US_MAX_STREAK)));
        streak.setLastActivityDate(cursor.getString(cursor.getColumnIndex(COLUMN_US_LAST_ACTIVITY_DATE)));
        streak.setLastLoginDate(cursor.getString(cursor.getColumnIndex(COLUMN_US_LAST_LOGIN_DATE)));

        return streak;
    }
}

