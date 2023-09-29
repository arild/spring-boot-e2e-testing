package com.example.e2e.invoice.example2

import com.example.e2e.invoice.EndToEndTest
import com.example.e2e.invoice.OrderProducer
import com.example.e2e.invoice.example1.oneMonthAgo
import com.example.e2e.kafka.OrderEvent
import com.example.e2e.kafka.OrderLineEvent
import com.example.e2e.model.OrderRepository
import io.kotest.matchers.nulls.shouldNotBeNull
import org.hamcrest.CoreMatchers
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.testcontainers.shaded.org.awaitility.Awaitility.await
import java.math.BigDecimal
import java.time.Duration.ofSeconds
import java.util.concurrent.TimeUnit.SECONDS

class InvoiceExportDatabaseTest(
    val orderProducer: OrderProducer,
    val mockMvc: MockMvc,
    val orderRepository: OrderRepository,
) : EndToEndTest({

    "Marks orders as exported during export" {
        orderProducer.send(
            OrderEvent(
                userId = 1,
                created = oneMonthAgo(),
                orderLines = listOf(OrderLineEvent(price = BigDecimal(10.0)), OrderLineEvent(price = BigDecimal(20.0))),
            ),
        )
        orderProducer.send(
            OrderEvent(
                userId = 2,
                created = oneMonthAgo(),
                orderLines = listOf(OrderLineEvent(price = BigDecimal(10.0))),
            ),
        )

        await()
            .pollInterval(ofSeconds(1))
            .atMost(10, SECONDS)
            .untilAsserted {
                mockMvc.post("/invoice/export")
                    .andExpect { status().isOk }
                    .andExpect { jsonPath("$.length()", CoreMatchers.equalTo(2)) }
            }

        orderRepository.findAll().forEach { order ->
            order.exported.shouldNotBeNull()
        }
    }
})
