package com.project.geo.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient

@Configuration
class OverpassClientConfig {

    @Bean
    fun overpassRestClient(@Value("\${overpass.url}") overpassUrl: String): RestClient {
        return RestClient.builder()
            .baseUrl(overpassUrl)
            .build()
    }
}