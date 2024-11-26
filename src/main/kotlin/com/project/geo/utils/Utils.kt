package com.project.geo.utils

import com.fasterxml.jackson.databind.json.JsonMapper

inline fun <reified C> String.deserializeByClass(): C {
    return JsonMapper()
        .readerFor(C::class.java)
        .readValue(this)
}