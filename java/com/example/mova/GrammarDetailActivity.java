package com.example.mova;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class GrammarDetailActivity extends AppCompatActivity {

    private TextView tvGrammarTitle, tvGrammarRule, tvDifficulty, tvExamples, tvExceptions;
    private Button btnPractice, btnClose;
    private ImageButton btnBack;
    private UserDataManager userDataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grammar_detail);

        // Инициализация UserDataManager
        userDataManager = new UserDataManager(this);

        initializeViews();
        setupClickListeners();
        loadGrammarData();
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        tvGrammarTitle = findViewById(R.id.tvGrammarTitle);
        tvGrammarRule = findViewById(R.id.tvGrammarRule);
        tvDifficulty = findViewById(R.id.tvDifficulty);
        tvExamples = findViewById(R.id.tvExamples);
        tvExceptions = findViewById(R.id.tvExceptions);
        btnPractice = findViewById(R.id.btnPractice);
        btnClose = findViewById(R.id.btnClose);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnPractice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPracticeActivity();
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void loadGrammarData() {
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String rule = intent.getStringExtra("rule");
        String difficulty = intent.getStringExtra("difficulty");

        if (title != null) {
            tvGrammarTitle.setText(title);
            setExamplesAndExceptions(title);
        }

        if (rule != null) {
            tvGrammarRule.setText(rule);
        }

        if (difficulty != null) {
            tvDifficulty.setText(difficulty);
            setDifficultyStyle(difficulty);
        } else {
            tvDifficulty.setText("⚡ Пачатковы");
            setDifficultyStyle("Пачатковы");
        }
    }

    private void setDifficultyStyle(String difficulty) {
        int backgroundResId;
        if (difficulty.contains("Пачатковы")) {
            backgroundResId = R.drawable.bg_difficulty_beginner;
        } else if (difficulty.contains("Сярэдні")) {
            backgroundResId = R.drawable.bg_difficulty_intermediate;
        } else if (difficulty.contains("Прасунуты")) {
            backgroundResId = R.drawable.bg_difficulty_advanced;
        } else {
            backgroundResId = R.drawable.bg_difficulty_beginner;
        }
        tvDifficulty.setBackgroundResource(backgroundResId);
    }

    private void setExamplesAndExceptions(String topic) {
        switch (topic) {
            case "Назоўнік":
                tvExamples.setText("• Дом, кніга, студэнт\n• Вялікі дом, цікавая кніга\n• Новы студэнт, школа\n• Я чытаю кнігу, ён будуе дом");
                tvExceptions.setText("• Назоўнікі ніякага роду: акно, поле, дзяўчынка\n• Нескланяльныя назоўнікі: кіно, таксі, кафе\n• Выключэнні ў множным ліку: вочы - вокны");
                break;

            case "Дзеяслоў":
                tvExamples.setText("• Я чытаю, ты пішаш, ён робіць\n• Мы вучымся, вы працуеце, яны гуляюць\n• Заўтра я буду чытаць\n• Учора я прачытаў кнігу");
                tvExceptions.setText("• Неправільныя дзеясловы: быць, есці, ісці\n• Зваротныя дзеясловы: вучыцца, мыцца, апранацца\n• Выключэнні ў спражэнні: хацець - хочу, хочаш");
                break;

            case "Прыметнік":
                tvExamples.setText("• Вялікі дом, цікавая кніга\n• Новы студэнт, старая школа\n• Больш цікавы, самы цікавы\n• Вельмі прыгожы, крыху складаны");
                tvExceptions.setText("• Прыметнікі, якія не маюць ступеняў: жывы, мёртвы\n• Выключэнні ў ступенях параўнання: добры - лепшы\n• Кароткія формы: рады, готов, павінен");
                break;

            case "Лічэбнік":
                tvExamples.setText("• Адзін, два, тры\n• Першы, другі, трэці\n• Дзесяць хлопчыкаў\n• Пятая кніга, палова яблыка");
                tvExceptions.setText("• Складаныя лічэбнікі: адзінаццаць, дванаццаць\n• Дробныя лічэбнікі: палова, трэць, чвэрць\n• Зборныя лічэбнікі: двое, трое, чацвёра");
                break;

            case "Займеннік":
                tvExamples.setText("• Я, ты, ён, яна\n• Мой, твой, наш, іх\n• Гэты, той, такі\n• Хто, што, які, чый");
                tvExceptions.setText("• Зваротны займеннік: сябе\n• Азначальныя займеннікі: сам, самы, увесь\n• Неазначальныя: нехта, нешта, некаторы");
                break;

            case "Прыслоўе":
                tvExamples.setText("• Ён чытае хутка\n• Яна спявае прыгожа\n• Мы прыйшлі рана\n• Яны размаўляюць гучна");
                tvExceptions.setText("• Прыслоўі, якія не маюць ступеняў: учора, сёння, заўтра\n• Выключэнні ў ступенях: доўга - даўжэй\n• Прыслоўі меры і ступені: вельмі, крыху, зусім");
                break;

            case "Дзеепрыслоўе":
                tvExamples.setText("• Чытаючы кнігу, я вучуся\n• Прачытаўшы газету, ён пайшоў на працу\n• Ідучы па вуліцы, я сустрэў сябра\n• Напісаўшы ліст, яна адправіла яго");
                tvExceptions.setText("• Дзеепрыслоўі ад дзеясловаў на -чы: могчы - могучы\n• Дзеепрыслоўі ад дзеясловаў з асновай на зычны: нясці - нясучы\n• Выключэнні ў ўтварэнні: быць - будучы");
                break;

            case "Дзеепрыметнік":
                tvExamples.setText("• Кніга, чытаная студэнтам\n• Дом, пабудаваны рабочымі\n• Рашэнне, прынятае кіраўніцтвам\n• Задача, вырашаная вучнем");
                tvExceptions.setText("• Дзеепрыметнікі ад дзеясловаў з -ся: вучыцца - які вучыцца\n• Дзеепрыметнікі з суфіксам -уч-/-юч-: лятаць - лятучы\n• Выключэнні: ісці - які ідзе");
                break;

            case "Складаназалежныя сказ":
                tvExamples.setText("• Я ведаю, што ты прыйдзеш\n• Калі будзе сонечна, мы паедзем за горад\n• Той, хто старанна вучыцца, дасягне поспеху\n• Мы пайшлі туды, дзе ціха і спакойна");
                tvExceptions.setText("• Сказы з некалькімі даданымі\n• Сказы з аднароднымі даданымі\n• Сказы з паслядоўным падпарадкаваннем");
                break;

            case "Безасабовая форма дзеяслова":
                tvExamples.setText("• Святлее. Холадна. Ціха.\n• Мне не спіцца. Яму весела.\n• Трасе. Ліе дажджом.\n• Вячэраецца. Абіраецца.");
                tvExceptions.setText("• Безасабовыя дзеясловы з постфіксам -ся\n• Безасабовыя дзеясловы з часткай -но, -то\n• Выключэнні: хочацца, трэба");
                break;

            case "Зваротак":
                tvExamples.setText("• Дружа, дапамажы мне!\n• Вось і ты, мой родны край!\n• Сыну мой, будзь мужным!\n• О, неба, як ты прыгожае!");
                tvExceptions.setText("• Звароткі з прыназоўнікамі: о, дружа!\n• Разавыя звароткі: ты, сонца яснае!\n• Выключэнні з клічнікам: ой!");
                break;

            case "Інфінітыў":
                tvExamples.setText("• Чытаць кнігі - карысна\n• Я хачу вучыцца\n• Трэба зрабіць работу\n• Ён пачаў спяваць");
                tvExceptions.setText("• Інфінітывы на -чы: магчы, пякчы\n• Інфінітывы з -ці: нясці, весці\n• Выключэнні: быць, есці");
                break;

            case "Аднародныя члены сказа":
                tvExamples.setText("• На стале ляжалі кнігі, сшыткі і ручкі\n• Ён быў разумны, добры і працавіты\n• Дзеці бегалі, скакалі і весяліліся\n• Мы былі ў кіно, тэатры і на выставе");
                tvExceptions.setText("• Аднародныя вызначэнні з рознымі адценнямі\n• Аднародныя дапаўненні з рознымі прыназоўнікамі\n• Выключэнні пры абагульняльных словах");
                break;

            case "Злучнік":
                tvExamples.setText("• Я і ты\n• Кніга або часопіс\n• Ён прыйдзе, калі будзе час\n• Яна спявае, а ён танцуе");
                tvExceptions.setText("• Складаныя злучнікі: таму што, так як\n• Супраціўныя злучнікі: а, але\n• Прычынныя злучнікі: бо, таму што");
                break;

            case "Выклічнік":
                tvExamples.setText("• Ой!, ах!, ох!\n• Брава!, ура!\n• Цс!, ша!\n• О!, эх!, вау!");
                tvExceptions.setText("• Выклічнікі, якія выражаюць боль: ай!, ой!\n• Выклічнікі захаплення: ваў!, о!\n• Звукапераймальныя: брр!, гав!, мяу!");
                break;

            case "Частка":
                tvExamples.setText("• Толькі ты\n• Нават ён\n• Ніхто не прыйшоў\n• Хай будзе свет!");
                tvExceptions.setText("• Адмоўныя часткі: не, ні\n• Узмацняльныя часткі: нават, жа\n• Формаўтваральныя часткі: бы, хай");
                break;

            case "Прыназоўнік":
                tvExamples.setText("• У доме, на стале\n• З сябрам, да школы\n• Праз раку, пад зямлёй\n• Перад домам, за дрэвам");
                tvExceptions.setText("• Складаныя прыназоўнікі: з-за, з-пад\n• Выключэнні ва ўжыванні: па дарозе, па вуліцы\n• Прыназоўнікі з рознымі склонамі");
                break;

            case "Заменнік":
                tvExamples.setText("• Я, ты, ён, яна\n• Мой, твой, наш, іх\n• Гэты, той, такі\n• Хто, што, які, чый");
                tvExceptions.setText("• Зваротны займеннік: сябе\n• Азначальныя займеннікі: сам, самы, увесь\n• Неазначальныя: нехта, нешта, некаторы");
                break;

            case "Сказ":
                tvExamples.setText("• Сонца свеціць.\n• Дзеці гуляюць у двары.\n• Калі прыйдзе вясна, зацвітуць дрэвы.\n• Я ведаю, што ты правы.");
                tvExceptions.setText("• Няпоўныя сказы\n• Злучнікавыя і бессоюзнае злучэнні\n• Сказы з рознымі відамі сувязі");
                break;

            case "Словазлучэнне":
                tvExamples.setText("• Чытаць кнігу\n• Вялікі дом\n• Ісці хутка\n• Цікавая кніга студэнта");
                tvExceptions.setText("• Складаныя словазлучэнні\n• Словазлучэнні з некалькімі залежнымі\n• Выключэнні ў кіраванні");
                break;

            case "Словаўтварэнне":
                tvExamples.setText("• Чытаць → чытальнік\n• Школа → школьнік\n• Белы → белізна\n• Вялікі → павялічыць");
                tvExceptions.setText("• Словы з невытворнай асновай\n• Словы з запазычанымі марфемамі\n• Выключэнні ў чаргаванні гукаў");
                break;

            // ДОБАВЛЯЮ НЕДОСТАЮЩИЕ 3 ТЕМЫ:
            case "Лічэбнік (колькасныя і парадкавыя)":
                tvExamples.setText("• Адзін, два, тры, чатыры\n• Першы, другі, трэці, чацвёрты\n• Дзесяць кніг, пяць студэнтаў\n• Дзясятая кніга, пяты студэнт");
                tvExceptions.setText("• Складаныя колькасныя: дваццаць адзін\n• Складаныя парадкавыя: дваццаць першы\n• Выключэнні: адзін - першы, два - другі");
                break;

            case "Прыметнік (якасныя і адносныя)":
                tvExamples.setText("• Вялікі, малы, прыгожы (якасныя)\n• Драўляны, шкляны, гарадскі (адносныя)\n• Добры, разумны, смелы (якасныя)\n• Вясковы, школьны, летні (адносныя)");
                tvExceptions.setText("• Якасныя маюць ступені параўнання\n• Адносныя не маюць ступеняў параўнання\n• Выключэнні: прыметнікі, якія могуць быць абодвух тыпаў");
                break;

            case "Дзеяслоў (спражэнне па асабах)":
                tvExamples.setText("• Я чытаю, ты чытаеш, ён чытае\n• Я пішу, ты пішаш, ён піша\n• Я раблю, ты робіш, ён робіць\n• Я вучу, ты вучыш, ён вучыць");
                tvExceptions.setText("• Дзеясловы з няправільным спражэннем\n• Дзеясловы з чаргаваннем у аснове\n• Выключэнні: быць, есці, ісці");
                break;
        }
    }

    private void startPracticeActivity() {
        String topic = tvGrammarTitle.getText().toString();
        String difficulty = tvDifficulty.getText().toString();

        // ДОБАВИТЬ: Засчитываем активность за переход к практике
        userDataManager.addPracticeCompleted();

        Intent intent = new Intent(this, GrammarPracticeActivity.class);
        intent.putExtra("topic", topic);
        intent.putExtra("difficulty", difficulty);
        startActivity(intent);
    }

    @SuppressLint("GestureBackNavigation")
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}