package com.project.geo.service

import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.project.geo.dto.Coordinate
import com.project.geo.dto.GeometryRequestDto
import com.project.geo.exceptions.IncorrectRequestException
import com.project.geo.service.impl.StreetGeometryServiceImpl
import kotlin.test.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.ArgumentMatchers.any
import org.mockito.InjectMocks
import org.mockito.Mockito.*
import org.springframework.http.MediaType
import org.springframework.web.client.RestClient
import org.springframework.web.client.body

@ExtendWith(MockitoExtension::class)
class StreetGeometryServiceTest {
    @Mock
    private lateinit var client: RestClient

    @InjectMocks
    private lateinit var service: StreetGeometryServiceImpl

    private val fakeAddress = GeometryRequestDto(
        address = "John Doe's Street",
        northEastCoordinate = Coordinate(45.0, 7.1),
        southWestCoordinate = Coordinate(50.8, 16.2)
    )
    private val regularIntermediateResponse = """
            {
              "version": 0.6,
              "generator": "Overpass API 0.7.62.4 2390de5a",
              "osm3s": {
                "timestamp_osm_base": "2024-11-22T13:00:16Z",
                "copyright": "The data included in this document is from www.openstreetmap.org. The data is made available under ODbL."
              },
              "elements": [
            {
              "type": "node",
              "id": 9717107887,
              "lat": 50.7241254,
              "lon": 7.1564241
            },
            {
              "type": "node",
              "id": 9998437079,
              "lat": 50.7234090,
              "lon": 7.1537021,
              "tags": {
                "crossing": "unmarked",
                "crossing:markings": "no",
                "highway": "crossing",
                "tactile_paving": "no"
              }
            },
            {
              "type": "node",
              "id": 12310564388,
              "lat": 50.7242889,
              "lon": 7.1570839
            }
              ]
            }
        """.trimIndent()

    private val emptyIntermediateResponse = """
        {
          "version": 0.6,
          "generator": "Overpass API 0.7.62.4 2390de5a",
          "osm3s": {
            "timestamp_osm_base": "2024-11-22T13:28:52Z",
            "copyright": "The data included in this document is from www.openstreetmap.org. The data is made available under ODbL."
          },
          "elements": [



          ]
        }
    """.trimIndent()


    @Test
    fun verifyOverpassClientError() {
        val mockResponseSpec = mockClientCommonBehaviour()
        `when`(mockResponseSpec.onStatus(any(), any()))
            .thenThrow(IncorrectRequestException("400"))

        assertThrows(IncorrectRequestException::class.java) {
            service.extractStreet(fakeAddress)
        }
    }

    @Test
    fun verifyRegularOutput() {
        val mockResponseSpec = mockClientCommonBehaviour()
        `when`(mockResponseSpec.onStatus(any(), any())).thenReturn(mockResponseSpec)
        `when`(mockResponseSpec.body<String>()).thenReturn(regularIntermediateResponse)

        val expected: String = LineString.fromLngLats(
            mutableListOf(
                Point.fromLngLat(7.1564241, 50.7241254),
                Point.fromLngLat(7.1537021, 50.7234090),
                Point.fromLngLat(7.1570839, 50.7242889)
            )
        ).toJson()

        assertEquals(expected,
            service.extractStreet(fakeAddress)
        )
    }

    @Test
    fun verifyEmptyOutput() {
        val mockResponseSpec = mockClientCommonBehaviour()
        `when`(mockResponseSpec.onStatus(any(), any())).thenReturn(mockResponseSpec)
        `when`(mockResponseSpec.body<String>()).thenReturn(emptyIntermediateResponse)

        val expected = "{\"type\":\"LineString\",\"coordinates\":[]}"
        assertEquals(expected, service.extractStreet(fakeAddress))
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
}