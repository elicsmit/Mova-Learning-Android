package com.example.mova;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

public class TopicDetailActivity extends AppCompatActivity {
    private TextView tvDescription;
    private TextView tvExamples;
    private Button btnMarkCompleted, btnPractice;
    private boolean isCompleted = false;
    private String currentTopic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_detail);

        // Кнопка назад
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Инициализация
        TextView tvTitle = findViewById(R.id.tvTopicTitle);
        tvDescription = findViewById(R.id.tvTopicContent);
        tvExamples = findViewById(R.id.tvExamples);
        btnMarkCompleted = findViewById(R.id.btnMarkCompleted);
        btnPractice = findViewById(R.id.btnPractice);

        // Получаем данные
        currentTopic = getIntent().getStringExtra("topic_title");
        String topicDescription = getIntent().getStringExtra("topic_description");

        // Устанавливаем данные
        if (currentTopic != null) {
            tvTitle.setText(currentTopic);
            setTopicContent(currentTopic);
        }

        // Обработчики кнопок
        btnMarkCompleted.setOnClickListener(v -> toggleCompletion());
        btnPractice.setOnClickListener(v -> startPractice());
    }

    @SuppressLint("SetTextI18n")
    private void setTopicContent(String topicTitle) {
        switch (topicTitle) {
            case "Назоўнік":
                tvDescription.setText("Назоўнік — частка мовы, якая абазначае прадмет і адказвае на пытанні хто? што?\n\n" +
                        "• Род: мужчынскі, жаночы, ніякі\n" +
                        "• Лік: адзіночны, множны\n" +
                        "• Склон: назоўны, родны, давальны, вінавальны, творны, месны");
                tvExamples.setText("Прыклады:\n" +
                        "• Мужчынскі род: стол, конь, дзень\n" +
                        "• Жаночы род: кніга, песня, ноч\n" +
                        "• Ніякі род: вакно, поле, яблыка\n" +
                        "• Множны лік: сталы, коні, кнігі");
                break;

            case "Дзеяслоў":
                tvDescription.setText("Дзеяслоў — частка мовы, якая абазначае дзеянне або стан прадмета і адказвае на пытанні што рабіць? што зрабіць?\n\n" +
                        "• Час: цяперашні, мінулы, будучы\n" +
                        "• Асоба: 1-я, 2-я, 3-я\n" +
                        "• Лад: сапраўдны, умоўны, загадны");
                tvExamples.setText("Прыклады:\n" +
                        "• Цяперашні час: чытаю, пішаш, рабоча\n" +
                        "• Мінулы час: чытаў, пісала, працавалі\n" +
                        "• Будучы час: буду чытаць, напішу, будуць працаваць\n" +
                        "• Загадны лад: чытай, пішы, працуй");
                break;

            case "Прыметнік":
                tvDescription.setText("Прыметнік — частка мовы, якая абазначае прыкмету прадмета і адказвае на пытанні які? чый? якой? колькі?\n\n" +
                        "• Ступені параўнання: станоўчы, вышэйшы, найвышэйшы\n" +
                        "• Поўныя і кароткія формы\n" +
                        "• Склоненне па родах, ліках, склонах");
                tvExamples.setText("Прыклады:\n" +
                        "• Станоўчы ступень: добры, прыгожы, ціхі\n" +
                        "• Вышэйшы ступень: лепшы, прыгажэйшы, цішэйшы\n" +
                        "• Найвышэйшы ступень: найлепшы, найпрыгажэйшы, найцішэйшы\n" +
                        "• Кароткая форма: рад, рада, рады");
                break;

            case "Лічэбнік":
                tvDescription.setText("Лічэбнік — частка мовы, якая абазначае колькасць або парадак прадметаў пры лічэнні.\n\n" +
                        "• Колькасныя: адзін, два, тры\n" +
                        "• Парадкавыя: першы, другі, трэці\n" +
                        "• Дробныя: палова, траціна\n" +
                        "• Зборныя: двое, трое, чацвёра");
                tvExamples.setText("Прыклады:\n" +
                        "• Колькасныя: пяць кніг, дзесяць хвілін\n" +
                        "• Парадкавыя: першы паверх, другая спроба\n" +
                        "• Дробныя: адна другая, дзве трэціх\n" +
                        "• Зборныя: двое сяброў, трое дзетак");
                break;

            case "Займеннік":
                tvDescription.setText("Займеннік — частка мовы, якая ўжываецца замест назоўніка, прыметніка, лічэбніка.\n\n" +
                        "• Асабовыя: я, ты, ён, яна, яно, мы, вы, яны\n" +
                        "• Прыналежныя: мой, твой, яго, яе, наш, ваш, іх\n" +
                        "• Указальныя: той, гэты, такі\n" +
                        "• Адносныя: хто, што, які, чый");
                tvExamples.setText("Прыклады:\n" +
                        "• Асабовыя: Я чытаю кнігу\n" +
                        "• Прыналежныя: Гэта мой дом\n" +
                        "• Указальныя: Той чалавек прыйшоў\n" +
                        "• Адносныя: Кніга, якую я купіў");
                break;

            case "Прыслоўе":
                tvDescription.setText("Прыслоўе — частка мовы, якая абазначае прыкмету дзеяння, стан або якасць.\n\n" +
                        "• Часу: сёння, учора, заўтра, цяпер\n" +
                        "• Месца: тут, там, усюды, нідзе\n" +
                        "• Спосабу: добра, дрэнна, хутка, павольна\n" +
                        "• Прычыны: таму, затым, з-за");
                tvExamples.setText("Прыклады:\n" +
                        "• Часу: Ён прыйшоў учора\n" +
                        "• Месца: Кніга ляжыць тут\n" +
                        "• Спосабу: Яна спявае прыгожа\n" +
                        "• Прычыны: Ён не прыйшоў з-за хваробы");
                break;

            case "Дзеепрыслоўе":
                tvDescription.setText("Дзеепрыслоўе — несамастойная форма дзеяслова, якая абазначае дадатковае дзеянне.\n\n" +
                        "• Адушаўлёныя і неадушаўлёныя формы\n" +
                        "• Час: незалежны ад асноўнага дзеяслова\n" +
                        "• Спосаб утварэння: ад асновы дзеяслова");
                tvExamples.setText("Прыклады:\n" +
                        "• Чытаючы кнігу, я вучуся\n" +
                        "• Прачытаўшы газету, ён пайшоў\n" +
                        "• Ідучы па вуліцы, сустрэў сябра\n" +
                        "• Напісаўшы ліст, адправіла яго");
                break;

            case "Дзеепрыметнік":
                tvDescription.setText("Дзеепрыметнік — дзеяслоўная форма, якая абазначае прыкмету прадмета па дзеянні.\n\n" +
                        "• Дзеяслоўныя формы: дзеючая і пасіўная\n" +
                        "• Час: цяперашні і мінулы\n" +
                        "• Поўныя і кароткія формы");
                tvExamples.setText("Прыклады:\n" +
                        "• Чытаная кніга (пасіўная)\n" +
                        "• Чытаючы студэнт (дзеючая)\n" +
                        "• Пабудаваны дом (пасіўная)\n" +
                        "• Будучы архітэктар (дзеючая)");
                break;

            case "Складаназалежныя сказ":
                tvDescription.setText("Складаназалежны сказ — сказ, які складаецца з галоўнага і даданага сказаў.\n\n" +
                        "• Сінтаксічны аналіз структуры\n" +
                        "• Віды даданых сказаў\n" +
                        "• Сродкі сувязі паміж часткамі");
                tvExamples.setText("Прыклады:\n" +
                        "• Я ведаю, што ты прыйдзеш\n" +
                        "• Калі будзе сонечна, паедзем\n" +
                        "• Той, хто старанна вучыцца, дасягне\n" +
                        "• Мы пайшлі туды, дзе ціха");
                break;

            case "Безасабовая форма дзеяслова":
                tvDescription.setText("Безасабовая форма — дзеяслоў, які абазначае дзеянне без суб'екта.\n\n" +
                        "• Асабовыя і безасабовыя формы\n" +
                        "• Выражэнне стану прыроды\n" +
                        "• Выражэнне фізічнага стану");
                tvExamples.setText("Прыклады:\n" +
                        "• Святлее. Холадна. Ціха.\n" +
                        "• Мне не спіцца. Яму весела.\n" +
                        "• Трасе. Ліе дажджом.\n" +
                        "• Вячэраецца. Абіраецца.");
                break;

            case "Зваротак":
                tvDescription.setText("Зваротак — слова або спалучэнне слоў, якое называе асобу або прадмет, да якога звяртаюцца.\n\n" +
                        "• Зваротныя канструкцыі\n" +
                        "• Інтанацыя зваротку\n" +
                        "• Выдзяленне коскамі");
                tvExamples.setText("Прыклады:\n" +
                        "• Дружа, дапамажы мне!\n" +
                        "• Вось і ты, мой родны край!\n" +
                        "• Сыну мой, будзь мужным!\n" +
                        "• О, неба, як ты прыгожае!");
                break;

            case "Інфінітыў":
                tvDescription.setText("Інфінітыў — нявызначаная форма дзеяслова.\n\n" +
                        "• Нявызначаная форма дзеяслова\n" +
                        "• Адказвае на пытанні што рабіць? што зрабіць?\n" +
                        "• Спосабы ўтварэння");
                tvExamples.setText("Прыклады:\n" +
                        "• Чытаць кнігі - карысна\n" +
                        "• Я хачу вучыцца\n" +
                        "• Трэба зрабіць работу\n" +
                        "• Ён пачаў спяваць");
                break;

            case "Аднародныя члены сказа":
                tvDescription.setText("Аднародныя члены сказа — члены сказа, якія адказваюць на адно пытанне.\n\n" +
                        "• Сінтаксічныя адносіны\n" +
                        "• Злучальныя злучнікі\n" +
                        "• Інтанацыя пералічэння");
                tvExamples.setText("Прыклады:\n" +
                        "• На стале ляжалі кнігі, сшыткі і ручкі\n" +
                        "• Ён быў разумны, добры і працавіты\n" +
                        "• Дзеці бегалі, скакалі і весяліліся\n" +
                        "• Мы былі ў кіно, тэатры і на выставе");
                break;

            case "Злучнік":
                tvDescription.setText("Злучнік — службовая частка мовы для злучэння членаў сказа і частак складанага сказа.\n\n" +
                        "• Сузінальныя і падпарадкавальныя\n" +
                        "• Простыя і складаныя\n" +
                        "• Адзіночныя і паўторныя");
                tvExamples.setText("Прыклады:\n" +
                        "• Сузінальныя: і, а, але, ці\n" +
                        "• Падпарадкавальныя: што, калі, каб, бо\n" +
                        "• Я і ты пайшлі ў кіно\n" +
                        "• Ён сказаў, што прыйдзе");
                break;

            case "Выклічнік":
                tvDescription.setText("Выклічнік — частка мовы, якая выражае пачуцці, эмоцыі, але не называе іх.\n\n" +
                        "• Эмацыйныя выклічнікі\n" +
                        "• Перайманне гукаў\n" +
                        "• Загадныя выклічнікі");
                tvExamples.setText("Прыклады:\n" +
                        "• Эмацыйныя: Ой!, ах!, ох!\n" +
                        "• Перайманне: брр!, гав!, мяу!\n" +
                        "• Загадныя: ша!, цс!, гэй!\n" +
                        "• Віншавальныя: ура!, брава!");
                break;

            case "Частка":
                tvDescription.setText("Частка — службовая частка мовы для выразання адценняў значэння.\n\n" +
                        "• Службовыя часткі мовы\n" +
                        "• Узмацняльныя часткі\n" +
                        "• Адмоўныя часткі");
                tvExamples.setText("Прыклады:\n" +
                        "• Узмацняльныя: нават, жа, то\n" +
                        "• Адмоўныя: не, ні\n" +
                        "• Аграничальныя: толькі, выключна\n" +
                        "• Пытальныя: няўжо, ці");
                break;

            case "Прыназоўнік":
                tvDescription.setText("Прыназоўнік — службовая частка мовы для выразання адносін паміж прадметамі.\n\n" +
                        "• Прасторавыя і часовыя\n" +
                        "• Простыя і складаныя\n" +
                        "• Склонныя і несклонныя");
                tvExamples.setText("Прыклады:\n" +
                        "• Прасторавыя: у, на, пад, над\n" +
                        "• Часовыя: перад, пасля, падчас\n" +
                        "• Прычынныя: з-за, дзякуючы\n" +
                        "• Мэтавыя: для, з мэтай");
                break;

            case "Заменнік":
                tvDescription.setText("Заменнік — частка мовы для замены іншых частак мовы.\n\n" +
                        "• Асабовыя і прыналежныя\n" +
                        "• Указальныя і азначальныя\n" +
                        "• Адносныя і пытальныя");
                tvExamples.setText("Прыклады:\n" +
                        "• Асабовыя: я, ты, ён\n" +
                        "• Прыналежныя: мой, твой, яго\n" +
                        "• Указальныя: гэты, той\n" +
                        "• Адносныя: хто, што, які");
                break;

            case "Сказ":
                tvDescription.setText("Сказ — сінтаксічная адзінка, якая выражае закончаную думку.\n\n" +
                        "• Просты і складаны сказ\n" +
                        "• Двохасноўны і аднаасноўны\n" +
                        "• Поўны і няпоўны");
                tvExamples.setText("Прыклады:\n" +
                        "• Просты: Сонца свеціць\n" +
                        "• Складаны: Я чытаю, а ты пішаш\n" +
                        "• Злучаны: Сонца свеціць і птушкі спяваюць\n" +
                        "• Бессоюзнасны: Святлее, падае снег");
                break;

            case "Словазлучэнне":
                tvDescription.setText("Словазлучэнне — спалучэнне двух і больш значных слоў.\n\n" +
                        "• Простыя і складаныя\n" +
                        "• Сінтаксічныя адносіны\n" +
                        "• Спосабы сувязі слоў");
                tvExamples.setText("Прыклады:\n" +
                        "• Чытаць кнігу (кіраванне)\n" +
                        "• Вялікі дом (прымяненне)\n" +
                        "• Ісці хутка (прыляганне)\n" +
                        "• Цікавая кніга студэнта");
                break;

            case "Словаўтварэнне":
                tvDescription.setText("Словаўтварэнне — раздзел мовазнаўства пра спосабы ўтварэння новых слоў.\n\n" +
                        "• Марфалагічныя спосабы\n" +
                        "• Сінтаксічныя спосабы\n" +
                        "• Семантычныя спосабы");
                tvExamples.setText("Прыклады:\n" +
                        "• Суфіксальны: школа → школьнік\n" +
                        "• Прэфіксальны: чытаць → прачытаць\n" +
                        "• Складанне: лёд + ход → лёдаход\n" +
                        "• Пераход у іншую частку мовы");
                break;

            case "Лічэбнік (колькасныя і парадкавыя)":
                tvDescription.setText("Лічэбнік — частка мовы для абазначэння колькасці і парадку.\n\n" +
                        "• Колькасныя і парадкавыя\n" +
                        "• Простыя і складаныя\n" +
                        "• Дробныя і зборныя");
                tvExamples.setText("Прыклады:\n" +
                        "• Колькасныя: адзін, два, пяць\n" +
                        "• Парадкавыя: першы, другі, пяты\n" +
                        "• Дробныя: палова, траціна\n" +
                        "• Зборныя: двое, трое, чацвёра");
                break;

            case "Прыметнік (якасныя і адносныя)":
                tvDescription.setText("Прыметнік — частка мовы для абазначэння прыкметы прадмета.\n\n" +
                        "• Якасныя і адносныя\n" +
                        "• Поўныя і кароткія формы\n" +
                        "• Ступені параўнання");
                tvExamples.setText("Прыклады:\n" +
                        "• Якасныя: добры, прыгожы, цёплы\n" +
                        "• Адносныя: драўляны, гарадскі, школьны\n" +
                        "• Прыналежныя: матулін, бацькаў\n" +
                        "• Кароткія формы: рад, павінен");
                break;

            case "Дзеяслоў (спражэнне па асабах)":
                tvDescription.setText("Дзеяслоў — частка мовы для абазначэння дзеяння.\n\n" +
                        "• Спражэнне па асабах\n" +
                        "• Час, лік, род\n" +
                        "• Лад і стан");
                tvExamples.setText("Прыклады:\n" +
                        "• 1-я асоба: я чытаю, мы чытаем\n" +
                        "• 2-я асоба: ты чытаеш, вы чытаеце\n" +
                        "• 3-я асоба: ён чытае, яны чытаюць\n" +
                        "• Множны лік: мы, вы, яны");
                break;
        }
    }

    private void toggleCompletion() {
        if (!isCompleted) {
            markLessonCompleted();
        } else {
            // Если нужно сделать возможность снять отметку
            isCompleted = false;
            btnMarkCompleted.setText("Пазначыць як пройдзеную");
            btnMarkCompleted.setBackgroundColor(Color.parseColor("#2196F3"));
            btnMarkCompleted.setTextColor(Color.WHITE);
            Toast.makeText(this, "Адзнака знятая", Toast.LENGTH_SHORT).show();
        }
    }

    private void markLessonCompleted() {
        // Отмечаем урок как пройденный
        isCompleted = true;
        btnMarkCompleted.setText("✓ Пройдзена");
        btnMarkCompleted.setBackgroundColor(Color.parseColor("#4CAF50"));
        btnMarkCompleted.setTextColor(Color.WHITE);

        // Показываем уведомление
        Toast.makeText(this, "Тэма адзначана як пройдзеная! +10 вопыту", Toast.LENGTH_SHORT).show();

        // ОБНОВЛЯЕМ ЧЕРЕЗ UserDataManager
        UserDataManager userDataManager = new UserDataManager(this);
        userDataManager.addCompletedLesson(); // Это увеличит счетчик

        // Также обновляем статистику для надежности
        SharedPreferences statsPrefs = getSharedPreferences("UserStats", MODE_PRIVATE);
        int currentLessons = statsPrefs.getInt("completed_lessons", 0);
        int currentExp = statsPrefs.getInt("experience", 0);

        SharedPreferences.Editor editor = statsPrefs.edit();
        editor.putInt("completed_lessons", currentLessons + 1);
        editor.putInt("experience", currentExp + 10);
        editor.apply();

        // Обновляем день активности
        updateActiveDay();
        updateStreak();

        // ОБНОВЛЯЕМ ДАННЫЕ В GrammarActivity
        setResult(RESULT_OK); // Это сообщит GrammarActivity об обновлении
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

    private void startPractice() {
        // Открываем практику
        Toast.makeText(this, "Практыкаванне па тэме: " + currentTopic, Toast.LENGTH_SHORT).show();

        // ЗАПУСКАЕМ АКТИВНОСТЬ ПРАКТИКИ
        Intent intent = new Intent(this, GrammarPracticeActivity.class);
        intent.putExtra("topic", currentTopic);
        startActivity(intent);
    }
}