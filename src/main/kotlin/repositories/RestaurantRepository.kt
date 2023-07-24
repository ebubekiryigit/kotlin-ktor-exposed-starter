package repositories

import models.db.Restaurant
import models.exception.RestaurantNotFoundException
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class RestaurantRepository : RestaurantRepositoryInterface {

    override fun getBalance(id: Int): Double {
        return transaction {
            Restaurant.select { Restaurant.id eq id }.map { it[Restaurant.balance] }.singleOrNull()
                ?: throw RestaurantNotFoundException("restaurant $id not found")
        }
    }

    override fun increaseBalance(restaurantId: Int, amount: Double) {
        transaction {
            Restaurant.update({ Restaurant.id eq restaurantId }) {
                with(SqlExpressionBuilder) {
                    it.update(balance, balance + amount)
                }
            }
        }
    }
}
