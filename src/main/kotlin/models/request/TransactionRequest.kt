package models.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TransactionRequest(
    @SerialName("customerId")
    val customerId: Int,

    @SerialName("restaurantId")
    val restaurantId: Int,

    @SerialName("amount")
    val amount: Double
)
