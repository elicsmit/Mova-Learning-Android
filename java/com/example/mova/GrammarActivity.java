package com.example.mova;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mova.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class GrammarActivity extends AppCompatActivity {

    private RecyclerView recyclerViewGrammar;
    private GrammarAdapter adapter;
    private List<GrammarTopic> grammarTopics;
    private List<GrammarTopic> originalGrammarTopics; // Для поиска
    private UserDataManager userDataManager;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grammar);

        // Инициализация UserDataManager
        userDataManager = new UserDataManager(this);

        Log.d("DEBUG", "=== GrammarActivity Started ===");

        ImageButton btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        } else {
            Log.e("DEBUG", "❌ Кнопка назад не найдена!");
        }

        // Инициализация поиска
        setupSearchView();

        // Находим счетчики по ПРАВИЛЬНЫМ ID
        updateCounters();

        initializeViews();
        setupRecyclerView();
    }

    private void setupSearchView() {
        searchView = findViewById(R.id.searchView);

        if (searchView != null) {
            // Настраиваем SearchView
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    // При нажатии Enter/Поиск
                    filterTopics(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    // При изменении текста (реальный поиск)
                    filterTopics(newText);
                    return true;
                }
            });

            // Очистка поиска при нажатии на X
            searchView.setOnCloseListener(() -> {
                showAllTopics();
                return false;
            });

            Log.d("DEBUG", "SearchView инициализирован");
        } else {
            Log.e("DEBUG", "❌ SearchView не найден!");
        }
    }

    private void filterTopics(String query) {
        if (query == null || query.trim().isEmpty()) {
            // Если поисковый запрос пустой, показываем все темы
            showAllTopics();
            return;
        }

        String searchQuery = query.toLowerCase().trim();
        List<GrammarTopic> filteredList = new ArrayList<>();

        for (GrammarTopic topic : originalGrammarTopics) {
            // Ищем в названии и описании
            if (topic.getTitle().toLowerCase().contains(searchQuery) ||
                    topic.getDescription().toLowerCase().contains(searchQuery)) {
                filteredList.add(topic);
            }
        }

        // Обновляем адаптер
        adapter.updateTopics(filteredList);

        Log.d("DEBUG", "Найдено тем: " + filteredList.size() + " по запросу: " + query);

        // Показываем сообщение если ничего не найдено
        if (filteredList.isEmpty()) {
            Toast.makeText(this, "Тэмы не знойдзены: " + query, Toast.LENGTH_SHORT).show();
        }
    }

    private void showAllTopics() {
        adapter.updateTopics(originalGrammarTopics);
        Log.d("DEBUG", "Показаны все темы");
    }

    private void updateCounters() {
        // Находим счетчики
        TextView tvTotalTopics = findViewById(R.id.tvTotalTopics);
        TextView tvCompletedTopics = findViewById(R.id.tvCompletedTopics);

        if (tvTotalTopics != null && tvCompletedTopics != null) {
            int totalTopics = 24;

            // ИСПРАВЛЕННАЯ СТРОКА - получаем через getUserStats()
            int completedTopics = userDataManager.getUserStats().getCompletedLessons();

            tvTotalTopics.setText(String.valueOf(totalTopics));
            tvCompletedTopics.setText(String.valueOf(completedTopics));

            Log.d("DEBUG", "Счетчики обновлены: " + completedTopics + "/" + totalTopics);
        } else {
            Log.e("DEBUG", "❌ Счетчики не найдены!");
        }
    }

    private void initializeViews() {
        recyclerViewGrammar = findViewById(R.id.recyclerViewGrammar);

        Log.d("DEBUG", "RecyclerView: " + (recyclerViewGrammar != null));

        if (recyclerViewGrammar == null) {
            Log.e("DEBUG", "❌ RecyclerView не найден! Проверьте ID в layout");
            Toast.makeText(this, "Ошибка загрузки списка", Toast.LENGTH_LONG).show();
            return;
        }
    }

    private void setupRecyclerView() {
        Log.d("DEBUG", "Настройка RecyclerView...");

        // 1. Загружаем данные
        loadGrammarData();
        Log.d("DEBUG", "Загружено тем: " + grammarTopics.size());

        // 2. Проверяем данные
        for (int i = 0; i < grammarTopics.size(); i++) {
            Log.d("DEBUG", "Тема " + i + ": " + grammarTopics.get(i).getTitle());
        }

        // 3. Настраиваем LayoutManager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewGrammar.setLayoutManager(layoutManager);
        Log.d("DEBUG", "LayoutManager установлен");

        // 4. Создаем и устанавливаем адаптер
        adapter = new GrammarAdapter(grammarTopics);
        recyclerViewGrammar.setAdapter(adapter);
        Log.d("DEBUG", "Адаптер установлен");

        // 5. Проверяем отображение
        if (adapter.getItemCount() > 0) {
            Log.d("DEBUG", "✅ Адаптер содержит " + adapter.getItemCount() + " элементов");
        } else {
            Log.e("DEBUG", "❌ Адаптер пустой!");
        }

        // 6. Обработчик кликов
        adapter.setOnItemClickListener(position -> {
            // Используем текущий список (может быть отфильтрованным)
            GrammarTopic topic = adapter.getTopicAtPosition(position);
            Log.d("DEBUG", "Клик по теме: " + topic.getTitle());
            openTopicDetail(topic);
        });
    }

    public void loadGrammarData() {
        grammarTopics = new ArrayList<>();
        originalGrammarTopics = new ArrayList<>();

        // Добавляем ВСЕ 24 темы
        originalGrammarTopics.add(new GrammarTopic("Назоўнік", "Род, лік, склон назоўнікаў", false));
        originalGrammarTopics.add(new GrammarTopic("Дзеяслоў", "Спражэнне дзеясловаў", false));
        originalGrammarTopics.add(new GrammarTopic("Прыметнік", "Ступені параўнання", false));
        originalGrammarTopics.add(new GrammarTopic("Лічэбнік", "Колькасныя і парадкавыя", false));
        originalGrammarTopics.add(new GrammarTopic("Займеннік", "Асабовыя займеннікі", false));
        originalGrammarTopics.add(new GrammarTopic("Прыслоўе", "Віды прыслоўяў", false));
        originalGrammarTopics.add(new GrammarTopic("Дзеепрыслоўе", "Адушаўлёныя і неадушаўлёныя", false));
        originalGrammarTopics.add(new GrammarTopic("Дзеепрыметнік", "Дзеяслоўныя формы", false));
        originalGrammarTopics.add(new GrammarTopic("Складаназалежныя сказ", "Сінтаксічны аналіз", false));
        originalGrammarTopics.add(new GrammarTopic("Безасабовая форма дзеяслова", "Асабовыя і безасабовыя", false));
        originalGrammarTopics.add(new GrammarTopic("Зваротак", "Зваротныя канструкцыі", false));
        originalGrammarTopics.add(new GrammarTopic("Інфінітыў", "Нявызначаная форма", false));
        originalGrammarTopics.add(new GrammarTopic("Аднародныя члены сказа", "Сінтаксічныя адносіны", false));
        originalGrammarTopics.add(new GrammarTopic("Злучнік", "Сузінальныя і падпарадкавальныя", false));
        originalGrammarTopics.add(new GrammarTopic("Выклічнік", "Эмацыйныя выклічнікі", false));
        originalGrammarTopics.add(new GrammarTopic("Частка", "Службовыя часткі мовы", false));
        originalGrammarTopics.add(new GrammarTopic("Прыназоўнік", "Прасторавыя і часовыя", false));
        originalGrammarTopics.add(new GrammarTopic("Лічэбнік", "Колькасныя і парадкавыя", false));
        originalGrammarTopics.add(new GrammarTopic("Заменнік", "Асабовыя і прыналежныя", false));
        originalGrammarTopics.add(new GrammarTopic("Прыметнік", "Якасныя і адносныя", false));
        originalGrammarTopics.add(new GrammarTopic("Дзеяслоў", "Спражэнне па асабах", false));
        originalGrammarTopics.add(new GrammarTopic("Назоўнік", "Склон і адміністрацыя", false));
        originalGrammarTopics.add(new GrammarTopic("Прыслоўе", "Акалічнасці", false));
        originalGrammarTopics.add(new GrammarTopic("Сказ", "Просты і складаны сказ", false));

        // Копируем в основной список
        grammarTopics.addAll(originalGrammarTopics);
    }

    private void openTopicDetail(GrammarTopic topic) {
        // Засчитываем активность за просмотр темы
        userDataManager.addPracticeCompleted();

        Intent intent = new Intent(this, TopicDetailActivity.class);
        intent.putExtra("topic_title", topic.getTitle());
        intent.putExtra("topic_description", topic.getDescription());

        // ЗАПУСКАЕМ С ОЖИДАНИЕМ РЕЗУЛЬТАТА
        startActivityForResult(intent, 1);
    }

    // Вспомогательный метод для определения сложности темы
    private String getDifficultyForTopic(String topic) {
        // Логика определения сложности (можно настроить по вашему усмотрению)
        if (topic.equals("Назоўнік") || topic.equals("Дзеяслоў") || topic.equals("Прыметнік")) {
            return "⚡ Пачатковы";
        } else if (topic.equals("Лічэбнік") || topic.equals("Займеннік") || topic.equals("Прыслоўе")) {
            return "⚡ Сярэдні";
        } else {
            return "⚡ Прасунуты";
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // ОБНОВЛЯЕМ СЧЕТЧИКИ СРАЗУ ПОСЛЕ ВОЗВРАЩЕНИЯ
            updateCounters();
            Log.d("DEBUG", "Счетчики обновлены после возвращения из темы");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Обновляем счетчики при возвращении на экран
        updateCounters();
    }
}