package models.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AccountBalance(
    @SerialName("accountId")
    val accountId: Int,

    @SerialName("balance")
    val balance: Double
)
