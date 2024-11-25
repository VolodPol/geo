package com.project.geo.service

interface StreetGeometryService {
    fun extractStreet(address: String, southWestPoint: List<Double>, northEastPoint: List<Double>): String
}