package com.example.mova.data.models

data class Article(
    val id: Int,
    val title: String,
    val content: String,
    val category: String,
    val difficulty: String,
    val imageUrl: String? = null
)

data class Word(
    val id: Int,
    val belarusian: String,
    val russian: String,
    val english: String,
    val transcription: String,
    val category: String,
    val examples: List<String> = emptyList()
)

data class GrammarRule(
    val id: Int,
    val title: String,
    val description: String,
    val examples: List<String>,
    val category: String
)