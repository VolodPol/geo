package com.project.geo.service.impl

import com.project.geo.dto.StreetResponse
import com.project.geo.service.OverpassClient
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.body

@Component
class OverpassClientImpl(private val overpassRestClient: RestClient) : OverpassClient {

    override fun findStreet(street: String, coordinates: Array<Double>): StreetResponse {
        return overpassRestClient.post()
            .contentType(MediaType.APPLICATION_JSON)
            .body(STREET_REQUEST_BODY.format(street, *coordinates))
            .retrieve()
            .onStatus(HttpStatusCode::is5xxServerError) { _, response ->
                throw IllegalArgumentException(response.statusCode.toString())
            }.body<StreetResponse>() ?: throw IllegalArgumentException()
    }

    private companion object {
        private val STREET_REQUEST_BODY: String = """
            [out:json];
            (
                way["name"="%s"](%f,%f,%f,%f);
                (._;>;);
            );
            node(w);
            out body; 
            """.trimIndent()
    }
}