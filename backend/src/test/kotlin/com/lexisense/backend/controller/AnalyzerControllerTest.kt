package com.lexisense.backend.controller

import com.lexisense.backend.client.DictionaryClient
import com.lexisense.backend.service.SentenceAnalyzerService
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.ExchangeFunction
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono

class AnalyzerControllerTest {

    @Test
    fun `findDefinition returns analyzed words`() {
        val exchangeFunction = ExchangeFunction { request ->
            val word = request.url().path.removePrefix("/")
            val response = ClientResponse.create(HttpStatus.OK)
                .header("Content-Type", "application/json")
                .body(definitionJson(word))
                .build()
            Mono.just(response)
        }
        val dictionaryClient = DictionaryClient(buildWebClient(exchangeFunction))
        val service = SentenceAnalyzerService(dictionaryClient)
        val controller = AnalyzerController(service)
        val webTestClient = WebTestClient.bindToController(controller).build()

        webTestClient.get()
            .uri("/analyze/sentence?sentence=Hello world")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.hello[0].word").isEqualTo("hello")
            .jsonPath("$.world[0].word").isEqualTo("world")
    }

    private fun buildWebClient(exchangeFunction: ExchangeFunction): WebClient {
        return WebClient.builder()
            .baseUrl("http://localhost")
            .exchangeFunction(exchangeFunction)
            .build()
    }

    private fun definitionJson(word: String): String {
        return """
            [
              {
                \"word\": \"${word.lowercase()}\",
                \"phonetic\": null,
                \"phonetics\": [],
                \"meanings\": [],
                \"license\": null,
                \"sourceUrls\": []
              }
            ]
        """.trimIndent()
    }
}
