package com.project.geo.service

import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.geojson.Point


class DirectionRequest (start: Coordinate, destination: Coordinate, overview: String, mode: String) {
    val start: Point
    val destination: Point
    val overviewMode: String
    val profileMode: String

    init {
        this.start = Point.fromLngLat(start.longitude, start.latitude)
        this.destination = Point.fromLngLat(destination.longitude, destination.latitude)
        this.overviewMode = overview
        this.profileMode = Mode.valueOf(mode).toString()
    }

    enum class Mode(val value: String){
        TRAFFIC(DirectionsCriteria.PROFILE_DRIVING_TRAFFIC),
        DRIVING(DirectionsCriteria.PROFILE_DRIVING),
        WALKING(DirectionsCriteria.PROFILE_WALKING),
        CYCLING(DirectionsCriteria.PROFILE_CYCLING)
    }
}
