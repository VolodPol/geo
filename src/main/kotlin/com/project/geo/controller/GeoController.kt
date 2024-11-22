package com.project.geo.controller

import com.project.geo.dto.GeometryRequestDto
import com.project.geo.service.StreetGeometryService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api")
class GeoController(
    private val streetGeometryService: StreetGeometryService
) {

    @GetMapping("street")
    fun streetGeometry(@RequestBody(required = true) request: GeometryRequestDto): ResponseEntity<String> {
        return ResponseEntity.ok(streetGeometryService.extractStreet(request))
    }
}