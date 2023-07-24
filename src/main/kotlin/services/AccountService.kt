package services

import models.response.AccountBalance
import repositories.CustomerRepository
import repositories.RestaurantRepository

class AccountService(
    private val customerRepository: CustomerRepository,
    private val restaurantRepository: RestaurantRepository
) {
    fun getCustomerBalance(id: Int): AccountBalance {
        val balance = customerRepository.getBalance(id)
        return AccountBalance(id, balance)
    }

    fun getRestaurantBalance(id: Int): AccountBalance {
        val balance = restaurantRepository.getBalance(id)
        return AccountBalance(id, balance)
    }
}
