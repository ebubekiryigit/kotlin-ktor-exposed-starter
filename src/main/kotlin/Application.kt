import database.DatabaseFactory
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import repositories.CustomerRepository
import repositories.RestaurantRepository
import repositories.TransactionRepository
import routes.accountRoute
import routes.transactionRoute
import services.AccountService
import services.TransactionService

fun main() {
    embeddedServer(
        Netty,
        port = 8080,
        host = "0.0.0.0",
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {
    install(DefaultHeaders)
    install(CallLogging)
    install(IgnoreTrailingSlash)

    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
        })
    }

    DatabaseFactory.init() // connect to db and migrate

    installExceptionHandler() // handle global exceptions

    val customerRepository = CustomerRepository()
    val restaurantRepository = RestaurantRepository()
    val transactionRepository = TransactionRepository()

    val accountService = AccountService(customerRepository, restaurantRepository)
    val transactionService = TransactionService(customerRepository, restaurantRepository, transactionRepository)

    install(Routing) {
        accountRoute(accountService)
        transactionRoute(transactionService)
    }
}
