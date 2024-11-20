package com.project.geo.service

import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.project.geo.dto.DirectionRequest

interface DirectionService {
    fun provideRoute(request: DirectionRequest): DirectionsResponse
}