package com.project.geo.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient

@Configuration
class OverpassClientConfig {

    @Bean("OverpassClient")
    fun overpassClient(): RestClient {
        return RestClient.builder()
            .baseUrl("https://overpass-api.de/api/interpreter")
            .build()
    }

}