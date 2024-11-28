package com.project.geo.service

import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.project.geo.dto.StreetResponse
import com.project.geo.dto.StreetResponse.Node
import com.project.geo.service.impl.StreetGeometryServiceImpl
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.web.client.body
import kotlin.test.Test


@ExtendWith(MockKExtension::class)
class StreetGeometryServiceTest {
    @MockK
    private lateinit var overpassClient: OverpassClient

    @InjectMockKs
    private lateinit var service: StreetGeometryServiceImpl

    @Test
    fun verifyRegularOutput() {
        val coordinates = listOf(Node(7.0, 50.1), Node(7.1, 50.2))
        every {overpassClient.findStreet(any(), any())} returns StreetResponse(coordinates)

        val expected = coordinates.map { Point.fromLngLat(it.lon, it.lat) }.let { LineString.fromLngLats(it) }
        assertThat(service.extractStreet("", listOf(), listOf()))
            .isEqualTo(expected)
    }

    @Test
    fun verifyEmptyOutput() {
        val coordinates = listOf<Node>()
        every {overpassClient.findStreet(any(), any())} returns StreetResponse(coordinates)

        val expected = LineString.fromLngLats(listOf())
        assertThat(service.extractStreet("", listOf(), listOf()))
            .isEqualTo(expected)
    }

}