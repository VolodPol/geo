package com.project.geo.service.impl

import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.project.geo.dto.StreetResponse
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

    override fun extractStreet(address: String, southWestPoint: List<Double>, northEastPoint: List<Double>): LineString {
        val south = southWestPoint.first()
        val west = southWestPoint.last()
        val north = northEastPoint.first()
        val east = northEastPoint.last()

        //todo: extract request logic to a separate 'OverpassClient' service
        return convertToGeoJson(
            overpassClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .body(OVERPASS_REQUEST_BODY_TMP.format(address, south, west, north, east))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError) { _, response ->
                    throw IllegalArgumentException(response.statusCode.toString())
                }
                .body<StreetResponse>() ?: throw IllegalArgumentException()
        )
    }

    private fun convertToGeoJson(response: StreetResponse): LineString {
        val points = response.elements
            .map { Point.fromLngLat(it.lon, it.lat) }
            .toMutableList()

        return LineString.fromLngLats(points)
    }
}