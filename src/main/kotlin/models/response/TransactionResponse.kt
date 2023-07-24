package models.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TransactionResponse(

    @SerialName("transactionId")
    val transactionId: Int,

    @SerialName("customerId")
    val customerId: Int,

    @SerialName("restaurantId")
    val restaurantId: Int,

    @SerialName("amount")
    val amount: Double
)
