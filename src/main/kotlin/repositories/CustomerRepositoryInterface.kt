package repositories

import org.jetbrains.exposed.sql.ResultRow


interface CustomerRepositoryInterface {

    fun getCustomer(customerId: Int): ResultRow

    fun getBalance(customerId: Int): Double

    fun decreaseBalanceAndLimit(customerId: Int, amount: Double)
}
