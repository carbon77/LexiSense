package com.lexisense.backend.client

import com.lexisense.backend.dto.WordDefinition
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

@Component
class DictionaryClient(
    private val webClient: WebClient,
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val cache = ConcurrentHashMap<String, Mono<List<WordDefinition>>>()

    fun findDefinition(word: String): Mono<List<WordDefinition>> {
        val key = word.lowercase()
        logger.info("Requesting word \"$word\"")

        return cache.computeIfAbsent(key) {
            webClient.get()
                .uri("/$word")
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError) { response ->
                    response.bodyToMono(String::class.java)
                        .defaultIfEmpty("Word \"$word\" not found")
                        .flatMap { Mono.error(Exception(it)) }
                }
                .onStatus(HttpStatusCode::is5xxServerError) { response ->
                    response.bodyToMono(String::class.java)
                        .defaultIfEmpty("Dictionary API error")
                        .flatMap { Mono.error(Exception(it)) }
                }
                .bodyToFlux(WordDefinition::class.java)
                .collectList()
                .doOnError { error ->
                    logger.error("Failed to request word \"$word\"", error)
                    cache.remove(key)
                }
                .filter { it.isNotEmpty() }
                .cache(Duration.ofHours(10))
        }
    }
}