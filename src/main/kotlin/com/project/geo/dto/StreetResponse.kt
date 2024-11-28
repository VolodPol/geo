package com.project.geo.dto


data class StreetResponse (
    val elements: List<Node>
) {
    data class Node(val lon: Double, val lat: Double)
}