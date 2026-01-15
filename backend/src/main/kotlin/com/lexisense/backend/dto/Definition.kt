package com.lexisense.backend.dto

data class Definition(
    val definition: String?,
    val synonyms: List<String> = emptyList(),
    val antonyms: List<String> = emptyList(),
    val example: String?,
)
