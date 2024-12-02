package com.project.geo.service

import com.github.tomakehurst.wiremock.junit5.WireMockTest
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.project.geo.NODE_ELEMENTS
import com.project.geo.RESPONSE_HEADER
import com.project.geo.TestUtils.Companion.ADDRESS
import com.project.geo.TestUtils.Companion.NORTH_EAST
import com.project.geo.TestUtils.Companion.SOUTH_WEST
import com.project.geo.TestUtils.Companion.readContent
import com.project.geo.dto.StreetResponse
import com.project.geo.dto.StreetResponse.Node
import com.project.geo.service.impl.OverpassClientImpl
import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.*

import org.springframework.web.client.RestClient


@WireMockTest(httpPort = 8089)
class OverpassClientTest {

    private val overpassRestClientUrl: String = "http://localhost:8089"
    private val overpassClientEndpoint: String = "/api/interpreter"
    private val overpassRestClient: RestClient = RestClient.create(overpassRestClientUrl + overpassClientEndpoint)
    private val client: OverpassClientImpl = OverpassClientImpl(overpassRestClient)

    @Test
    fun `test regular case`() {
        val firstPoint = Node(7.156, 50.724)
        val secondPoint = Node(7.153, 50.723)
        val thirdPoint = Node(7.157, 50.725)

        val intermediateResponse = provideResponseForPoints(firstPoint, secondPoint, thirdPoint)
        val requestBody = OverpassClientImpl.STREET_REQUEST_BODY.format(ADDRESS, *(SOUTH_WEST + NORTH_EAST).toTypedArray())
        stubFor(post(urlEqualTo(overpassClientEndpoint))
            .withRequestBody(equalTo(requestBody))
            .willReturn(okJson(intermediateResponse)))

        val expectedResponse = StreetResponse(listOf(firstPoint, secondPoint, thirdPoint))
        assertThat(client.findStreet(ADDRESS, (SOUTH_WEST + NORTH_EAST).toTypedArray())).isEqualTo(expectedResponse)
    }

    @Test
    fun `test server error`() {
        val requestBody = OverpassClientImpl.STREET_REQUEST_BODY.format(ADDRESS, *(SOUTH_WEST + NORTH_EAST).toTypedArray())
        stubFor(post(urlEqualTo(overpassClientEndpoint))
            .withRequestBody(equalTo(requestBody))
            .willReturn(serverError()))

        assertThatThrownBy { client.findStreet(ADDRESS, (SOUTH_WEST + NORTH_EAST).toTypedArray()) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("500 INTERNAL_SERVER_ERROR")
    }

    @Test
    fun `test empty response`() {
        val requestBody = OverpassClientImpl.STREET_REQUEST_BODY.format(ADDRESS, *(SOUTH_WEST + NORTH_EAST).toTypedArray())
        stubFor(post(urlEqualTo(overpassClientEndpoint))
            .withRequestBody(equalTo(requestBody))
            .willReturn(okJson("")))

        assertThatThrownBy { client.findStreet(ADDRESS, (SOUTH_WEST + NORTH_EAST).toTypedArray()) }
            .isInstanceOf(IllegalArgumentException::class.java)
    }

    private fun provideResponseForPoints(first: Node, second: Node, third: Node): String {
        val intermediateResponse = readContent(RESPONSE_HEADER)
        val listElements = readContent(NODE_ELEMENTS)
        return intermediateResponse.format(
            listElements.format(
                first.lat, first.lon,
                second.lat, second.lon,
                third.lat, third.lon
            )
        )
    }
}