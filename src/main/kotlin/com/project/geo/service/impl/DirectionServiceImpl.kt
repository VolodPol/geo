package com.project.geo.service.impl

import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.MapboxDirections
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.project.geo.dto.DirectionRequest
import com.project.geo.service.DirectionService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class DirectionServiceImpl: DirectionService {

    @Value("\${mapbox.token}")
    private lateinit var token: String

    override fun provideRoute(request: DirectionRequest): DirectionsResponse {

        val client = MapboxDirections.builder()
            .origin(request.start)
            .destination(request.destination)
            .overview(DirectionsCriteria.OVERVIEW_FULL)
            .profile(DirectionsCriteria.PROFILE_DRIVING)
            .accessToken(token)
            .build()

        val response = client.executeCall()

        if (response.body() == null) {
            throw RuntimeException("No routes found, make sure you set the right user and access token.")
        } else if (response.body()!!.routes().size < 1) {
            println("No routes found")
        }

        return response.body()!!
    }

}