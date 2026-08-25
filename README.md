# Kotlin Workshop 5: Library Management REST API

This project is a RESTful backend API for managing a library system, built with **Kotlin**, **Ktor**, **Exposed ORM**, and an in-memory **H2 database**. It provides endpoints for managing books (CRUD operations) as well as a lending system to allow users to check out and return books.

##  Technologies Used

- **Language:** [Kotlin](https://kotlinlang.org/)
- **Framework:** [Ktor Server](https://ktor.io/)
- **Serialization:** kotlinx.serialization (JSON)
- **Database/ORM:** [Exposed](https://github.com/JetBrains/Exposed) with H2 Database
- **Build Tool:** Gradle (Kotlin DSL)

##  Features

- **Book Management:** Add, retrieve, update, and delete books in the library.
- **Lending System:**
  - Checkout limits availability status of a book and generates a borrowing record.
  - Return functionality records when the book is brought back and updates its availability.
- **In-Memory Database:** Easy to set up without requiring a complex external database instance.

##  Building & Running

You can interact with the project using the Gradle wrapper (`./gradlew` on Unix, or `gradlew.bat` on Windows).

| Command           | Description                                            |
| ----------------- | ------------------------------------------------------ |
| `./gradlew build` | Build the project completely                           |
| `./gradlew test`  | Run the test suite                                     |
| `./gradlew run`   | Start the development server at `http://0.0.0.0:8080/` |

**Example Server Output:**

```
[main] INFO  Application - Application started in 0.303 seconds.
[main] INFO  Application - Responding at http://0.0.0.0:8080
```

##  API Endpoints

### Books API

| Method | Endpoint      | Description                                         |
| ------ | ------------- | --------------------------------------------------- |
| GET    | `/books`      | Retrieve a list of all books                        |
| GET    | `/books/{id}` | Retrieve details of a specific book by ID           |
| POST   | `/books`      | Add a new book (requires `title`, `author` in body) |
| PUT    | `/books/{id}` | Update an existing book's details                   |
| DELETE | `/books/{id}` | Delete a book by ID                                 |

### Lending Records API

| Method | Endpoint                     | Description                                         |
| ------ | ---------------------------- | --------------------------------------------------- |
| GET    | `/lending`                   | View all lending records                            |
| GET    | `/lending/{id}`              | View a specific lending record by its ID            |
| POST   | `/lending/checkout`          | Checkout a book (requires `bookId`, `borrowerName`) |
| POST   | `/lending/return/{recordId}` | Return a book using the record's ID                 |

## 📁 Project Structure

The core business flows are stored under `src/main/kotlin/com/example/`:

- **`models/`**: Contains data models like `Book`, `LendingRecord`, `BookRequest`, etc.
- **`repositories/`**: Houses `LibraryRepository.kt` containing Exposed table definitions and ORM database transactions.
- **`routes/`**: Handles incoming HTTP requests and binds them to repository logic (`LibraryRoutes.kt`).
