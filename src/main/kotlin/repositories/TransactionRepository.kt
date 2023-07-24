package repositories

import models.db.Transaction
import models.request.TransactionRequest
import models.response.TransactionResponse
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

class TransactionRepository : TransactionRepositoryInterface {
    override fun insertTransaction(request: TransactionRequest): TransactionResponse {
        return transaction {
            val transactionId = Transaction.insert {
                it[customerId] = request.customerId
                it[restaurantId] = request.restaurantId
                it[amount] = request.amount
            } get Transaction.id

            TransactionResponse(transactionId, request.customerId, request.restaurantId, request.amount)
        }
    }
}
