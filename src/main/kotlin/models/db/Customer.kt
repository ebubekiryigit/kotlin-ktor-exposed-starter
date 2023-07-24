package models.db

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object Customer : Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val balance: Column<Double> = double("balance")
    val dailyLimit: Column<Double> = double("daily_limit")

    override val primaryKey = PrimaryKey(id)
}