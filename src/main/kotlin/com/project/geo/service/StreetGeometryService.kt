package com.project.geo.service

import com.mapbox.geojson.LineString

interface StreetGeometryService {
    fun extractStreet(address: String, southWestPoint: List<Double>, northEastPoint: List<Double>): LineString
}