package com.project.geo.service

import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.project.geo.TestUtils.Companion.ADDRESS
import com.project.geo.TestUtils.Companion.NORTH_EAST
import com.project.geo.TestUtils.Companion.SOUTH_WEST
import com.project.geo.dto.StreetResponse
import com.project.geo.dto.StreetResponse.Node
import com.project.geo.service.impl.StreetGeometryServiceImpl
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.Test


@ExtendWith(MockKExtension::class)
class StreetGeometryServiceTest {
    @MockK
    private lateinit var overpassClient: OverpassClient

    @InjectMockKs
    private lateinit var service: StreetGeometryServiceImpl

    private val coordinates: Array<Double> = (SOUTH_WEST + NORTH_EAST).toTypedArray()

    @Test
    fun verifyRegularOutput() {
        val nodes = listOf(Node(7.0, 50.1), Node(7.1, 50.2))
        every {overpassClient.findStreet(ADDRESS, coordinates)} returns StreetResponse(nodes)

        val expected = nodes.map { Point.fromLngLat(it.lon, it.lat) }.let { LineString.fromLngLats(it) }
        assertThat(service.extractStreet(ADDRESS, SOUTH_WEST, NORTH_EAST))
            .isEqualTo(expected)
    }

    @Test
    fun verifyEmptyOutput() {
        val nodes = listOf<Node>()
        every {overpassClient.findStreet(ADDRESS, coordinates)} returns StreetResponse(nodes)

        val expected = LineString.fromLngLats(listOf())
        assertThat(service.extractStreet(ADDRESS, SOUTH_WEST, NORTH_EAST))
            .isEqualTo(expected)
    }
}