package com.example

import com.example.models.BookRequest
import com.example.models.CheckoutRequest
import com.example.repositories.LibraryRepository
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.*

class LibraryTest {

    @BeforeTest
    fun setUp() {
        // Initialize in-memory H2 specifically for testing
        Database.connect("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")
        LibraryRepository.initDb()
        
        // Clean up the database before each test
        transaction {
            org.jetbrains.exposed.sql.SchemaUtils.drop(com.example.repositories.BooksTable, com.example.repositories.LendingRecordsTable)
            org.jetbrains.exposed.sql.SchemaUtils.create(com.example.repositories.BooksTable, com.example.repositories.LendingRecordsTable)
        }
    }

    @Test
    fun `test 1 create book`() {
        val created = LibraryRepository.createBook(BookRequest(title = "Kotlin in Action", author = "Dmitry Jemerov"))
        assertNotNull(created)
        assertEquals("Kotlin in Action", created.title)
        assertTrue(created.isAvailable)
    }

    @Test
    fun `test 2 checkout available book creates lending record and updates book status`() {
        val book = LibraryRepository.createBook(BookRequest(title = "Coroutine Guide", author = "Roman Elizarov"))
        
        val record = LibraryRepository.checkoutBook(CheckoutRequest(book.id, "Alice"))
        
        assertNotNull(record)
        assertEquals(book.id, record.bookId)
        assertEquals("Alice", record.borrowerName)
        
        val updatedBook = LibraryRepository.getBookById(book.id)
        assertNotNull(updatedBook)
        assertFalse(updatedBook.isAvailable, "Book should not be available after checkout")
    }

    @Test
    fun `test 3 checkout unavailable book fails`() {
        val book = LibraryRepository.createBook(BookRequest(title = "Effective Java", author = "Joshua Bloch"))
        
        // First checkout succeeds
        val record1 = LibraryRepository.checkoutBook(CheckoutRequest(book.id, "Alice"))
        assertNotNull(record1)

        // Second checkout should fail (returns null)
        val record2 = LibraryRepository.checkoutBook(CheckoutRequest(book.id, "Bob"))
        assertNull(record2, "Should not be able to checkout an unavailable book")
    }

    @Test
    fun `test 4 return book updates lending record and book status`() {
        val book = LibraryRepository.createBook(BookRequest(title = "Clean Code", author = "Robert C. Martin"))
        val record = LibraryRepository.checkoutBook(CheckoutRequest(book.id, "Charlie"))
        assertNotNull(record)

        val returnedRecord = LibraryRepository.returnBook(record.id)
        assertNotNull(returnedRecord)
        assertNotNull(returnedRecord.returnDate, "Return date should be set")

        val updatedBook = LibraryRepository.getBookById(book.id)
        assertTrue(updatedBook!!.isAvailable, "Book should be available again after return")
    }

    @Test
    fun `test 5 return already returned book fails`() {
        val book = LibraryRepository.createBook(BookRequest(title = "Design Patterns", author = "GoF"))
        val record = LibraryRepository.checkoutBook(CheckoutRequest(book.id, "Dave"))
        assertNotNull(record)

        // First return succeeds
        val return1 = LibraryRepository.returnBook(record.id)
        assertNotNull(return1)

        // Second return fails
        val return2 = LibraryRepository.returnBook(record.id)
        assertNull(return2, "Should not be able to return a book that was already returned")
    }
}
