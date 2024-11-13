package com.project.geo.controller

import com.mapbox.geojson.Point
import com.mapbox.geojson.Polygon
import com.project.geo.service.DirectionRequest
import com.project.geo.service.DirectionResponse
import com.project.geo.service.DirectionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api")
class GeoController(@Autowired service: DirectionService) {

    private val directionService: DirectionService = service

    @GetMapping(value = ["/geo"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getGeoJson(): String {
        val p1 = Point.fromLngLat(-111.04980468750001, 45.06420960950604)
        val p2 = Point.fromLngLat(-110.98828125, 41.105846596984776)
        val p3 = Point.fromLngLat(-104.044921875, 41.0064335786322)
        val p4 = Point.fromLngLat(-104.044921875, 45.05179258717928)
        val p5 = Point.fromLngLat(p1.longitude(), p1.latitude())

        val polygon: Polygon = Polygon.fromLngLats(listOf(
            listOf(p1, p2, p3, p4, p5)
        ))

        return polygon.toJson()?:""

    }

    @GetMapping(value = ["/direction"], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun directionRequest(@RequestBody(required = true) request: DirectionRequest): ResponseEntity<String> {
        return ResponseEntity.ok(
            directionService.provideRoute(request).toJson()
        )
    }

}