package com.lexisense.backend.dto

data class Meaning(
    val partOfSpeech: String?,
    val definitions: List<Definition> = emptyList(),
)