package api

import com.google.gson.JsonParser
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import models.db.Customer
import models.db.Restaurant
import models.exception.CustomerNotFoundException
import module
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class TransactionRouteTest {

    @Test
    fun testMakeOneSuccessTransaction() = testApplication {
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

        val response = client.post("/transactions") {
            contentType(ContentType.Application.Json)
            setBody("{\"customerId\":$customerId,\"restaurantId\":$restaurantId,\"amount\":4.5}")
        }
        assertEquals(HttpStatusCode.Created, response.status)

        val jsonBody = JsonParser.parseString(response.bodyAsText()).asJsonObject
        assertEquals(true, jsonBody.get("success").asBoolean)

        val customerNewBalance = newSuspendedTransaction {
            Customer.select { Customer.id eq customerId }.map { it[Customer.balance] }.singleOrNull() ?: 0.0
        }
        assertEquals(15.5, customerNewBalance)

        val restaurantNewBalance = newSuspendedTransaction {
            Restaurant.select { Restaurant.id eq restaurantId }.map { it[Restaurant.balance] }.singleOrNull() ?: 0.0
        }
        assertEquals(54.5, restaurantNewBalance)
    }

    @Test
    fun testTransactionDailyLimit() = testApplication {
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

        val exception = assertFailsWith(IllegalArgumentException::class, block = {
            client.post("/transactions") {
                contentType(ContentType.Application.Json)
                setBody("{\"customerId\":$customerId,\"restaurantId\":$restaurantId,\"amount\":11}")
            }
        })

        assertEquals(exception.message, "You exceed your daily limit")
    }

    @Test
    fun testTransactionLowBalance() = testApplication {
        application {
            module()
        }

        val customerId = newSuspendedTransaction {
            Customer.insert {
                it[balance] = 20.0
                it[dailyLimit] = 30.0
            } get Customer.id
        }

        val restaurantId = newSuspendedTransaction {
            Restaurant.insert {
                it[balance] = 50.0
            } get Restaurant.id
        }

        val exception = assertFailsWith(IllegalArgumentException::class, block = {
            client.post("/transactions") {
                contentType(ContentType.Application.Json)
                setBody("{\"customerId\":$customerId,\"restaurantId\":$restaurantId,\"amount\":21}")
            }
        })

        assertEquals(exception.message, "You don't have enough money")
    }


    @Test
    fun testTransactionInvalidCustomer() = testApplication {
        application {
            module()
        }

        val exception = assertFailsWith(CustomerNotFoundException::class, block = {
            client.post("/transactions") {
                contentType(ContentType.Application.Json)
                setBody("{\"customerId\":100000,\"restaurantId\":200000,\"amount\":5}")
            }
        })

        assertEquals(exception.message, "customer 100000 not found")
    }


    @Test
    fun testTransactionInvalidRestaurant() = testApplication {
        application {
            module()
        }

        val customerId = newSuspendedTransaction {
            Customer.insert {
                it[balance] = 20.0
                it[dailyLimit] = 30.0
            } get Customer.id
        }

        assertFailsWith(Exception::class, block = {
            client.post("/transactions") {
                contentType(ContentType.Application.Json)
                setBody("{\"customerId\":$customerId,\"restaurantId\":100000,\"amount\":5}")
            }
        })

        val newBalance = newSuspendedTransaction {
            Customer.select { Customer.id eq customerId }.map { it[Customer.balance] }.singleOrNull() ?: 0.0
        }

        assertEquals(20.0, newBalance)
    }

    @Test
    fun testMultipleTransactionDailyLimit() = testApplication {
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

        val response1 = client.post("/transactions") {
            contentType(ContentType.Application.Json)
            setBody("{\"customerId\":$customerId,\"restaurantId\":$restaurantId,\"amount\":3.0}")
        }
        assertEquals(HttpStatusCode.Created, response1.status)

        val response2 = client.post("/transactions") {
            contentType(ContentType.Application.Json)
            setBody("{\"customerId\":$customerId,\"restaurantId\":$restaurantId,\"amount\":5.0}")
        }
        assertEquals(HttpStatusCode.Created, response2.status)

        val response3 = client.post("/transactions") {
            contentType(ContentType.Application.Json)
            setBody("{\"customerId\":$customerId,\"restaurantId\":$restaurantId,\"amount\":2.0}")
        }
        assertEquals(HttpStatusCode.Created, response3.status)

        val exception = assertFailsWith(IllegalArgumentException::class, block = {
            client.post("/transactions") {
                contentType(ContentType.Application.Json)
                setBody("{\"customerId\":$customerId,\"restaurantId\":$restaurantId,\"amount\":1.0}")
            }
        })

        assertEquals(exception.message, "You exceed your daily limit")

    }
}