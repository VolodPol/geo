package com.project.geo.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("api")
class PingController {
    @GetMapping("ping")
    fun ping(): ResponseEntity<String> {
        return ResponseEntity.ok("pong")
    }
}