package com.lexisense.backend.service

import com.lexisense.backend.client.DictionaryClient
import com.lexisense.backend.dto.WordDefinition
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class SentenceAnalyzerService(
    private val dictionaryClient: DictionaryClient,
) {

    fun analyzeSentence(sentence: String): Mono<Map<String, List<WordDefinition>>> {
        return Flux.fromIterable(extractWords(sentence))
            .flatMap { word ->
                dictionaryClient.findDefinition(word)
                    .map { definitions -> word to definitions }
                    .onErrorResume { error -> Mono.just(word to emptyList()) }
            }
            .collectMap(
                { it.first },
                { it.second }
            )
    }

    private fun extractWords(sentence: String): List<String> {
        return sentence
            .lowercase()
            .replace(Regex("[^a-zA-Z0-9\\s]"), "")
            .split("\\s+".toRegex())
            .filter { it.isNotEmpty() }
    }
}