package com.lexisense.backend.service

import com.lexisense.backend.client.DictionaryClient
import com.lexisense.backend.dto.WordDefinition
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

class SentenceAnalyzerServiceTest {

    @Test
    fun `analyzeSentence returns definitions and empty lists for missing words`() {
        val dictionaryClient = mockk<DictionaryClient>()
        every { dictionaryClient.findDefinition("hello") } returns Mono.just(listOf(definition("hello")))
        every { dictionaryClient.findDefinition("world") } returns Mono.just(listOf(definition("world")))
        every { dictionaryClient.findDefinition("missing") } returns Mono.error(RuntimeException("not found"))

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

    private fun definition(word: String): WordDefinition {
        return WordDefinition(
            word = word,
            phonetic = null,
            phonetics = emptyList(),
            meanings = emptyList(),
            license = null,
            sourceUrls = emptyList()
        )
    }
}
