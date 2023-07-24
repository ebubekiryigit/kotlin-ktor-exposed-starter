package routes

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import models.response.BaseResponse
import services.AccountService

fun Route.accountRoute(accountService: AccountService) {
    route("/accounts") {

        get("customer/{id}") {
            val id = call.parameters["id"]?.toIntOrNull() ?: throw IllegalArgumentException("Must provide account id")
            val accountBalance = accountService.getCustomerBalance(id)
            call.respond(BaseResponse(success = true, data = accountBalance))
        }

        get("restaurant/{id}") {
            val id = call.parameters["id"]?.toIntOrNull() ?: throw IllegalArgumentException("Must provide account id")
            val accountBalance = accountService.getRestaurantBalance(id)
            call.respond(BaseResponse(success = true, data = accountBalance))
        }

        // maybe we can simplify like this
        // get("{type}/{id}") {}
    }
}
