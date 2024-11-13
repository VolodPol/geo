package com.project.geo.service

import com.mapbox.geojson.Point
import com.mapbox.geojson.Polygon
import com.project.geo.service.exceptions.NotValidPointsNumber
import org.springframework.stereotype.Service

@Service
class GeometryService {

    fun polygonByCoordinates(coordinates: List<Coordinate>): Polygon {
        val points = coordinates.asSequence()
            .map { Point.fromLngLat(it.longitude, it.latitude) }
            .toMutableList()
        validatePoints(points)
        return Polygon.fromLngLats(
            listOf(
                points
            )
        )
    }

    private fun validatePoints(points: MutableList<Point>) {
        if (points.size < 3)
            throw NotValidPointsNumber("To create a Polygon you need to specify at lest ${4} Points instead of ${points.size}")

        val firstPoint = points.first()
        if (firstPoint != points.last() && points.size > 2) {
            points.add(Point.fromLngLat(firstPoint.longitude(), firstPoint.latitude()))
        }
    }

}