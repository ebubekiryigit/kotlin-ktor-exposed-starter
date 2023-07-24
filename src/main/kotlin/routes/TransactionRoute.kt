package routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import models.request.TransactionRequest
import services.TransactionService

fun Route.transactionRoute(transactionService: TransactionService) {
    route("/transactions") {
        post {
            val transactionRequest = call.receive<TransactionRequest>()
            val transactionResponse = transactionService.makeTransaction(transactionRequest)
            call.respond(HttpStatusCode.Created, transactionResponse)
        }
    }
}
