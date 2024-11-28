package com.project.geo.controller


import com.project.geo.service.StreetGeometryService
import com.project.geo.validation.ValidPoint
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
@RequestMapping("api")
class GeoController(
    private val streetGeometryService: StreetGeometryService
) {

    @GetMapping("street/{address}")
    fun streetGeometry(@PathVariable address: String,
                       @ValidPoint @RequestParam southWest: List<Double>,
                       @ValidPoint @RequestParam northEast: List<Double>
    ): ResponseEntity<String> {
        val streetLine = streetGeometryService.extractStreet(address, southWest, northEast)
        return ResponseEntity.ok(streetLine.toJson())
    }
}