package com.project.geo.dto

import com.fasterxml.jackson.annotation.JsonCreator

data class StreetResponse @JsonCreator constructor(
    val elements: List<Node>
)