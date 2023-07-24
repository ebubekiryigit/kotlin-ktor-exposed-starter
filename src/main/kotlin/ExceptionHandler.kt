import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import models.exception.EntityNotFoundException
import models.response.BaseResponse

fun Application.installExceptionHandler() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            if (cause is IllegalArgumentException || cause is EntityNotFoundException) {
                call.respond(
                    HttpStatusCode.BadRequest, BaseResponse(success = false, message = cause.message, data = null)
                )
            } else {
                // log unhandled exceptions
                call.respond(
                    HttpStatusCode.InternalServerError,
                    BaseResponse(success = false, message = "An internal error occurred", data = null)
                )
            }
            throw cause  // re-throw the error to print it in logs
        }
    }
}