package com.project.geo.service

import com.github.tomakehurst.wiremock.junit5.WireMockTest
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.mapbox.geojson.Point
import com.project.geo.NODE_ELEMENTS
import com.project.geo.RESPONSE_HEADER
import com.project.geo.service.impl.OverpassClientImpl
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.springframework.beans.factory.annotation.Value
import org.springframework.web.client.RestClient
import java.nio.charset.StandardCharsets


@WireMockTest(httpPort = 56327)
class OverpassClientTest {

    @Value("\${overpass.url}")
    private lateinit var overpassRestClientUrl: String
    private lateinit var overpassRestClient: RestClient
    private lateinit var client: OverpassClientImpl

    @BeforeEach
    fun init() {
        overpassRestClient = RestClient.create(overpassRestClientUrl)
        client = OverpassClientImpl(overpassRestClient)
    }

    @Test
    fun `test regular case`() {
        val firstPoint = Point.fromLngLat(7.156, 50.724)
        val secondPoint = Point.fromLngLat(7.153, 50.723)
        val thirdPoint = Point.fromLngLat(7.157, 50.725)

        val intermediateResponse = provideRegularIntermediateResponse(firstPoint, secondPoint, thirdPoint)
        stubFor(post(overpassRestClientUrl)
            .withRequestBody(equalTo(OverpassClientImpl.STREET_REQUEST_BODY.format(ADDRESS, *(SOUTH_WEST + NORTH_EAST).toTypedArray())))
            .willReturn(okJson(intermediateResponse)))


        println(
            client.findStreet(ADDRESS, (SOUTH_WEST + NORTH_EAST).toTypedArray())
        )
    }

    companion object {
        private const val ADDRESS = "John Doe Street"
        private val SOUTH_WEST: List<Double> = listOf(16.2, 50.8)
        private val NORTH_EAST: List<Double> = listOf(7.1, 45.0)

        private val INTERMEDIATE_RESPONSE = readContent(RESPONSE_HEADER)
        private val LIST_ELEMENTS = readContent(NODE_ELEMENTS)

        fun readContent(file: String): String {
            var content = ""
            try {
                Thread.currentThread().contextClassLoader.getResourceAsStream(file)?.use {
                    content = String(it.readAllBytes(), StandardCharsets.UTF_8)
                }
            } catch (_: Exception) {throw IllegalArgumentException("Resource file '$file' not found")}
            return content
        }
    }

    private fun provideRegularIntermediateResponse(first: Point, second: Point, third: Point): String {
        return INTERMEDIATE_RESPONSE.format(
            LIST_ELEMENTS.format(
                first.latitude(), first.longitude(),
                second.latitude(), second.longitude(),
                third.latitude(), third.longitude()
            )
        )
    }
}