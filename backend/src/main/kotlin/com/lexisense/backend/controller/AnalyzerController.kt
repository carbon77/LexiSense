package com.lexisense.backend.controller

import com.lexisense.backend.dto.WordDefinition
import com.lexisense.backend.service.SentenceAnalyzerService
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/analyze")
@CrossOrigin("*")
class AnalyzerController(
    private val sentenceAnalyzer: SentenceAnalyzerService,
) {

    @GetMapping("sentence")
    fun findDefinition(@RequestParam(required = true) sentence: String): Mono<Map<String, List<WordDefinition>>> {
        return sentenceAnalyzer.analyzeSentence(sentence)
    }
}