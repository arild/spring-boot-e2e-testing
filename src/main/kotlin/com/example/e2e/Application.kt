package com.example.e2e

import com.example.e2e.model.Order
import com.example.e2e.model.OrderLine
import com.example.e2e.model.OrderRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.math.BigDecimal
import java.time.Clock
import java.time.Instant

@SpringBootApplication
class Application(val orderRepository: OrderRepository) : CommandLineRunner {

    override fun run(vararg args: String?) {
        val testOrder = Order(
            id = 1,
            userId = 100,
            created = Instant.now(),
            orderLines = listOf(OrderLine(price = BigDecimal(10.0)), OrderLine(price = BigDecimal(20.0)))
        )

        orderRepository.save(testOrder)
    }
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}


@Configuration
class ClockConfig {
    @Bean
    fun clock(): Clock {
        return Clock.systemUTC()
    }
}
