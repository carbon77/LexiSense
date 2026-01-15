package com.lexisense.backend.dto

data class WordDefinition(
    val word: String?,
    val phonetic: String?,
    val phonetics: List<Phonetic> = emptyList(),
    val meanings: List<Meaning> = emptyList(),
    val license: License?,
    val sourceUrls: List<String> = emptyList(),
)
