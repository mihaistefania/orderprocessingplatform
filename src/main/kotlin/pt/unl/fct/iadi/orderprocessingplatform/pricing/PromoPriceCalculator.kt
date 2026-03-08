package pt.unl.fct.iadi.orderprocessingplatform.pricing

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import pt.unl.fct.iadi.orderprocessingplatform.domain.Order

@Component
@ConditionalOnProperty(
    name = ["pricing.promo.enabled"],
    havingValue = "true"
)
class PromoPriceCalculator : PriceCalculator {

    override fun calculateTotalPrice(order: Order): Double {

        return order.items.sumOf {

            if (it.quantity > 5)
                it.quantity * it.price * 0.8
            else
                it.quantity * it.price

        }

    }

}