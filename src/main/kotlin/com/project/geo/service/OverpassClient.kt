package com.project.geo.service

import org.springframework.web.client.RestClient

interface OverpassClient {
    fun sendRequest(request: String): RestClient.ResponseSpec
}