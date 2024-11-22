package com.project.geo.service.impl

import com.fasterxml.jackson.databind.json.JsonMapper
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.project.geo.dto.GeometryRequestDto
import com.project.geo.dto.StreetResponse
import com.project.geo.exceptions.IncorrectRequestException
import com.project.geo.service.StreetGeometryService
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import org.springframework.web.client.body

@Service
class StreetGeometryServiceImpl(
    private val overpassClient: RestClient
) : StreetGeometryService {

    private companion object {
        private val OVERPASS_REQUEST_BODY_TMP: String = """
            [out:json];
            (
                way["name"="%s"](%f,%f,%f,%f);
                (._;>;);
            );
            node(w);
            out body; 
            """.trimIndent()
    }

    override fun extractStreet(request: GeometryRequestDto): String {
        val southWest = request.southWestCoordinate
        val northEast = request.northEastCoordinate

        return convertToGeoJson(deserialize(
                overpassClient.post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(OVERPASS_REQUEST_BODY_TMP.format(request.address, southWest.latitude, southWest.longitude, northEast.latitude, northEast.longitude))
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError) { _, response ->
                        throw IncorrectRequestException(response.statusCode.toString())
                    }
                    .body<String>() ?: throw IncorrectRequestException()
        ))
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