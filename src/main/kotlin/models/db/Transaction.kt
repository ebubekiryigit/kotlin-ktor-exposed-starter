package models.db

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object Transaction : Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val customerId: Column<Int> = (integer("customer_id") references Customer.id)
    val restaurantId: Column<Int> = (integer("restaurant_id") references Restaurant.id)
    val amount: Column<Double> = double("amount")

    override val primaryKey = PrimaryKey(id)
}