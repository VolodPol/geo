package com.project.geo.service

import com.project.geo.dto.StreetResponse

interface OverpassClient {
    fun findStreet(street: String, coordinates: Array<Double>): StreetResponse
}