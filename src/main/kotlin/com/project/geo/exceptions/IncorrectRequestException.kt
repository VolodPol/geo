package com.project.geo.exceptions

class IncorrectRequestException: RuntimeException {
    constructor() : super()
    constructor(message: String): super(message)
}