package com.example.routes

import com.example.models.BookRequest
import com.example.models.CheckoutRequest
import com.example.repositories.LibraryRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureLibraryRoutes() {
    routing {
        
        route("/books") {
            // Get all books
            get {
                call.respond(HttpStatusCode.OK, LibraryRepository.getAllBooks())
            }

            // Get book by id
            get("{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid Book ID")
                    return@get
                }

                val book = LibraryRepository.getBookById(id)
                if (book != null) {
                    call.respond(HttpStatusCode.OK, book)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Book not found")
                }
            }

            // Create new book
            post {
                val request = call.receive<BookRequest>()
                val createdBook = LibraryRepository.createBook(request)
                call.respond(HttpStatusCode.Created, createdBook)
            }

            // Update book
            put("{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid Book ID")
                    return@put
                }

                val request = call.receive<BookRequest>()
                val updatedBook = LibraryRepository.updateBook(id, request)
                if (updatedBook != null) {
                    call.respond(HttpStatusCode.OK, updatedBook)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Book not found")
                }
            }

            // Delete book
            delete("{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid Book ID")
                    return@delete
                }

                if (LibraryRepository.deleteBook(id)) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Book not found")
                }
            }
        }
        
        route("/lending") {
            // Get all lending records
            get {
                 call.respond(HttpStatusCode.OK, LibraryRepository.getAllLendingRecords())
            }

            // Checkout a book
            post("/checkout") {
                val request = call.receive<CheckoutRequest>()
                val record = LibraryRepository.checkoutBook(request)
                if (record != null) {
                    call.respond(HttpStatusCode.Created, record)
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Book is not available or does not exist")
                }
            }

            // Return a book (specify the lending record id to close it)
            put("/return/{recordId}") {
                val recordId = call.parameters["recordId"]?.toIntOrNull()
                if (recordId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid Record ID")
                    return@put
                }

                val returnedRecord = LibraryRepository.returnBook(recordId)
                if (returnedRecord != null) {
                    call.respond(HttpStatusCode.OK, returnedRecord)
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Lending record not found or already returned")
                }
            }
        }

    }
}
