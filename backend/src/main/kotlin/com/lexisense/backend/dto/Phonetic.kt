package com.lexisense.backend.dto

data class Phonetic(
    val text: String?,
    val audio: String?,
    val sourceUrl: String?,
    val license: License?,
)

