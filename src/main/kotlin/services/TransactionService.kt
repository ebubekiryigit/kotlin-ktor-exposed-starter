package services

import models.db.Customer
import models.request.TransactionRequest
import models.response.BaseResponse
import models.response.TransactionResponse
import org.jetbrains.exposed.sql.transactions.transaction
import repositories.*

class TransactionService(
    private val customerRepository: CustomerRepositoryInterface,
    private val restaurantRepository: RestaurantRepositoryInterface,
    private val transactionRepository: TransactionRepositoryInterface
) {
    fun makeTransaction(request: TransactionRequest): BaseResponse<TransactionResponse> {

        if (request.amount < 0) {
            throw IllegalArgumentException("Transaction amount cannot be less than zero")
        }

        return transaction {
            val customer = customerRepository.getCustomer(request.customerId)

            if (request.amount > customer[Customer.balance]) {
                throw IllegalArgumentException("You don't have enough money")
            }

            if (request.amount > customer[Customer.dailyLimit]) {
                throw IllegalArgumentException("You exceed your daily limit")
            }

            customerRepository.decreaseBalanceAndLimit(request.customerId, request.amount)
            restaurantRepository.increaseBalance(request.restaurantId, request.amount)
            val transactionResponse = transactionRepository.insertTransaction(request)
            BaseResponse(true, data = transactionResponse)
        }
    }
}

