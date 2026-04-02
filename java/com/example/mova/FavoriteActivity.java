package com.example.mova;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FavoriteActivity extends AppCompatActivity {
    private RecyclerView recyclerViewFavorites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        recyclerViewFavorites = findViewById(R.id.recyclerViewFavorites);
        loadFavoriteTopics();
    }

    public void loadFavoriteTopics() {
        // Загружаем из БД только избранные темы
        List<GrammarTopic> favorites = new ArrayList<>();

        // Все темы изначально не в избранном

        favorites.add(new GrammarTopic("Назоўнік", "Род, лік, склон назоўнікаў", false));
        favorites.add(new GrammarTopic("Дзеяслоў", "Спражэнне дзеясловаў", false));
        favorites.add(new GrammarTopic("Прыметнік", "Ступені параўнання", false));
        favorites.add(new GrammarTopic("Лічэбнік", "Колькасныя і парадкавыя", false));
        favorites.add(new GrammarTopic("Займеннік", "Асабовыя займеннікі", false));
        favorites.add(new GrammarTopic("Прыслоўе", "Віды прыслоўяў", false));
        favorites.add(new GrammarTopic("Выклічнік", "Ужыванне выклічнікаў", false));
        favorites.add(new GrammarTopic("Злучнік", "Складаныя і простыя злучнікі", false));

        // Синтаксис (6)
        favorites.add(new GrammarTopic("Сказ", "Граматычная аснова сказа", false));
        favorites.add(new GrammarTopic("Члены сказа", "Галоўныя і другасныя члены", false));
        favorites.add(new GrammarTopic("Просты сказ", "Аднаскладовыя і шматскладовыя", false));
        favorites.add(new GrammarTopic("Складаны сказ", "Злучаныя і падпарадкаваныя", false));
        favorites.add(new GrammarTopic("Зварот", "Даданыя і прыслоўевыя звароты", false));
        favorites.add(new GrammarTopic("Інтанацыя", "Тыпы інтанацыйных канструкцый", false));

        // Морфология (5)
        favorites.add(new GrammarTopic("Словаўтварэнне", "Спосабы ўтварэння словаў", false));
        favorites.add(new GrammarTopic("Фанетыка", "Гукавы лад беларускай мовы", false));
        favorites.add(new GrammarTopic("Арфаэпія", "Правілы вымаўлення", false));
        favorites.add(new GrammarTopic("Арфаграфія", "Правілы правапісу", false));
        favorites.add(new GrammarTopic("Пунктуацыя", "Правілы расстаноўкі знакаў", false));

        // Дополнительные темы (5)
        favorites.add(new GrammarTopic("Дыялектызмы", "Рэгіянальныя асаблівасці", false));
        favorites.add(new GrammarTopic("Стылістыка", "Моўныя стылі і рэгістры", false));
        favorites.add(new GrammarTopic("Сінонімы", "Сінанімічныя шэрагі", false));
        favorites.add(new GrammarTopic("Антонімы", "Процілегласці ў мове", false));
        favorites.add(new GrammarTopic("Фразеалогізмы", "Устойлівыя словазлучэнні", false));
        GrammarAdapter adapter = new GrammarAdapter(favorites);
        recyclerViewFavorites.setAdapter(adapter);
    }
}