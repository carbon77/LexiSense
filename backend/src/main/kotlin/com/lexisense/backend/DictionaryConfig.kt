package com.lexisense.backend

import io.netty.channel.ChannelOption
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import java.time.Duration
import java.util.concurrent.TimeUnit

@Configuration
class DictionaryConfig(
    @Value("\${app.dictionary.url}")
    private val dictionaryUrl: String,
    @Value("\${app.dictionary.timeout}")
    private val timeout: Int,
) {
    @Bean
    fun webClient(): WebClient {
        val httpClient = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeout)
            .responseTimeout(Duration.ofSeconds(timeout.toLong()))
            .doOnConnected { conn ->
                conn.addHandlerLast(ReadTimeoutHandler(timeout.toLong(), TimeUnit.MILLISECONDS))
                conn.addHandlerLast(WriteTimeoutHandler(timeout.toLong(), TimeUnit.MILLISECONDS))
            }
        return WebClient.builder()
            .baseUrl(dictionaryUrl)
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .build()
    }
}