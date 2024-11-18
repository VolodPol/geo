package com.project.geo.service

import com.mapbox.api.geocoding.v5.MapboxGeocoding
import com.mapbox.geojson.*
import com.project.geo.service.dto.Coordinate
import com.project.geo.service.dto.GeometryRequestDto
import com.project.geo.service.exceptions.NoGeometryFoundException
import com.project.geo.service.exceptions.NotValidPointsNumber
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class GeometryService {

    @Value("\${mapbox.token}")
    private lateinit var token: String

    fun polygonByCoordinates(coordinates: List<Coordinate>): Polygon {
        val points = coordinates.asSequence()
            .map { Point.fromLngLat(it.longitude, it.latitude) }
            . toMutableList()
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

    fun geometryByAddress(request: GeometryRequestDto): String {
        val box: BoundingBox = BoundingBox.fromPoints(
            Point.fromLngLat(request.southWestCoordinate.longitude, request.southWestCoordinate.latitude),
            Point.fromLngLat(request.northEastCoordinate.longitude, request.northEastCoordinate.latitude)
        )
        val geocoding = MapboxGeocoding.builder()
            .bbox(box)
            .accessToken(token)
            .query(request.address)
            .build()

        val response = geocoding.executeCall()
        val results = response.body()!!.features()

        if (results.size > 0) {
            val feature = FeatureCollection.fromFeatures(
                results.map { Feature.fromGeometry(it.geometry()) }
            )

            return feature.toJson()
        } else {
            throw NoGeometryFoundException("No Geometry found for the specified BoundingBox and country")
        }
    }


}