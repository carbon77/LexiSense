package com.lexisense.backend

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class DictionaryConfig(
    @Value("\${app.dictionary.url}")
    private val dictionaryUrl: String,
) {

    @Bean
    fun webClient() = WebClient.builder()
        .baseUrl(dictionaryUrl)
        .build()
}