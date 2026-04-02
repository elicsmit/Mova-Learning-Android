package com.example.mova;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class GrammarPracticeActivity extends AppCompatActivity {
    private TextView tvQuestion, tvTopic;
    private Button btnOption1, btnOption2, btnOption3, btnOption4;
    private int currentQuestion = 0;
    private int score = 0;
    private String topic;
    private UserDataManager userDataManager;

    private List<PracticeQuestion> questions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grammar_practice);

        // Инициализация UserDataManager
        userDataManager = new UserDataManager(this);

        topic = getIntent().getStringExtra("topic");

        tvTopic = findViewById(R.id.tvTopic);
        tvQuestion = findViewById(R.id.tvQuestion);
        btnOption1 = findViewById(R.id.btnOption1);
        btnOption2 = findViewById(R.id.btnOption2);
        btnOption3 = findViewById(R.id.btnOption3);
        btnOption4 = findViewById(R.id.btnOption4);

        tvTopic.setText("Практыкаванне: " + topic);

        loadQuestionsForTopic();
        showQuestion();

        // Обработчики кнопок
        btnOption1.setOnClickListener(v -> checkAnswer(0));
        btnOption2.setOnClickListener(v -> checkAnswer(1));
        btnOption3.setOnClickListener(v -> checkAnswer(2));
        btnOption4.setOnClickListener(v -> checkAnswer(3));
    }

    private void loadQuestionsForTopic() {
        questions = new ArrayList<>();

        switch (topic) {
            case "Назоўнік":
                questions.add(new PracticeQuestion(
                        "Які род назоўніка 'сталіца'?",
                        new String[]{"Мужчынскі", "Жаночы", "Ніякі", "Агульны"},
                        1
                ));
                questions.add(new PracticeQuestion(
                        "У якім склоне назоўнік 'вучнем'?",
                        new String[]{"Назоўны", "Родны", "Давальны", "Творны"},
                        3
                ));
                questions.add(new PracticeQuestion(
                        "Які лік назоўніка 'дзеці'?",
                        new String[]{"Адзіночны", "Множны", "Нявызначаны", "Агульны"},
                        1
                ));
                break;

            case "Дзеяслоў":
                questions.add(new PracticeQuestion(
                        "Які час дзеяслова 'чытаў'?",
                        new String[]{"Цяперашні", "Мінулы", "Будучы", "Нявызначаны"},
                        1
                ));
                questions.add(new PracticeQuestion(
                        "Выберыце дзеяслоў у загадным ладзе:",
                        new String[]{"чытаю", "чытаеш", "чытай", "чыталі"},
                        2
                ));
                questions.add(new PracticeQuestion(
                        "Якая асоба дзеяслова 'пішам'?",
                        new String[]{"1-я асоба", "2-я асоба", "3-я асоба", "Нявызначаная"},
                        0
                ));
                break;

            case "Прыметнік":
                questions.add(new PracticeQuestion(
                        "Якая ступень параўнання 'лепшы'?",
                        new String[]{"Станоўчая", "Вышэйшая", "Найвышэйшая", "Няправільная"},
                        1
                ));
                questions.add(new PracticeQuestion(
                        "Выберыце кароткую форму прыметніка:",
                        new String[]{"добры", "лепшы", "рад", "прыгожы"},
                        2
                ));
                questions.add(new PracticeQuestion(
                        "Які прыметнік з'яўляецца адносным?",
                        new String[]{"прыгожы", "драўляны", "вялікі", "цёплы"},
                        1
                ));
                break;

            case "Лічэбнік":
                questions.add(new PracticeQuestion(
                        "Які тып лічэбніка 'трэці'?",
                        new String[]{"Колькасны", "Парадкавы", "Дробны", "Зборны"},
                        1
                ));
                questions.add(new PracticeQuestion(
                        "Выберыце зборны лічэбнік:",
                        new String[]{"два", "другое", "двое", "двух"},
                        2
                ));
                questions.add(new PracticeQuestion(
                        "Які лічэбнік з'яўляецца дробным?",
                        new String[]{"пяць", "пяты", "палова", "пяцёра"},
                        2
                ));
                break;

            case "Займеннік":
                questions.add(new PracticeQuestion(
                        "Які тып займенніка 'мой'?",
                        new String[]{"Асабовы", "Прыналежны", "Указальны", "Адносны"},
                        1
                ));
                questions.add(new PracticeQuestion(
                        "Выберыце адносны займеннік:",
                        new String[]{"я", "той", "хто", "гэты"},
                        2
                ));
                questions.add(new PracticeQuestion(
                        "Які займеннік з'яўляецца асабовым?",
                        new String[]{"мой", "гэты", "я", "які"},
                        2
                ));
                break;

            case "Прыслоўе":
                questions.add(new PracticeQuestion(
                        "Які тып прыслоўя 'учора'?",
                        new String[]{"Часу", "Месца", "Спосабу", "Прычыны"},
                        0
                ));
                questions.add(new PracticeQuestion(
                        "Выберыце прыслоўе месца:",
                        new String[]{"хутка", "учора", "тут", "таму"},
                        2
                ));
                questions.add(new PracticeQuestion(
                        "Якое прыслоўе выражае спосаб дзеяння?",
                        new String[]{"сёння", "тут", "прыгожа", "таму"},
                        2
                ));
                break;

            case "Дзеепрыслоўе":
                questions.add(new PracticeQuestion(
                        "Што абазначае дзеепрыслоўе?",
                        new String[]{"Прадмет", "Дзеянне", "Дадатковае дзеянне", "Прыкмету"},
                        2
                ));
                questions.add(new PracticeQuestion(
                        "Выберыце дзеепрыслоўе:",
                        new String[]{"чытаючы", "чытаны", "чытаць", "чытаю"},
                        0
                ));
                questions.add(new PracticeQuestion(
                        "Якое дзеепрыслоўе ўтворана ад дзеяслова 'ісці'?",
                        new String[]{"ідучы", "ішоўшы", "ідзі", "ісці"},
                        0
                ));
                break;

            case "Дзеепрыметнік":
                questions.add(new PracticeQuestion(
                        "Што абазначае дзеепрыметнік?",
                        new String[]{"Асноўнае дзеянне", "Прыкмету па дзеянні", "Стан прадмета", "Колькасць"},
                        1
                ));
                questions.add(new PracticeQuestion(
                        "Выберыце дзеепрыметнік:",
                        new String[]{"чытаны", "чытаючы", "чытаць", "чытае"},
                        0
                ));
                questions.add(new PracticeQuestion(
                        "Які дзеепрыметнік утвораны ад дзеяслова 'будаваць'?",
                        new String[]{"будаваны", "будучы", "будаваўшы", "будаваць"},
                        0
                ));
                break;

            case "Складаназалежныя сказ":
                questions.add(new PracticeQuestion(
                        "Колькі граматычных асноў у складаназалежным сказе?",
                        new String[]{"Адна", "Дзве", "Тры", "Чатыры"},
                        1
                ));
                questions.add(new PracticeQuestion(
                        "Якая частка складаназалежнага сказа з'яўляецца галоўнай?",
                        new String[]{"Тай, што стаіць першай", "Тай, што стаіць другой", "Тай, што не мае злучніка", "Тай, што мае злучнік"},
                        2
                ));
                questions.add(new PracticeQuestion(
                        "Выберыце складаназалежны сказ:",
                        new String[]{"Я іду ў школу", "Я ведаю, што ты прыйдзеш", "Сонца свеціць", "Дзеці гуляюць"},
                        1
                ));
                break;

            case "Безасабовая форма дзеяслова":
                questions.add(new PracticeQuestion(
                        "Што абазначае безасабовы дзеяслоў?",
                        new String[]{"Дзеянне канкрэтнага суб'екта", "Дзеянне без суб'екта", "Стан прадмета", "Прыкмету"},
                        1
                ));
                questions.add(new PracticeQuestion(
                        "Выберыце безасабовы дзеяслоў:",
                        new String[]{"я іду", "святлее", "ён спіць", "яна чытае"},
                        1
                ));
                questions.add(new PracticeQuestion(
                        "Які дзеяслоў выражае стан прыроды?",
                        new String[]{"я вучуся", "святлее", "ён працуе", "яны гуляюць"},
                        1
                ));
                break;

            case "Зваротак":
                questions.add(new PracticeQuestion(
                        "Што такое зваротак?",
                        new String[]{"Галоўны член сказа", "Слова, якое называе таго, да каго звяртаюцца", "Даданы член сказа", "Частка складанага сказа"},
                        1
                ));
                questions.add(new PracticeQuestion(
                        "Як выдзяляецца зваротак на пісьме?",
                        new String[]{"Дужкамі", "Двукроп'ем", "Коскамі", "Кропкай"},
                        2
                ));
                questions.add(new PracticeQuestion(
                        "Выберыце сказ са звароткам:",
                        new String[]{"Я іду ў школу", "Дружа, дапамажы мне", "Сонца свеціць ярка", "Кніга ляжыць на стале"},
                        1
                ));
                break;

            case "Інфінітыў":
                questions.add(new PracticeQuestion(
                        "Што такое інфінітыў?",
                        new String[]{"Вызначаная форма дзеяслова", "Нявызначаная форма дзеяслова", "Форма цяперашняга часу", "Форма мінулага часу"},
                        1
                ));
                questions.add(new PracticeQuestion(
                        "На якія пытанні адказвае інфінітыў?",
                        new String[]{"Што робіць?", "Што зрабіў?", "Што рабіць? Што зрабіць?", "Які?"},
                        2
                ));
                questions.add(new PracticeQuestion(
                        "Выберыце інфінітыў:",
                        new String[]{"чытаю", "чытаў", "чытаць", "чытай"},
                        2
                ));
                break;

            case "Аднародныя члены сказа":
                questions.add(new PracticeQuestion(
                        "Што такое аднародныя члены сказа?",
                        new String[]{"Члены сказа з рознымі функцыямі", "Члены сказа з аднолькавымі функцыямі", "Галоўныя члены сказа", "Даданыя члены сказа"},
                        1
                ));
                questions.add(new PracticeQuestion(
                        "Які знак прыпынку ставіцца паміж аднароднымі членамі?",
                        new String[]{"Кропка", "Двукроп'е", "Коска", "Кропка з коскай"},
                        2
                ));
                questions.add(new PracticeQuestion(
                        "Выберыце сказ з аднароднымі членамі:",
                        new String[]{"Я іду", "На стале ляжалі кнігі, сшыткі і ручкі", "Сонца свеціць", "Ён прыйшоў"},
                        1
                ));
                break;

            case "Злучнік":
                questions.add(new PracticeQuestion(
                        "Што такое злучнік?",
                        new String[]{"Самастойная частка мовы", "Службовая частка мовы", "Член сказа", "Частка слова"},
                        1
                ));
                questions.add(new PracticeQuestion(
                        "Якія бываюць злучнікі?",
                        new String[]{"Сузінальныя і падпарадкавальныя", "Асабовыя і неасабовыя", "Якасныя і адносныя", "Галоўныя і даданыя"},
                        0
                ));
                questions.add(new PracticeQuestion(
                        "Выберыце сузінальны злучнік:",
                        new String[]{"што", "калі", "і", "каб"},
                        2
                ));
                break;

            case "Выклічнік":
                questions.add(new PracticeQuestion(
                        "Што выражае выклічнік?",
                        new String[]{"Прадмет", "Дзеянне", "Прыкмету", "Пачуцці і эмоцыі"},
                        3
                ));
                questions.add(new PracticeQuestion(
                        "Як выдзяляюцца выклічнікі на пісьме?",
                        new String[]{"Коскамі", "Дужкамі", "Клічнікам", "Двукроп'ем"},
                        2
                ));
                questions.add(new PracticeQuestion(
                        "Выберыце выклічнік:",
                        new String[]{"дом", "чытаць", "ой", "прыгожы"},
                        2
                ));
                break;

            case "Частка":
                questions.add(new PracticeQuestion(
                        "Што такое частка?",
                        new String[]{"Самастойная частка мовы", "Службовая частка мовы для выразання адценняў", "Галоўны член сказа", "Частка слова"},
                        1
                ));
                questions.add(new PracticeQuestion(
                        "Якая частка з'яўляецца адмоўнай?",
                        new String[]{"нават", "жа", "не", "бы"},
                        2
                ));
                questions.add(new PracticeQuestion(
                        "Выберыце ўзмацняльную частку:",
                        new String[]{"не", "ні", "нават", "каб"},
                        2
                ));
                break;

            case "Прыназоўнік":
                questions.add(new PracticeQuestion(
                        "Што такое прыназоўнік?",
                        new String[]{"Самастойная частка мовы", "Службовая частка мовы", "Член сказа", "Частка слова"},
                        1
                ));
                questions.add(new PracticeQuestion(
                        "З якімі часткамі мовы ўжываюцца прыназоўнікі?",
                        new String[]{"З дзеясловамі", "З назоўнікамі і займеннікамі", "З прыметнікамі", "З прыслоўямі"},
                        1
                ));
                questions.add(new PracticeQuestion(
                        "Выберыце прыназоўнік:",
                        new String[]{"у", "дом", "чытаць", "прыгожы"},
                        0
                ));
                break;

            case "Заменнік":
                questions.add(new PracticeQuestion(
                        "Што такое заменнік?",
                        new String[]{"Новая частка мовы", "Частка мовы для замены іншых частак", "Службовая частка мовы", "Член сказа"},
                        1
                ));
                questions.add(new PracticeQuestion(
                        "Які заменнік з'яўляецца прыналежным?",
                        new String[]{"я", "мой", "гэты", "хто"},
                        1
                ));
                questions.add(new PracticeQuestion(
                        "Выберыце азначальны заменнік:",
                        new String[]{"я", "той", "сам", "хто"},
                        2
                ));
                break;

            case "Сказ":
                questions.add(new PracticeQuestion(
                        "Што такое сказ?",
                        new String[]{"Спалучэнне слоў", "Сінтаксічная адзінка, якая выражае закончаную думку", "Частка мовы", "Член сказа"},
                        1
                ));
                questions.add(new PracticeQuestion(
                        "Колькі асноўных членаў у няпоўным сказе?",
                        new String[]{"Два", "Адзін", "Тры", "Ніводнага"},
                        1
                ));
                questions.add(new PracticeQuestion(
                        "Выберыце сказ:",
                        new String[]{"прыгожы", "чытаць", "Сонца свеціць", "у доме"},
                        2
                ));
                break;

            case "Словазлучэнне":
                questions.add(new PracticeQuestion(
                        "Што такое словазлучэнне?",
                        new String[]{"Асобнае слова", "Спалучэнне двух і больш значных слоў", "Член сказа", "Частка мовы"},
                        1
                ));
                questions.add(new PracticeQuestion(
                        "Які від сувязі ў словазлучэнні 'чытаць кнігу'?",
                        new String[]{"Прымяненне", "Прыляганне", "Кіраванне", "Сувязь"},
                        2
                ));
                questions.add(new PracticeQuestion(
                        "Выберыце словазлучэнне:",
                        new String[]{"дом", "чытаць кнігу", "ён", "прыгожы"},
                        1
                ));
                break;

            case "Словаўтварэнне":
                questions.add(new PracticeQuestion(
                        "Што вывучае словаўтварэнне?",
                        new String[]{"Сінтаксіс", "Марфалогію", "Спосабы ўтварэння новых слоў", "Фанетыку"},
                        2
                ));
                questions.add(new PracticeQuestion(
                        "Які спосаб словаўтварэння ў слове 'чытальнік'?",
                        new String[]{"Прэфіксальны", "Суфіксальны", "Складанне", "Пераход"},
                        1
                ));
                questions.add(new PracticeQuestion(
                        "Якое слова ўтворана складаннем?",
                        new String[]{"чытальнік", "лёдаход", "перачытаць", "чытаны"},
                        1
                ));
                break;

            case "Лічэбнік (колькасныя і парадкавыя)":
                questions.add(new PracticeQuestion(
                        "Які лічэбнік абазначае парадак?",
                        new String[]{"Колькасны", "Парадкавы", "Дробны", "Зборны"},
                        1
                ));
                questions.add(new PracticeQuestion(
                        "Выберыце парадкавы лічэбнік:",
                        new String[]{"адзін", "першы", "палова", "двое"},
                        1
                ));
                questions.add(new PracticeQuestion(
                        "Які лічэбнік з'яўляецца складаным?",
                        new String[]{"пяць", "пяты", "пяцёра", "пяцьдзесят"},
                        3
                ));
                break;

            case "Прыметнік (якасныя і адносныя)":
                questions.add(new PracticeQuestion(
                        "Які прыметнік мае ступені параўнання?",
                        new String[]{"Якасны", "Адносны", "Прыналежны", "Усе"},
                        0
                ));
                questions.add(new PracticeQuestion(
                        "Выберыце якасны прыметнік:",
                        new String[]{"драўляны", "прыгожы", "матулін", "гарадскі"},
                        1
                ));
                questions.add(new PracticeQuestion(
                        "Які прыметнік з'яўляецца адносным?",
                        new String[]{"добры", "разумны", "шкляны", "вясёлы"},
                        2
                ));
                break;

            case "Дзеяслоў (спражэнне па асабах)":
                questions.add(new PracticeQuestion(
                        "Колькі асабовых формаў у дзеяслова?",
                        new String[]{"Дзве", "Тры", "Шэсць", "Дзевяць"},
                        2
                ));
                questions.add(new PracticeQuestion(
                        "Якая асаба ў формы 'чытаеш'?",
                        new String[]{"1-я", "2-я", "3-я", "Нявызначаная"},
                        1
                ));
                questions.add(new PracticeQuestion(
                        "Выберыце форму 3-й асобы множнага ліку:",
                        new String[]{"я чытаю", "ты чытаеш", "ён чытае", "яны чытаюць"},
                        3
                ));
                break;
        }
    }

    private void showQuestion() {
        if (currentQuestion < questions.size()) {
            PracticeQuestion question = questions.get(currentQuestion);
            tvQuestion.setText(question.getQuestion());
            btnOption1.setText(question.getOptions()[0]);
            btnOption2.setText(question.getOptions()[1]);
            btnOption3.setText(question.getOptions()[2]);
            btnOption4.setText(question.getOptions()[3]);
        } else {
            showResults();
        }
    }

    private void checkAnswer(int selectedOption) {
        PracticeQuestion question = questions.get(currentQuestion);
        if (selectedOption == question.getCorrectAnswer()) {
            score++;
            Toast.makeText(this, "Правільна! ✅", Toast.LENGTH_SHORT).show();
            userDataManager.addPracticeCompleted();
        } else {
            Toast.makeText(this, "Няправільна! ❌", Toast.LENGTH_SHORT).show();
        }

        // Добавляем задержку перед переходом к следующему вопросу
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        currentQuestion++;
                        showQuestion();
                    }
                },
                1000 // Задержка 1 секунда для показа Toast
        );
    }

    private void showResults() {
        // ОБНОВЛЯЕМ СТАТИСТИКУ ПЕРЕД ПОКАЗОМ РЕЗУЛЬТАТОВ
        updatePracticeStats();

        // ДОБАВИТЬ: Засчитываем завершение урока
        userDataManager.addCompletedLesson();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Вынікі практыкавання")
                .setMessage("Вы адказалі правільна на " + score + " з " + questions.size() + " пытанняў!\n\n" +
                        "Тэма: " + topic + "\n\n" +
                        getResultMessage())
                .setPositiveButton("Закрыць", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    private void updatePracticeStats() {
        // Теперь вся логика в UserDataManager
        // Просто показываем уведомление
        Toast.makeText(this, "Практыкаванне завершана! +50 вопыту", Toast.LENGTH_SHORT).show();
    }

    private String getResultMessage() {
        double percentage = (double) score / questions.size() * 100;
        if (percentage >= 80) {
            return "Выдатны вынік! Вы добра ведаеце тэму! 🎉";
        } else if (percentage >= 60) {
            return "Добры вынік! Трэба яшчэ папрацаваць! 👍";
        } else {
            return "Трэба паўтарыць тэму! Не спыняйцеся! 💪";
        }
    }
}