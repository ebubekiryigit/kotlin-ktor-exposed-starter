package api

import com.google.gson.JsonParser
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import models.db.Customer
import models.db.Restaurant
import models.exception.EntityNotFoundException
import module
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class AccountRouteTest {

    @Test
    fun testGetAValidCustomer() = testApplication {
        application {
            module()
        }

        val customerId = newSuspendedTransaction {
            Customer.insert {
                it[balance] = 20.0
                it[dailyLimit] = 10.0
            } get Customer.id
        }

        val response = client.get("/accounts/customer/$customerId")
        assertEquals(HttpStatusCode.OK, response.status)

        val jsonBody = JsonParser.parseString(response.bodyAsText()).asJsonObject
        assertEquals(true, jsonBody.get("success").asBoolean)

        val data = jsonBody.get("data").asJsonObject
        assertEquals(customerId, data.get("accountId").asInt)
        assertEquals(20.0, data.get("balance").asDouble)
    }

    @Test
    fun testGetAnInvalidCustomer() = testApplication {
        application {
            module()
        }

        assertFailsWith(EntityNotFoundException::class, block = {
            client.get("/accounts/customer/100000")
        })
    }

    @Test
    fun testInvalidCustomerId() = testApplication {
        application {
            module()
        }

        assertFailsWith(IllegalArgumentException::class, block = {
            client.get("/accounts/customer/notInt")
        })
    }


    @Test
    fun testGetAValidRestaurant() = testApplication {
        application {
            module()
        }

        val restaurantId = newSuspendedTransaction {
            Restaurant.insert {
                it[balance] = 50.0
            } get Restaurant.id
        }

        val response = client.get("/accounts/restaurant/$restaurantId")
        assertEquals(HttpStatusCode.OK, response.status)

        val jsonBody = JsonParser.parseString(response.bodyAsText()).asJsonObject
        assertEquals(true, jsonBody.get("success").asBoolean)

        val data = jsonBody.get("data").asJsonObject
        assertEquals(restaurantId, data.get("accountId").asInt)
        assertEquals(50.0, data.get("balance").asDouble)
    }

    @Test
    fun testGetAnInvalidRestaurant() = testApplication {
        application {
            module()
        }

        assertFailsWith(EntityNotFoundException::class, block = {
            client.get("/accounts/restaurant/100000")
        })
    }

    @Test
    fun testInvalidRestaurantId() = testApplication {
        application {
            module()
        }

        assertFailsWith(IllegalArgumentException::class, block = {
            client.get("/accounts/restaurant/notInt")
        })
    }
}