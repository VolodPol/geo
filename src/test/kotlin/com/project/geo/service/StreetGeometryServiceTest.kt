package com.project.geo.service

import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.project.geo.exceptions.IncorrectRequestException
import com.project.geo.service.impl.StreetGeometryServiceImpl
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.MediaType
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import java.nio.charset.StandardCharsets
import kotlin.test.Test


@ExtendWith(MockitoExtension::class)
class StreetGeometryServiceTest {
    @Mock
    private lateinit var client: RestClient

    @InjectMocks
    private lateinit var service: StreetGeometryServiceImpl

    private companion object {
        private const val ADDRESS = "John Doe Street"
        private val SOUTH_WEST: List<Double> = listOf(16.2, 50.8)
        private val NORTH_EAST: List<Double> = listOf(7.1, 45.0)

        private val INTERMEDIATE_RESPONSE = readContent("overpass_response_header.txt")
        private val NODE_ELEMENTS = readContent("overpass_node_elements.txt")

        private fun readContent(file: String): String {
            var content = ""
            try {
                Thread.currentThread().contextClassLoader.getResourceAsStream(file)?.use {
                    content = String(it.readAllBytes(), StandardCharsets.UTF_8)
                }
            } catch (_: Exception) {throw IllegalArgumentException("Resource file '$file' not found")}
            return content
        }
    }

    @Test
    fun verifyOverpassClientError() {
        val mockResponseSpec = mockClientCommonBehaviour()
        `when`(mockResponseSpec.onStatus(any(), any()))
            .thenThrow(IncorrectRequestException("400"))

        assertThrows(IncorrectRequestException::class.java) {
            service.extractStreet(ADDRESS, SOUTH_WEST, NORTH_EAST)
        }
    }

    @Test
    fun verifyRegularOutput() {
        val firstPoint = Point.fromLngLat(7.1564241, 50.7241254)
        val secondPoint = Point.fromLngLat(7.1537021, 50.7234090)
        val thirdPoint = Point.fromLngLat(7.1570839, 50.7242889)

        val mockResponseSpec = mockClientCommonBehaviour()
        `when`(mockResponseSpec.onStatus(any(), any())).thenReturn(mockResponseSpec)
        `when`(mockResponseSpec.body<String>()).thenReturn(
            provideRegularIntermediateResponse(firstPoint, secondPoint, thirdPoint)
        )

        val expected: String = LineString.fromLngLats(mutableListOf(firstPoint, secondPoint, thirdPoint)).toJson()
        assertEquals(expected, service.extractStreet(ADDRESS, SOUTH_WEST, NORTH_EAST))
    }

    @Test
    fun verifyEmptyOutput() {
        val mockResponseSpec = mockClientCommonBehaviour()
        `when`(mockResponseSpec.onStatus(any(), any())).thenReturn(mockResponseSpec)
        `when`(mockResponseSpec.body<String>()).thenReturn(INTERMEDIATE_RESPONSE.format(""))

        val expected = "{\"type\":\"LineString\",\"coordinates\":[]}"
        assertEquals(expected, service.extractStreet(ADDRESS, SOUTH_WEST, NORTH_EAST))
    }

    private fun mockClientCommonBehaviour(): RestClient.ResponseSpec {
        val mockRequestSpec = mock(RestClient.RequestBodyUriSpec::class.java)
        val mockRequestBodySpec = mock(RestClient.RequestBodySpec::class.java)
        val mockResponseSpec = mock(RestClient.ResponseSpec::class.java)

        `when`(client.post()).thenReturn(mockRequestSpec)
        `when`(mockRequestSpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(mockRequestSpec)
        `when`(mockRequestSpec.body(anyString())).thenReturn(mockRequestBodySpec)
        `when`(mockRequestBodySpec.retrieve()).thenReturn(mockResponseSpec)

        return mockResponseSpec
    }

    private fun provideRegularIntermediateResponse(first: Point, second: Point, third: Point): String {
        return INTERMEDIATE_RESPONSE.format(
            NODE_ELEMENTS.format(
                first.latitude(), first.longitude(),
                second.latitude(), second.longitude(),
                third.latitude(), third.longitude()
            )
        )
    }
}