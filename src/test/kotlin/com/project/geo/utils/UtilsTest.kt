package com.project.geo.utils

import com.project.geo.NODE_COMPARISON_PRECISION
import com.project.geo.NODE_ELEMENTS
import com.project.geo.RESPONSE_HEADER
import com.project.geo.dto.Node
import com.project.geo.dto.StreetResponse
import com.project.geo.service.StreetGeometryServiceTest

import org.junit.Test
import java.util.concurrent.ThreadLocalRandom
import org.assertj.core.api.Assertions.*


class UtilsTest {
    private val responseHeader = StreetGeometryServiceTest.readContent(RESPONSE_HEADER)

    @Test
    fun testDeserialization() {
        val content = StreetGeometryServiceTest.readContent(NODE_ELEMENTS)

        val points = coordinates()
        val response = responseHeader.format(content.format(*points))

        val expectedNodes = listOf(Node(points[1], points[0]), Node(points[3], points[2]), Node(points[5], points[4]))
        val deserialized = response.deserializeByClass<StreetResponse>()
        for (i in deserialized.elements.indices)
            compareNodes(deserialized.elements[i], expectedNodes[i])
    }

    @Test
    fun testEmptyDeserialization() {
        val response = responseHeader.format("")
        val deserialized = response.deserializeByClass<StreetResponse>()
        assertThat(deserialized.elements.size).isZero()
    }

    private fun compareNodes(actual: Node, expected: Node) {
        assertThat(actual.lat).isEqualTo(expected.lat, withPrecision(NODE_COMPARISON_PRECISION))
        assertThat(actual.lon).isEqualTo(expected.lon, withPrecision(NODE_COMPARISON_PRECISION))
    }

    private fun coordinates(): Array<Double> {
        return generateSequence { ThreadLocalRandom.current().nextDouble() }
            .take(6)
            .toList()
            .toTypedArray()
    }
}