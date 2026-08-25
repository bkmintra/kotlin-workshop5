package com.example.repositories

import com.example.models.Book
import com.example.models.BookRequest
import com.example.models.CheckoutRequest
import com.example.models.LendingRecord
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object BooksTable : Table() {
    val id = integer("id").autoIncrement()
    val title = varchar("title", 255)
    val author = varchar("author", 255)
    val isAvailable = bool("isAvailable").default(true)

    override val primaryKey = PrimaryKey(id)
}

object LendingRecordsTable : Table() {
    val id = integer("id").autoIncrement()
    val bookId = integer("bookId").references(BooksTable.id)
    val borrowerName = varchar("borrowerName", 255)
    val checkoutDate = date("checkoutDate")
    val returnDate = date("returnDate").nullable()

    override val primaryKey = PrimaryKey(id)
}

object LibraryRepository {
    
    fun initDb() {
        transaction {
            SchemaUtils.create(BooksTable, LendingRecordsTable)
        }
    }

    // Book CRUD
    fun getAllBooks(): List<Book> {
        return transaction {
            BooksTable.selectAll().map {
                Book(
                    id = it[BooksTable.id],
                    title = it[BooksTable.title],
                    author = it[BooksTable.author],
                    isAvailable = it[BooksTable.isAvailable]
                )
            }
        }
    }

    fun getBookById(id: Int): Book? {
        return transaction {
            BooksTable.selectAll().where { BooksTable.id eq id }.map {
                Book(
                    id = it[BooksTable.id],
                    title = it[BooksTable.title],
                    author = it[BooksTable.author],
                    isAvailable = it[BooksTable.isAvailable]
                )
            }.singleOrNull()
        }
    }

    fun createBook(request: BookRequest): Book {
        return transaction {
            val insertId = BooksTable.insert {
                it[title] = request.title
                it[author] = request.author
                it[isAvailable] = true
            } get BooksTable.id

            getBookById(insertId)!!
        }
    }

    fun updateBook(id: Int, request: BookRequest): Book? {
        return transaction {
            val updatedCount = BooksTable.update({ BooksTable.id eq id }) {
                it[title] = request.title
                it[author] = request.author
            }
            if (updatedCount > 0) getBookById(id) else null
        }
    }

    fun deleteBook(id: Int): Boolean {
        return transaction {
            BooksTable.deleteWhere { BooksTable.id eq id } > 0
        }
    }

    // Lending CRUD / Logic
    fun getAllLendingRecords(): List<LendingRecord> {
        return transaction {
            LendingRecordsTable.selectAll().map {
                LendingRecord(
                    id = it[LendingRecordsTable.id],
                    bookId = it[LendingRecordsTable.bookId],
                    borrowerName = it[LendingRecordsTable.borrowerName],
                    checkoutDate = it[LendingRecordsTable.checkoutDate].toString(),
                    returnDate = it[LendingRecordsTable.returnDate]?.toString()
                )
            }
        }
    }

    
    fun getLendingRecordById(id: Int): LendingRecord? {
        return transaction {
            LendingRecordsTable.selectAll().where { LendingRecordsTable.id eq id }.map {
                LendingRecord(
                    id = it[LendingRecordsTable.id],
                    bookId = it[LendingRecordsTable.bookId],
                    borrowerName = it[LendingRecordsTable.borrowerName],
                    checkoutDate = it[LendingRecordsTable.checkoutDate].toString(),
                    returnDate = it[LendingRecordsTable.returnDate]?.toString()
                )
            }.singleOrNull()
        }
    }


    /**
     * Checkout a book. Transaction ensures state integrity.
     */
    fun checkoutBook(request: CheckoutRequest): LendingRecord? {
        return transaction {
            val book = getBookById(request.bookId)
            
            // If book doesn't exist or not available, rollback/fail
            if (book == null || !book.isAvailable) {
                return@transaction null
            }

            // Mark book as unavailable
            BooksTable.update({ BooksTable.id eq request.bookId }) {
                it[isAvailable] = false
            }

            // Create LendingRecord
            val insertId = LendingRecordsTable.insert {
                it[bookId] = request.bookId
                it[borrowerName] = request.borrowerName
                it[checkoutDate] = LocalDate.now()
            } get LendingRecordsTable.id

            getLendingRecordById(insertId)
        }
    }

    /**
     * Return a book. Update the LendingRecord returnDate and set Book isAvailable to true.
     */
    fun returnBook(recordId: Int): LendingRecord? {
        return transaction {
            val record = getLendingRecordById(recordId)
            if (record == null || record.returnDate != null) {
                // Not found or already returned
                return@transaction null
            }

            // Mark book as available
            BooksTable.update({ BooksTable.id eq record.bookId }) {
                it[isAvailable] = true
            }

            // Set return date
            LendingRecordsTable.update({ LendingRecordsTable.id eq recordId }) {
                it[returnDate] = LocalDate.now()
            }

            getLendingRecordById(recordId)
        }
    }
}
