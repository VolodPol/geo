package com.project.geo.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.api.directions.v5.models.DirectionsWaypoint

class DirectionResponse(
    val code: String,
    val message: String,
    @JsonProperty("direction waypoints")
    val waypoints: List<DirectionsWaypoint>,
    @JsonProperty("direction routes")
    val directionRoutes: List<DirectionsRoute>
)