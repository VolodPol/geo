package com.project.geo.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class GeometryRequestDto(
    val address: String,
    @JsonProperty("south_west_point")
    val southWestCoordinate: Coordinate,
    @JsonProperty("north_east_point")
    val northEastCoordinate: Coordinate
)