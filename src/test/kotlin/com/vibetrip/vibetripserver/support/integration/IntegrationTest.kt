package com.vibetrip.vibetripserver.support.integration

import org.junit.jupiter.api.Tag
import org.springframework.boot.test.context.SpringBootTest

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Tag("integration")
@SpringBootTest
annotation class IntegrationTest
