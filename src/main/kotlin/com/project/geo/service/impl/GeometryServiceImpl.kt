package com.project.geo.service.impl

import com.mapbox.api.geocoding.v5.MapboxGeocoding
import com.mapbox.geojson.*
import com.project.geo.dto.GeometryRequestDto
import com.project.geo.exceptions.NoGeometryFoundException
import com.project.geo.service.GeometryService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class GeometryServiceImpl: GeometryService {

    @Value("\${mapbox.token}")
    private lateinit var token: String

    override fun geometryByAddress(request: GeometryRequestDto): String {
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