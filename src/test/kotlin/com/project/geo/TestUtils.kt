package com.project.geo

import java.nio.charset.StandardCharsets

class TestUtils {
    companion object {
        const val ADDRESS = "John Doe Street"
        val SOUTH_WEST: List<Double> = listOf(7.1, 45.0)
        val NORTH_EAST: List<Double> = listOf(16.2, 50.8)

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
}