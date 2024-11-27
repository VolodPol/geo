package com.project.geo.service.impl

import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.project.geo.dto.StreetResponse
import com.project.geo.service.OverpassClient
import com.project.geo.service.StreetGeometryService
import org.springframework.stereotype.Service
import org.springframework.web.client.body

@Service
class StreetGeometryServiceImpl(
    private val overpassClient: OverpassClient
) : StreetGeometryService {

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

    override fun extractStreet(address: String, southWestPoint: List<Double>, northEastPoint: List<Double>): LineString {
        val points = (southWestPoint + northEastPoint).toTypedArray()

        return convertToGeoJson(
            overpassClient
                .sendRequest(STREET_REQUEST_BODY.format(address, *points))
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