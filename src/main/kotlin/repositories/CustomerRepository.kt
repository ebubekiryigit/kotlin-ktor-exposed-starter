package repositories

import models.db.Customer
import models.exception.CustomerNotFoundException
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update


class CustomerRepository : CustomerRepositoryInterface {

    override fun getCustomer(customerId: Int): ResultRow {
        return transaction {
            Customer.select { Customer.id eq customerId }.singleOrNull()
                ?: throw CustomerNotFoundException("customer $customerId not found")
        }
    }

    override fun getBalance(customerId: Int): Double {
        return transaction {
            Customer.select { Customer.id eq customerId }.map { it[Customer.balance] }.singleOrNull()
                ?: throw CustomerNotFoundException("customer $customerId not found")
        }
    }

    override fun decreaseBalanceAndLimit(customerId: Int, amount: Double) {
        transaction {
            Customer.update({ Customer.id eq customerId }) {
                with(SqlExpressionBuilder) {
                    it.update(balance, balance - amount)
                    it.update(dailyLimit, dailyLimit - amount)
                }
            }
        }
    }
}
