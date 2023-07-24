package service

import io.ktor.server.testing.*
import models.db.Customer
import models.db.Restaurant
import models.request.TransactionRequest
import module
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.junit.Test
import repositories.CustomerRepository
import repositories.RestaurantRepository
import repositories.TransactionRepository
import services.TransactionService
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class TransactionServiceTest {

    private lateinit var customerRepository: CustomerRepository
    private lateinit var restaurantRepository: RestaurantRepository
    private lateinit var transactionRepository: TransactionRepository
    private lateinit var transactionService: TransactionService

    @BeforeTest
    fun setup() {
        customerRepository = CustomerRepository()
        restaurantRepository = RestaurantRepository()
        transactionRepository = TransactionRepository()
        transactionService = TransactionService(customerRepository, restaurantRepository, transactionRepository)
    }

    @Test
    fun testMakeValidTransaction() = testApplication {
        application {
            module()
        }

        val customerId = newSuspendedTransaction {
            Customer.insert {
                it[balance] = 53.2
                it[dailyLimit] = 12.5
            } get Customer.id
        }

        val restaurantId = newSuspendedTransaction {
            Restaurant.insert {
                it[balance] = 150.5
            } get Restaurant.id
        }

        val transactionRequest = TransactionRequest(customerId, restaurantId, 10.0)
        val response = transactionService.makeTransaction(transactionRequest)

        assertEquals(true, response.success)
        assertEquals(customerId, response.data?.customerId)
        assertEquals(restaurantId, response.data?.restaurantId)
        assertEquals(10.0, response.data?.amount)
    }

    @Test
    fun testMakeZeroAmountTransaction() = testApplication {
        application {
            module()
        }

        val customerId = newSuspendedTransaction {
            Customer.insert {
                it[balance] = 20.0
                it[dailyLimit] = 10.0
            } get Customer.id
        }

        val restaurantId = newSuspendedTransaction {
            Restaurant.insert {
                it[balance] = 50.0
            } get Restaurant.id
        }

        val transactionRequest = TransactionRequest(customerId, restaurantId, 0.0)
        val response = transactionService.makeTransaction(transactionRequest)

        assertEquals(true, response.success)
        assertEquals(customerId, response.data?.customerId)
        assertEquals(restaurantId, response.data?.restaurantId)
        assertEquals(0.0, response.data?.amount)
    }


    @Test
    fun testMakeNegativeAmountTransaction() = testApplication {
        application {
            module()
        }

        val customerId = newSuspendedTransaction {
            Customer.insert {
                it[balance] = 20.0
                it[dailyLimit] = 10.0
            } get Customer.id
        }

        val restaurantId = newSuspendedTransaction {
            Restaurant.insert {
                it[balance] = 50.0
            } get Restaurant.id
        }

        val transactionRequest = TransactionRequest(customerId, restaurantId, -1.0)
        val exception = assertFailsWith(IllegalArgumentException::class, block = {
            transactionService.makeTransaction(transactionRequest)
        })

        assertEquals(exception.message, "Transaction amount cannot be less than zero")

    }
}