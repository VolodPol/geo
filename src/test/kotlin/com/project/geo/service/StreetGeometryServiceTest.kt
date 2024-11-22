package com.project.geo.service

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

@ExtendWith(MockitoExtension::class)
class StreetGeometryServiceTest {
    @Mock
    private lateinit var client: RestClient

    @InjectMocks
    private lateinit var service: StreetGeometryServiceImpl

    @Test
    fun verifyOverpassClientError() {
        // Mock RestClient behavior
        val mockRequestSpec = mock(RestClient.RequestBodyUriSpec::class.java)
        val mockRequestBodySpec = mock(RestClient.RequestBodySpec::class.java)
        val mockResponseSpec = mock(RestClient.ResponseSpec::class.java)

        `when`(client.post()).thenReturn(mockRequestSpec)
        `when`(mockRequestSpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(mockRequestSpec)
        `when`(mockRequestSpec.body(anyString())).thenReturn(mockRequestBodySpec)
        `when`(mockRequestBodySpec.retrieve()).thenReturn(mockResponseSpec)
        `when`(mockResponseSpec.onStatus(any(), any()))
            .thenThrow(IncorrectRequestException("400"))

        assertThrows(IncorrectRequestException::class.java) {
            service.extractStreet(GeometryRequestDto(
                address = "John Doe's Street",
                northEastCoordinate = Coordinate(45.0, 15.3),
                southWestCoordinate = Coordinate(50.1, 16.2)
            ))
        }
    }
}