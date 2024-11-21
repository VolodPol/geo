package com.project.geo.controller

import com.project.geo.dto.DirectionRequest
import com.project.geo.dto.GeometryRequestDto
import com.project.geo.service.StreetGeometryService
import com.project.geo.service.impl.DirectionServiceImpl
import com.project.geo.service.impl.GeometryServiceImpl
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api")
class GeoController(
    private val geometryService: GeometryServiceImpl,
    private val directionService: DirectionServiceImpl,
    private val streetGeometryService: StreetGeometryService
) {

    @GetMapping("direction")
    fun directionRequest(@RequestBody(required = true) request: DirectionRequest): ResponseEntity<String> {
        return ResponseEntity.ok(
            directionService.provideRoute(request).toJson()
        )
    }

    @GetMapping("geometry")
    fun geometryByAddressAndBounding(
        @RequestBody(required = true) request: GeometryRequestDto
    ): ResponseEntity<String> {
        return ResponseEntity.ok(geometryService.geometryByAddress(request))
    }

    @GetMapping("street")
    fun streetGeometry(
        @RequestBody(required = true) request: GeometryRequestDto
    ): ResponseEntity<String> {
        return ResponseEntity.ok(streetGeometryService.extractStreet(request))
    }
}