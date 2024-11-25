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
                       @RequestParam("south_west") southWest: List<Double>,
                       @RequestParam("north_east") northEast: List<Double>
    ): ResponseEntity<String> {
        return ResponseEntity.ok(streetGeometryService.extractStreet(address, southWest, northEast))
    }
}