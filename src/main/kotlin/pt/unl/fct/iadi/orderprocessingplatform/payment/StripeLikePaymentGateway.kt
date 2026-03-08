package pt.unl.fct.iadi.orderprocessingplatform.payment

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import pt.unl.fct.iadi.orderprocessingplatform.domain.*
import java.util.UUID

@Component
@Profile("prod")
class StripeLikePaymentGateway : PaymentGateway {

    override fun processPayment(request: PaymentRequest): Receipt {

        if (request.amount <= 0) {
            return Receipt(
                request.orderId,
                ReceiptStatus.REJECTED,
                mapOf(
                    "gateway" to "stripe-like",
                    "reason" to "Invalid amount",
                    "amount" to request.amount
                )
            )
        }

        if (request.amount > 10000) {
            return Receipt(
                request.orderId,
                ReceiptStatus.FLAGGED_FOR_REVIEW,
                mapOf(
                    "gateway" to "stripe-like",
                    "reason" to "High value transaction requires review",
                    "amount" to request.amount
                )
            )
        }

        return Receipt(
            request.orderId,
            ReceiptStatus.PAID,
            mapOf(
                "gateway" to "stripe-like",
                "transactionId" to UUID.randomUUID().toString(),
                "amount" to request.amount
            )
        )
    }
}