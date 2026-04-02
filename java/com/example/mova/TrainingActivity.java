package com.example.mova;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class TrainingActivity extends AppCompatActivity {

    // UI элементы
    private TextView tvProgress, tvScore, tvTranslationQuestion, tvListeningQuestion,
            tvSpellingQuestion, tvResult, tvCorrectAnswer;
    private EditText etTranslationAnswer, etListeningAnswer, etSpellingAnswer;
    private Button btnTranslationCheck, btnListeningCheck, btnSpellingCheck,
            btnListeningNext, btnResultNext, btnPlayAudio;
    private ImageView ivResult;
    private CardView cardTranslation, cardListening, cardSpelling;
    private LinearLayout layoutTrainingSelection, layoutTranslationExercise,
            layoutListeningExercise, layoutSpellingExercise, layoutResult;

    // Данные тренировки
    private DatabaseHelper dbHelper;
    private List<TranslationExercise> translationExercises;
    private List<ListeningExercise> listeningExercises;
    private List<SpellingExercise> spellingExercises;
    private int currentExercise = 0;
    private int score = 0;
    private final int totalExercises = 10;
    private String currentTrainingType = "";
    private MediaPlayer mediaPlayer;
    private Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        dbHelper = new DatabaseHelper(this);
        initViews();
        setupClickListeners();
        initExercises();
        updateProgress();
    }

    private void initViews() {
        tvProgress = findViewById(R.id.tvProgress);
        tvScore = findViewById(R.id.tvScore);
        tvTranslationQuestion = findViewById(R.id.tvTranslationQuestion);
        tvListeningQuestion = findViewById(R.id.tvListeningQuestion);
        tvSpellingQuestion = findViewById(R.id.tvSpellingQuestion);
        tvResult = findViewById(R.id.tvResult);
        tvCorrectAnswer = findViewById(R.id.tvCorrectAnswer);

        etTranslationAnswer = findViewById(R.id.etTranslationAnswer);
        etListeningAnswer = findViewById(R.id.etListeningAnswer);
        etSpellingAnswer = findViewById(R.id.etSpellingAnswer);

        btnTranslationCheck = findViewById(R.id.btnTranslationCheck);
        btnListeningCheck = findViewById(R.id.btnListeningCheck);
        btnSpellingCheck = findViewById(R.id.btnSpellingCheck);
        btnListeningNext = findViewById(R.id.btnListeningNext);
        btnResultNext = findViewById(R.id.btnResultNext);
        btnPlayAudio = findViewById(R.id.btnPlayAudio);

        ivResult = findViewById(R.id.ivResult);

        cardTranslation = findViewById(R.id.cardTranslation);
        cardListening = findViewById(R.id.cardListening);
        cardSpelling = findViewById(R.id.cardSpelling);

        layoutTrainingSelection = findViewById(R.id.layoutTrainingSelection);
        layoutTranslationExercise = findViewById(R.id.layoutTranslationExercise);
        layoutListeningExercise = findViewById(R.id.layoutListeningExercise);
        layoutSpellingExercise = findViewById(R.id.layoutSpellingExercise);
        layoutResult = findViewById(R.id.layoutResult);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    private void setupClickListeners() {
        cardTranslation.setOnClickListener(v -> startTranslationTraining());
        cardListening.setOnClickListener(v -> startListeningTraining());
        cardSpelling.setOnClickListener(v -> startSpellingTraining());

        btnTranslationCheck.setOnClickListener(v -> checkAnswer());
        btnListeningCheck.setOnClickListener(v -> checkAnswer());
        btnSpellingCheck.setOnClickListener(v -> checkAnswer());

        btnListeningNext.setOnClickListener(v -> nextExercise());
        btnResultNext.setOnClickListener(v -> nextExercise());

        btnPlayAudio.setOnClickListener(v -> playCurrentAudio());
    }

    private void initExercises() {
        translationExercises = new ArrayList<>();
        listeningExercises = new ArrayList<>();
        spellingExercises = new ArrayList<>();

        List<Word> words = dbHelper.getAllWords();
        if (words.isEmpty()) {
            Toast.makeText(this, "Нет слов в базе данных", Toast.LENGTH_SHORT).show();
            return;
        }

        // Перемешиваем слова
        Collections.shuffle(words);

        for (Word word : words) {
            // Упражнения на перевод
            translationExercises.add(new TranslationExercise(
                    word.getBelarusian(),
                    word.getRussian(),
                    "belarusian"
            ));

            // Упражнения на аудирование (только если есть аудио)
            if (word.getAudioResourceId() != 0) {
                listeningExercises.add(new ListeningExercise(
                        word.getBelarusian(),
                        word.getRussian(),
                        word.getAudioResourceId()
                ));
            }

            // Упражнения на правописание (только для слов длиной от 4 символов)
            if (word.getBelarusian().length() >= 4) {
                SpellingExercise spellingExercise = createSpellingExercise(word);
                if (spellingExercise != null) {
                    spellingExercises.add(spellingExercise);
                }
            }
        }

        // Ограничиваем количество упражнений
        if (translationExercises.size() > 20) {
            translationExercises = translationExercises.subList(0, 20);
        }
        if (listeningExercises.size() > 20) {
            listeningExercises = listeningExercises.subList(0, 20);
        }
        if (spellingExercises.size() > 20) {
            spellingExercises = spellingExercises.subList(0, 20);
        }

        Collections.shuffle(translationExercises);
        Collections.shuffle(listeningExercises);
        Collections.shuffle(spellingExercises);
    }

    private SpellingExercise createSpellingExercise(Word word) {
        String belarusianWord = word.getBelarusian();

        // Выбираем случайную позицию для пропуска (кроме первой и последней буквы)
        int missingPosition = random.nextInt(belarusianWord.length() - 2) + 1;
        char missingChar = belarusianWord.charAt(missingPosition);

        // Создаем слово с пропуском
        StringBuilder wordWithGap = new StringBuilder();
        for (int i = 0; i < belarusianWord.length(); i++) {
            if (i == missingPosition) {
                wordWithGap.append("_");
            } else {
                wordWithGap.append(belarusianWord.charAt(i));
            }
        }

        return new SpellingExercise(
                "Вставьте пропущенную букву:\n" + wordWithGap.toString(),
                String.valueOf(missingChar),
                "Перевод: " + word.getRussian(),
                belarusianWord
        );
    }

    private void startTranslationTraining() {
        currentTrainingType = "translation";
        hideAllExerciseLayouts();
        layoutTranslationExercise.setVisibility(View.VISIBLE);
        showCurrentExercise();
    }

    private void startListeningTraining() {
        if (listeningExercises.isEmpty()) {
            Toast.makeText(this, "Нет доступных аудио упражнений", Toast.LENGTH_SHORT).show();
            return;
        }
        currentTrainingType = "listening";
        hideAllExerciseLayouts();
        layoutListeningExercise.setVisibility(View.VISIBLE);
        showCurrentExercise();
    }

    private void startSpellingTraining() {
        if (spellingExercises.isEmpty()) {
            Toast.makeText(this, "Нет доступных упражнений на правописание", Toast.LENGTH_SHORT).show();
            return;
        }
        currentTrainingType = "spelling";
        hideAllExerciseLayouts();
        layoutSpellingExercise.setVisibility(View.VISIBLE);
        showCurrentExercise();
    }

    private void hideAllExerciseLayouts() {
        layoutTrainingSelection.setVisibility(View.GONE);
        layoutTranslationExercise.setVisibility(View.GONE);
        layoutListeningExercise.setVisibility(View.GONE);
        layoutSpellingExercise.setVisibility(View.GONE);
        layoutResult.setVisibility(View.GONE);
    }

    private void showCurrentExercise() {
        if (currentExercise < getCurrentExercises().size() && currentExercise < totalExercises) {
            clearAnswerField();

            switch (currentTrainingType) {
                case "translation":
                    TranslationExercise transExercise = translationExercises.get(currentExercise);
                    tvTranslationQuestion.setText("Переведите на русский:\n" + transExercise.getWord());
                    break;

                case "listening":
                    ListeningExercise listenExercise = listeningExercises.get(currentExercise);
                    tvListeningQuestion.setText("Прослушайте слово и введите перевод на русский");
                    playAudio(listenExercise.getAudioResource());
                    break;

                case "spelling":
                    SpellingExercise spellExercise = spellingExercises.get(currentExercise);
                    tvSpellingQuestion.setText(spellExercise.getQuestion() +
                            "\n\n" + spellExercise.getHint());
                    break;
            }

            updateProgress();
        } else {
            finishTraining();
        }
    }

    private void clearAnswerField() {
        switch (currentTrainingType) {
            case "translation":
                etTranslationAnswer.setText("");
                etTranslationAnswer.requestFocus();
                break;
            case "listening":
                etListeningAnswer.setText("");
                etListeningAnswer.requestFocus();
                break;
            case "spelling":
                etSpellingAnswer.setText("");
                etSpellingAnswer.requestFocus();
                break;
        }
    }

    private List<?> getCurrentExercises() {
        switch (currentTrainingType) {
            case "translation": return translationExercises;
            case "listening": return listeningExercises;
            case "spelling": return spellingExercises;
            default: return translationExercises;
        }
    }

    private void playCurrentAudio() {
        if (currentTrainingType.equals("listening") && currentExercise < listeningExercises.size()) {
            ListeningExercise exercise = listeningExercises.get(currentExercise);
            playAudio(exercise.getAudioResource());
        }
    }

    private void playAudio(int audioResource) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }
            mediaPlayer = MediaPlayer.create(this, audioResource);
            mediaPlayer.setOnCompletionListener(mp -> {
                Toast.makeText(this, "Повторите аудио если нужно", Toast.LENGTH_SHORT).show();
            });
            mediaPlayer.start();
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка воспроизведения аудио", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    private void checkAnswer() {
        String userAnswer = getUserAnswer();
        if (userAnswer.isEmpty()) {
            Toast.makeText(this, "Введите ответ!", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isCorrect = false;
        String correctAnswer = "";

        switch (currentTrainingType) {
            case "translation":
                TranslationExercise transExercise = translationExercises.get(currentExercise);
                isCorrect = userAnswer.equalsIgnoreCase(transExercise.getTranslation());
                correctAnswer = transExercise.getTranslation();
                break;

            case "listening":
                ListeningExercise listenExercise = listeningExercises.get(currentExercise);
                isCorrect = userAnswer.equalsIgnoreCase(listenExercise.getTranslation());
                correctAnswer = listenExercise.getTranslation() + " (" + listenExercise.getWord() + ")";
                break;

            case "spelling":
                SpellingExercise spellExercise = spellingExercises.get(currentExercise);
                isCorrect = userAnswer.equalsIgnoreCase(spellExercise.getCorrectAnswer());
                correctAnswer = spellExercise.getFullWord();
                break;
        }

        showResult(isCorrect, correctAnswer);
        updateProgress();
    }

    private String getUserAnswer() {
        switch (currentTrainingType) {
            case "translation":
                return etTranslationAnswer.getText().toString().trim();
            case "listening":
                return etListeningAnswer.getText().toString().trim();
            case "spelling":
                return etSpellingAnswer.getText().toString().trim();
            default:
                return "";
        }
    }

    private void showResult(boolean isCorrect, String correctAnswer) {
        hideAllExerciseLayouts();
        layoutResult.setVisibility(View.VISIBLE);

        if (isCorrect) {
            score += 10;
            ivResult.setImageResource(R.drawable.ic_correct);
            tvResult.setText("Правильно!");
            tvResult.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            tvCorrectAnswer.setText("");
        } else {
            ivResult.setImageResource(R.drawable.ic_incorrect);
            tvResult.setText("Неправильно");
            tvResult.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            tvCorrectAnswer.setText("Правильный ответ: " + correctAnswer);
        }
    }

    private void nextExercise() {
        currentExercise++;

        if (currentExercise < totalExercises && currentExercise < getCurrentExercises().size()) {
            hideAllExerciseLayouts();
            showCurrentLayout();
            showCurrentExercise();
        } else {
            finishTraining();
        }
    }

    private void showCurrentLayout() {
        switch (currentTrainingType) {
            case "translation":
                layoutTranslationExercise.setVisibility(View.VISIBLE);
                break;
            case "listening":
                layoutListeningExercise.setVisibility(View.VISIBLE);
                break;
            case "spelling":
                layoutSpellingExercise.setVisibility(View.VISIBLE);
                break;
        }
    }

    @SuppressLint("SetTextI18n")
    private void updateProgress() {
        tvProgress.setText((currentExercise + 1) + "/" + totalExercises);
        tvScore.setText("Очки: " + score);
    }

    @SuppressLint("SetTextI18n")
    private void finishTraining() {
        hideAllExerciseLayouts();
        layoutResult.setVisibility(View.VISIBLE);

        ivResult.setImageResource(score >= 70 ? R.drawable.ic_star : R.drawable.ic_trophy);
        tvResult.setText("Тренировка завершена!");
        tvResult.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));

        String resultMessage = getResultMessage();
        tvCorrectAnswer.setText(resultMessage + "\nВаш результат: " + score + " баллов");

        btnResultNext.setText("Завершить");
        btnResultNext.setOnClickListener(v -> finish());
    }

    private String getResultMessage() {
        if (score >= 90) {
            return "Отлично! 🎉";
        } else if (score >= 70) {
            return "Хорошо! 👍";
        } else if (score >= 50) {
            return "Можно лучше! 💪";
        } else {
            return "Попробуйте еще раз! 📚";
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    // Классы упражнений
    private static class TranslationExercise {
        private final String word, translation, language;
        public TranslationExercise(String word, String translation, String language) {
            this.word = word; this.translation = translation; this.language = language;
        }
        public String getWord() { return word; }
        public String getTranslation() { return translation; }
        public String getLanguage() { return language; }
    }

    private static class ListeningExercise {
        private final String word, translation;
        private final int audioResource;
        public ListeningExercise(String word, String translation, int audioResource) {
            this.word = word; this.translation = translation; this.audioResource = audioResource;
        }
        public String getWord() { return word; }
        public String getTranslation() { return translation; }
        public int getAudioResource() { return audioResource; }
    }

    private static class SpellingExercise {
        private final String question;
        private final String correctAnswer;
        private final String hint;
        private final String fullWord;

        public SpellingExercise(String question, String correctAnswer, String hint, String fullWord) {
            this.question = question;
            this.correctAnswer = correctAnswer;
            this.hint = hint;
            this.fullWord = fullWord;
        }

        public String getQuestion() { return question; }
        public String getCorrectAnswer() { return correctAnswer; }
        public String getHint() { return hint; }
        public String getFullWord() { return fullWord; }
    }
}