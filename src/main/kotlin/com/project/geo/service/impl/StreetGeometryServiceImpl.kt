package com.project.geo.service.impl

import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.project.geo.service.OverpassClient
import com.project.geo.service.StreetGeometryService
import org.springframework.stereotype.Service

@Service
class StreetGeometryServiceImpl(
    private val overpassClient: OverpassClient
) : StreetGeometryService {

    override fun extractStreet(address: String, southWestPoint: List<Double>, northEastPoint: List<Double>): LineString {
        val points = (southWestPoint + northEastPoint).toTypedArray()

        return overpassClient.findStreet(address, points)
                .elements
                .map { Point.fromLngLat(it.lon, it.lat) }
                .toMutableList().let { LineString.fromLngLats(it) }
    }
}