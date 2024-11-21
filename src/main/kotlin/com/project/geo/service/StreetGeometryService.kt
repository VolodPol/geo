package com.project.geo.service

import com.project.geo.dto.GeometryRequestDto

interface StreetGeometryService {
    fun extractStreet(request: GeometryRequestDto): String
}