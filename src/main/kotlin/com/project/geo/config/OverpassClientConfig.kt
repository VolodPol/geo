package com.project.geo.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient

@Configuration
class OverpassClientConfig {

    @Value("\${overpass.url}")
    private lateinit var overpassUrl: String

    @Bean("OverpassClient")
    fun overpassClient(): RestClient {
        return RestClient.builder()
            .baseUrl(overpassUrl)
            .build()
    }
}