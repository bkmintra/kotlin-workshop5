package com.example

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import com.example.routes.configureLibraryRoutes

fun Application.configureRouting() {
    install(ContentNegotiation) {
        json()
    }

    routing {
        get("/") {
            call.respondText("Hello, World!")
        }

        // Hello endpoints
        get("/hello/{name}") {
            val name = call.parameters["name"] ?: "World"
            call.respondText("Hello, $name!")
        }

        get("/hello") {
            val name = call.request.queryParameters["name"] ?: "World"
            call.respondText("Hello, $name!")
        }

        // Library REST API Routes
        configureLibraryRoutes()
    }
}