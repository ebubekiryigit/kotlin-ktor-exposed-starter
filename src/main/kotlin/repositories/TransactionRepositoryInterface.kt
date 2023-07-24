package repositories

import models.request.TransactionRequest
import models.response.TransactionResponse

interface TransactionRepositoryInterface {
    fun insertTransaction(request: TransactionRequest): TransactionResponse
}
