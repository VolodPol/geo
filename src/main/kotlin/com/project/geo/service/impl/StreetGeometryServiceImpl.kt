package com.project.geo.service.impl

import com.fasterxml.jackson.databind.json.JsonMapper
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.project.geo.dto.GeometryRequestDto
import com.project.geo.dto.StreetResponse
import com.project.geo.exceptions.IncorrectRequestException
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
        val southWest = request.southWestCoordinate
        val northEast = request.northEastCoordinate

        val body = client.post()
            .contentType(MediaType.APPLICATION_JSON)
            .body(requestBody.format(request.address, southWest.latitude, southWest.longitude, northEast.latitude, northEast.longitude))
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError) { _, response ->
                throw IncorrectRequestException(response.statusCode.toString())
            }
            .body<String>() ?: ""

        return convertToGeoJson(deserialize(body))
    }

    private fun deserialize(response: String): StreetResponse {
        return JsonMapper()
            .readerFor(StreetResponse::class.java)
            .readValue(response)
    }

    private fun convertToGeoJson(response: StreetResponse): String {
        val points = response.elements
            .map { Point.fromLngLat(it.longitude, it.latitude) }
            .toMutableList()

        return LineString.fromLngLats(points).toJson()
    }
}