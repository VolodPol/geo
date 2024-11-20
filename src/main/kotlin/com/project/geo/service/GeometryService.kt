package com.project.geo.service

import com.project.geo.dto.GeometryRequestDto

interface GeometryService {
    fun geometryByAddress(request: GeometryRequestDto): String
}