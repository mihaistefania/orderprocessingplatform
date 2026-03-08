package pt.unl.fct.iadi.orderprocessingplatform

import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import pt.unl.fct.iadi.orderprocessingplatform.domain.Order
import pt.unl.fct.iadi.orderprocessingplatform.domain.PaymentRequest
import pt.unl.fct.iadi.orderprocessingplatform.payment.PaymentGateway
import pt.unl.fct.iadi.orderprocessingplatform.pricing.PriceCalculator
import java.math.BigDecimal
import java.math.RoundingMode

@Component
class OrderProcessor(
    private val priceCalculator: PriceCalculator,
    private val paymentGateway: PaymentGateway
) : CommandLineRunner {

    fun processOrder(order: Order): List<String> {

        val output = mutableListOf<String>()

        output.add("Order ID: ${order.id}")
        output.add("User ID: ${order.userId}")
        output.add("Created at: ${order.createdAt}")
        output.add("")

        output.add("Items:")

        order.items.forEach {
            val itemTotal = it.quantity * it.price
            output.add("- ${it.productId}: ${it.quantity} x ${it.price} = $itemTotal")
        }

        val totalPrice = priceCalculator.calculateTotalPrice(order)

        val roundedTotal = BigDecimal(totalPrice)
            .setScale(2, RoundingMode.HALF_UP)
            .toDouble()

        output.add("")
        output.add("Total Price: $roundedTotal")
        output.add("Calculator Used: ${priceCalculator.javaClass.simpleName}")
        output.add("")

        val paymentRequest = PaymentRequest(order.id, roundedTotal)

        val receipt = paymentGateway.processPayment(paymentRequest)

        output.add("Payment Status: ${receipt.status}")
        output.add("Payment Gateway: ${receipt.metadata["gateway"]}")

        if (receipt.metadata.containsKey("transactionId")) {
            output.add("Transaction ID: ${receipt.metadata["transactionId"]}")
        }

        if (receipt.metadata.containsKey("reason")) {
            output.add("Reason: ${receipt.metadata["reason"]}")
        }

        output.add("")
        output.add("=== Processing Complete ===")

        return output
    }

    override fun run(vararg args: String?) {

        val order = Order(
            id = "ORD-2026-001",
            userId = "user123",
            items = listOf(
                Order.OrderItem("LAPTOP-001", 2, 999.99),
                Order.OrderItem("MOUSE-042", 3, 29.99),
                Order.OrderItem("KEYBOARD-123", 1, 149.99)
            )
        )

        val result = processOrder(order)

        result.forEach { println(it) }
    }
}