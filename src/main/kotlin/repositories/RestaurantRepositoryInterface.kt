package repositories


interface RestaurantRepositoryInterface {
    fun getBalance(id: Int): Double

    fun increaseBalance(restaurantId: Int, amount: Double)
}
