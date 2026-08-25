package com.example

import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlin.test.*

class ServerTest {

    @BeforeTest
    fun setUp() {
        TaskRepository.clear()
    }

    @Test
    fun `test root endpoint`() = testApplication {
        configure()
        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Hello, World!", response.bodyAsText())
    }

    @Test
    fun `test tasks CRUD operations`() = testApplication {
        configure()
        val jsonClient = createClient {
            install(ClientContentNegotiation) {
                json()
            }
        }

        // 1. GET /tasks initially empty
        val initialGet = jsonClient.get("/tasks")
        assertEquals(HttpStatusCode.OK, initialGet.status)
        assertEquals("[]", initialGet.bodyAsText())

        // 2. POST /tasks create first task
        val postResponse1 = jsonClient.post("/tasks") {
            contentType(ContentType.Application.Json)
            setBody("""{"content":"Learn Ktor","isDone":false}""")
        }
        assertEquals(HttpStatusCode.Created, postResponse1.status)
        assertTrue(postResponse1.bodyAsText().contains(""""id":1"""))
        assertTrue(postResponse1.bodyAsText().contains(""""content":"Learn Ktor""""))

        // 3. POST /tasks create second task
        val postResponse2 = jsonClient.post("/tasks") {
            contentType(ContentType.Application.Json)
            setBody("""{"content":"Build REST API","isDone":true}""")
        }
        assertEquals(HttpStatusCode.Created, postResponse2.status)
        assertTrue(postResponse2.bodyAsText().contains(""""id":2"""))

        // 4. GET /tasks/1
        val getTask1 = jsonClient.get("/tasks/1")
        assertEquals(HttpStatusCode.OK, getTask1.status)
        assertTrue(getTask1.bodyAsText().contains(""""content":"Learn Ktor""""))

        // 5. GET /tasks/999 (Not Found)
        val getTaskNotFound = jsonClient.get("/tasks/999")
        assertEquals(HttpStatusCode.NotFound, getTaskNotFound.status)

        // 6. PUT /tasks/1 (Update task)
        val putResponse = jsonClient.put("/tasks/1") {
            contentType(ContentType.Application.Json)
            setBody("""{"content":"Master Ktor","isDone":true}""")
        }
        assertEquals(HttpStatusCode.OK, putResponse.status)
        assertTrue(putResponse.bodyAsText().contains(""""content":"Master Ktor""""))
        assertTrue(putResponse.bodyAsText().contains(""""isDone":true"""))

        // 7. PUT /tasks/999 (Update Not Found)
        val putNotFound = jsonClient.put("/tasks/999") {
            contentType(ContentType.Application.Json)
            setBody("""{"content":"Non-existent","isDone":false}""")
        }
        assertEquals(HttpStatusCode.NotFound, putNotFound.status)

        // 8. DELETE /tasks/1 (Success -> 204 No Content)
        val deleteResponse = jsonClient.delete("/tasks/1")
        assertEquals(HttpStatusCode.NoContent, deleteResponse.status)

        // 9. DELETE /tasks/1 again (Not Found -> 404)
        val deleteAgainResponse = jsonClient.delete("/tasks/1")
        assertEquals(HttpStatusCode.NotFound, deleteAgainResponse.status)

        // 10. GET /tasks after delete (should only have task 2)
        val remainingTasks = jsonClient.get("/tasks")
        assertEquals(HttpStatusCode.OK, remainingTasks.status)
        assertFalse(remainingTasks.bodyAsText().contains(""""id":1"""))
        assertTrue(remainingTasks.bodyAsText().contains(""""id":2"""))
    }
}
