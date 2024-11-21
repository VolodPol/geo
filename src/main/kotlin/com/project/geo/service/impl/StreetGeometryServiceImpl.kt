package com.project.geo.service.impl

import com.fasterxml.jackson.databind.json.JsonMapper
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.project.geo.dto.GeometryRequestDto
import com.project.geo.dto.StreetResponse
import com.project.geo.service.StreetGeometryService
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import org.springframework.web.client.body

@Service
class StreetGeometryServiceImpl(
    @Qualifier("OverpassClient") private val client: RestClient
) : StreetGeometryService {

    private val requestBody: String = """
        [out:json];
        (
            way["name"="%s"](%f,%f,%f,%f);
            (._;>;);
        );
        node(w);
        out body; 
        """.trimIndent()

    override fun extractStreet(request: GeometryRequestDto): String {
        val street = request.address
        val southWest = request.southWestCoordinate
        val northEast = request.northEastCoordinate

        val body = client.post()
            .contentType(MediaType.APPLICATION_JSON)
            .body(requestBody.format(street, southWest.latitude, southWest.longitude, northEast.latitude, northEast.longitude))
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError) { _, response ->
                throw RuntimeException(response.statusCode.toString())
            }
            .body<String>() ?: ""

        val response: StreetResponse = JsonMapper()
            .readerFor(StreetResponse::class.java)
            .readValue(body)

        return convertToGeoJson(response)
    }

    private fun convertToGeoJson(response: StreetResponse): String {
        val points = response.elements.asSequence()
            .map { Point.fromLngLat(it.longitude, it.latitude) }
            .toMutableList()

        return LineString.fromLngLats(points).toJson()
    }
}