package database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import models.db.Customer
import models.db.Restaurant
import models.db.Transaction
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import javax.sql.DataSource

object DatabaseFactory {

    fun init() {
        val connectionPool = hikari()
        Database.connect(connectionPool)
        flyway(connectionPool)

        transaction {
            SchemaUtils.create(Customer)
            SchemaUtils.create(Restaurant)
            SchemaUtils.create(Transaction)

            // create dummy rows
            Customer.insert {
                it[balance] = 20.0
                it[dailyLimit] = 10.0
            }

            Customer.insert {
                it[balance] = 30.0
                it[dailyLimit] = 5.0
            }

            Restaurant.insert {
                it[balance] = 100.0
            }

            Restaurant.insert {
                it[balance] = 130.0
            }

            Restaurant.insert {
                it[balance] = 140.0
            }
        }
    }

    private fun flyway(dataSource: DataSource) {
        val flyway = Flyway.configure().dataSource(dataSource).load()
        flyway.info()
        flyway.migrate()
    }

    private fun hikari(): HikariDataSource {
        val config = HikariConfig().apply {
            driverClassName = "org.h2.Driver"
            jdbcUrl = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;"
            maximumPoolSize = 3
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }
        return HikariDataSource(config)
    }
}
