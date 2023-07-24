package models.db

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object Restaurant : Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val balance: Column<Double> = double("balance")

    override val primaryKey = PrimaryKey(id)
}