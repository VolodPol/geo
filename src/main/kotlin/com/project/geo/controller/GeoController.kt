package com.project.geo.controller


import com.project.geo.service.StreetGeometryService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api")
class GeoController(
    private val streetGeometryService: StreetGeometryService
) {

    @GetMapping("street/{address}")
    fun streetGeometry(@PathVariable address: String,
                       @RequestParam southWest: List<Double>,
                       @RequestParam northEast: List<Double>
    ): ResponseEntity<String> {
        if (southWest.size != 2 || northEast.size != 2)
            throw IllegalArgumentException("Two pairs of coordinates must be provided")

        val streetLine = streetGeometryService.extractStreet(address, southWest, northEast)
        return ResponseEntity.ok(streetLine.toJson())
    }
}