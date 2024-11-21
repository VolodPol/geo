package com.project.geo.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class Node(
    @JsonProperty("lon") val longitude: Double,
    @JsonProperty("lat") val latitude: Double
)
