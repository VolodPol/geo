package com.project.geo.controller

import com.project.geo.service.*
import com.project.geo.service.dto.DirectionRequest
import com.project.geo.service.dto.PolygonDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api")
class GeoController(
    @Autowired val geometryService: GeometryService,
    @Autowired val directionService: DirectionService
) {

    @GetMapping(value = ["/polygon"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getGeoJson(@RequestBody(required = true) dto: PolygonDto): ResponseEntity<String> {
        return ResponseEntity.ok(
            geometryService.polygonByCoordinates(dto.coordinates).toJson()
        )
    }

    @GetMapping(value = ["/direction"], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun directionRequest(@RequestBody(required = true) request: DirectionRequest): ResponseEntity<String> {
        return ResponseEntity.ok(
            directionService.provideRoute(request).toJson()
        )
    }

}