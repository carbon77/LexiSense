package com.lexisense.backend.service

import com.lexisense.backend.client.DictionaryClient
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.ExchangeFunction
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

class SentenceAnalyzerServiceTest {

    @Test
    fun `analyzeSentence returns definitions and empty lists for missing words`() {
        val exchangeFunction = ExchangeFunction { request ->
            val word = request.url().path.removePrefix("/")
            val response = if (word == "missing") {
                ClientResponse.create(HttpStatus.NOT_FOUND)
                    .header("Content-Type", "text/plain")
                    .body("Word \"$word\" not found")
                    .build()
            } else {
                ClientResponse.create(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body(definitionJson(word))
                    .build()
            }
            Mono.just(response)
        }
        val dictionaryClient = DictionaryClient(buildWebClient(exchangeFunction))
        val service = SentenceAnalyzerService(dictionaryClient)

        StepVerifier.create(service.analyzeSentence("Hello, world! Missing?"))
            .assertNext { result ->
                assertEquals(3, result.size)
                assertTrue(result.containsKey("hello"))
                assertTrue(result.containsKey("world"))
                assertTrue(result.containsKey("missing"))
                assertEquals("hello", result.getValue("hello").first().word)
                assertEquals("world", result.getValue("world").first().word)
                assertTrue(result.getValue("missing").isEmpty())
            }
            .verifyComplete()
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
