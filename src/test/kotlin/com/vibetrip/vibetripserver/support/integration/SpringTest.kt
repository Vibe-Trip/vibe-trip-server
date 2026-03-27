package com.vibetrip.vibetripserver.support.integration

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.mysql.MySQLContainer

@SpringBootTest
@ActiveProfiles("test")
class SpringTest {
    companion object {
        var container: MySQLContainer =
            MySQLContainer("mysql:8.0.44-debian")
                .withDatabaseName("retrip")
                .withUsername("test")
                .withPassword("test")

        init {
            container.start()
        }

        @DynamicPropertySource
        fun configureProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", container::getJdbcUrl)
            registry.add("spring.datasource.username", container::getUsername)
            registry.add("spring.datasource.password", container::getPassword)
        }
    }
}
