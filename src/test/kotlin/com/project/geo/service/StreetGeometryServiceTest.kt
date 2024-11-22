package com.project.geo.service

import com.project.geo.service.impl.StreetGeometryServiceImpl
import kotlin.test.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.InjectMocks
import org.springframework.web.client.RestClient

@ExtendWith(MockitoExtension::class)
class StreetGeometryServiceTest {
    @Mock
    private lateinit var client: RestClient

    @InjectMocks
    private lateinit var service: StreetGeometryServiceImpl


    @Test
    fun verifyOverpassClientError() {


    }
}