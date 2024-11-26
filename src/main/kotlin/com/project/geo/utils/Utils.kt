package com.project.geo.utils

import com.fasterxml.jackson.databind.json.JsonMapper

fun <C> String.deserializeByClass(clazz: Class<C>): C {
    return JsonMapper()
        .readerFor(clazz)
        .readValue(this)
}