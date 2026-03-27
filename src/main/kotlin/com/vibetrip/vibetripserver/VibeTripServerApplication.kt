package com.vibetrip.vibetripserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.retry.annotation.EnableRetry

@EnableRetry
@SpringBootApplication
class VibeTripServerApplication

fun main(args: Array<String>) {
    runApplication<VibeTripServerApplication>(*args)
}
