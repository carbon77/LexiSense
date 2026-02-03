package com.lexisense.backend.client

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.web.reactive.function.client.WebClient
import reactor.test.StepVerifier

class DictionaryClientTest {

    @Test
    fun `findDefinition caches successful responses`() {
        val server = MockWebServer()
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody(definitionJson("test"))
        )
        server.start()

        try {
            val client = DictionaryClient(buildWebClient(server))

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

            assertEquals(1, server.requestCount)
        } finally {
            server.shutdown()
        }
    }

    @Test
    fun `findDefinition removes cache entry on error`() {
        val server = MockWebServer()
        server.enqueue(
            MockResponse()
                .setResponseCode(404)
                .addHeader("Content-Type", "text/plain")
                .setBody("Word \"missing\" not found")
        )
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody(definitionJson("missing"))
        )
        server.start()

        try {
            val client = DictionaryClient(buildWebClient(server))

            StepVerifier.create(client.findDefinition("Missing"))
                .expectErrorMessage("Word \"missing\" not found")
                .verify()

            StepVerifier.create(client.findDefinition("missing"))
                .assertNext { definitions ->
                    assertEquals("missing", definitions.first().word)
                }
                .verifyComplete()

            assertEquals(2, server.requestCount)
        } finally {
            server.shutdown()
        }
    }

    private fun buildWebClient(server: MockWebServer): WebClient {
        return WebClient.builder()
            .baseUrl(server.url("/").toString().removeSuffix("/"))
            .build()
    }

    private fun definitionJson(word: String): String {
        return """
            [
              {
                "word": "${word.lowercase()}",
                "phonetic": null,
                "phonetics": [],
                "meanings": [],
                "license": null,
                "sourceUrls": []
              }
            ]
        """.trimIndent()
    }
}
