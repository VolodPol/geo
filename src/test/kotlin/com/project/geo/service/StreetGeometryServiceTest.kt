package com.project.geo.service

import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.project.geo.exceptions.IncorrectRequestException
import com.project.geo.service.impl.StreetGeometryServiceImpl
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.MediaType
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import java.nio.charset.StandardCharsets
import kotlin.test.Test


@ExtendWith(MockKExtension::class)
class StreetGeometryServiceTest {
    @MockK
    private lateinit var client: RestClient

    @InjectMockKs
    private lateinit var service: StreetGeometryServiceImpl

    companion object {
        private const val ADDRESS = "John Doe Street"
        private val SOUTH_WEST: List<Double> = listOf(16.2, 50.8)
        private val NORTH_EAST: List<Double> = listOf(7.1, 45.0)

        private val INTERMEDIATE_RESPONSE = readContent("overpass_response_header.txt")
        private val NODE_ELEMENTS = readContent("overpass_node_elements.txt")

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

    @Test
    fun verifyOverpassClientError() {
        val mockResponseSpec = mockClientCommonBehaviour()
        val errorMessage = "400"
        every {mockResponseSpec.onStatus(any(), any())} throws IncorrectRequestException(errorMessage)

        assertThatThrownBy { service.extractStreet(ADDRESS, SOUTH_WEST, NORTH_EAST) }
            .hasMessage(errorMessage)
            .isInstanceOf(IncorrectRequestException::class.java)
    }

    @Test
    fun verifyRegularOutput() {
        val firstPoint = Point.fromLngLat(7.1564241, 50.7241254)
        val secondPoint = Point.fromLngLat(7.1537021, 50.7234090)
        val thirdPoint = Point.fromLngLat(7.1570839, 50.7242889)

        val mockResponseSpec = mockClientCommonBehaviour()
        every {mockResponseSpec.onStatus(any(), any())} returns mockResponseSpec
        every {mockResponseSpec.body<String>()} returns (
            provideRegularIntermediateResponse(firstPoint, secondPoint, thirdPoint)
        )

        val expected: String = LineString.fromLngLats(mutableListOf(firstPoint, secondPoint, thirdPoint)).toJson()
        assertThat(service.extractStreet(ADDRESS, SOUTH_WEST, NORTH_EAST)).isEqualTo(expected)
    }

    @Test
    fun verifyEmptyOutput() {
        val mockResponseSpec = mockClientCommonBehaviour()
        every {mockResponseSpec.onStatus(any(), any())} returns mockResponseSpec
        every {mockResponseSpec.body<String>()} returns INTERMEDIATE_RESPONSE.format("")

        val expected = "{\"type\":\"LineString\",\"coordinates\":[]}"
        assertThat(service.extractStreet(ADDRESS, SOUTH_WEST, NORTH_EAST)).isEqualTo(expected)
    }

    private fun mockClientCommonBehaviour(): RestClient.ResponseSpec {
        val mockRequestSpec = mockk<RestClient.RequestBodyUriSpec>()
        val mockRequestBodySpec = mockk<RestClient.RequestBodySpec>()
        val mockResponseSpec = mockk<RestClient.ResponseSpec>()

        every { client.post() } returns mockRequestSpec
        every { mockRequestSpec.contentType(MediaType.APPLICATION_JSON) } returns mockRequestSpec
        every { mockRequestSpec.body(any<String>()) } returns mockRequestBodySpec
        every { mockRequestBodySpec.retrieve() } returns mockResponseSpec

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