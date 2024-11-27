package com.project.geo.service.impl

import com.project.geo.service.OverpassClient
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class OverpassClientImpl(private val restClient: RestClient) : OverpassClient {

    override fun sendRequest(request: String): RestClient.ResponseSpec {
        return restClient.post()
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError) { _, response ->
                throw IllegalArgumentException(response.statusCode.toString())
            }
    }
}