package service

import io.ktor.server.testing.*
import models.db.Customer
import models.db.Restaurant
import module
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.junit.Test
import repositories.CustomerRepository
import repositories.RestaurantRepository
import services.AccountService
import kotlin.test.BeforeTest
import kotlin.test.assertEquals

class AccountServiceTest {

    private lateinit var customerRepository: CustomerRepository
    private lateinit var restaurantRepository: RestaurantRepository
    private lateinit var accountService: AccountService

    @BeforeTest
    fun setup() {
        customerRepository = CustomerRepository()
        restaurantRepository = RestaurantRepository()
        accountService = AccountService(customerRepository, restaurantRepository)
    }

    @Test
    fun testGetCustomersAccount() = testApplication {
        application {
            module()
        }

        val customerId = newSuspendedTransaction {
            Customer.insert {
                it[balance] = 53.2
                it[dailyLimit] = 12.5
            } get Customer.id
        }

        val balance = accountService.getCustomerBalance(customerId)
        assertEquals(53.2, balance.balance)
        assertEquals(customerId, balance.accountId)
    }

    @Test
    fun testGetRestaurantsAccount() = testApplication {
        application {
            module()
        }

        val restaurantId = newSuspendedTransaction {
            Restaurant.insert {
                it[balance] = 150.5
            } get Restaurant.id
        }

        val balance = accountService.getRestaurantBalance(restaurantId)
        assertEquals(150.5, balance.balance)
        assertEquals(restaurantId, balance.accountId)
    }
}