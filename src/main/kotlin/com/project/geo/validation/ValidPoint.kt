package com.project.geo.validation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import jakarta.validation.constraints.Size
import kotlin.reflect.KClass

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [])
@Size(min = 2, max = 2, message = ERROR_MESSAGE)
annotation class ValidPoint(
    val message: String = ERROR_MESSAGE,
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)