package com.lexisense.backend.client

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.ExchangeFunction
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.util.concurrent.atomic.AtomicInteger

class DictionaryClientTest {

    @Test
    fun `findDefinition caches successful responses`() {
        val callCount = AtomicInteger(0)
        val exchangeFunction = ExchangeFunction { request ->
            callCount.incrementAndGet()
            val word = request.url().path.removePrefix("/")
            val response = ClientResponse.create(HttpStatus.OK)
                .header("Content-Type", "application/json")
                .body(definitionJson(word))
                .build()
            Mono.just(response)
        }
        val client = DictionaryClient(buildWebClient(exchangeFunction))

        StepVerifier.create(client.findDefinition("Test"))
            .assertNext { definitions ->
                assertEquals(1, definitions.size)
                assertEquals("test", definitions.first().word)
            }
            .verifyComplete()

        StepVerifier.create(client.findDefinition("test"))
            .assertNext { definitions ->
                assertEquals(1, definitions.size)
                assertEquals("test", definitions.first().word)
            }
            .verifyComplete()

        assertEquals(1, callCount.get())
    }

    @Test
    fun `findDefinition removes cache entry on error`() {
        val callCount = AtomicInteger(0)
        val exchangeFunction = ExchangeFunction { request ->
            val word = request.url().path.removePrefix("/")
            val response = if (callCount.getAndIncrement() == 0) {
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
        val client = DictionaryClient(buildWebClient(exchangeFunction))

        StepVerifier.create(client.findDefinition("Missing"))
            .expectErrorMessage("Word \"Missing\" not found")
            .verify()

        StepVerifier.create(client.findDefinition("missing"))
            .assertNext { definitions ->
                assertEquals("missing", definitions.first().word)
            }
            .verifyComplete()

        assertEquals(2, callCount.get())
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
