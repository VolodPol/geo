package com.project.geo.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class StreetResponse @JsonCreator constructor(
    @JsonProperty("elements") val elements: List<Node>
)