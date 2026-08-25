package com.example

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

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

        // Tasks REST API Routes
        route("/tasks") {
            // GET /tasks: คืนค่า List ของ Task ทั้งหมด (Status 200 OK)
            get {
                call.respond(HttpStatusCode.OK, TaskRepository.getAll())
            }

            // GET /tasks/{id}: รับ id จาก Path Parameter ถ้าเจอคืนค่า Task (200 OK) ถ้าไม่พบคืน 404 Not Found
            get("{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid Task ID")
                    return@get
                }

                val task = TaskRepository.getById(id)
                if (task != null) {
                    call.respond(HttpStatusCode.OK, task)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Task not found")
                }
            }

            // POST /tasks: รับ TaskRequest เข้ามา เพิ่มลง Repository แล้วตอบกลับด้วย Task ที่สร้างใหม่ (Status 201 Created)
            post {
                val request = call.receive<TaskRequest>()
                val createdTask = TaskRepository.add(request)
                call.respond(HttpStatusCode.Created, createdTask)
            }

            // PUT /tasks/{id}: รับ id และ Body สำหรับอัปเดต ถ้าสำเร็จตอบ 200 OK ถ้าไม่พบคืน 404 Not Found
            put("{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid Task ID")
                    return@put
                }

                val request = call.receive<TaskRequest>()
                val updatedTask = TaskRepository.update(id, request)
                if (updatedTask != null) {
                    call.respond(HttpStatusCode.OK, updatedTask)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Task not found")
                }
            }

            // DELETE /tasks/{id}: ลบ Task ตาม id ถ้าสำเร็จตอบกลับด้วย 204 No Content ถ้าไม่พบคืน 404 Not Found
            delete("{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid Task ID")
                    return@delete
                }

                val deleted = TaskRepository.delete(id)
                if (deleted) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Task not found")
                }
            }
        }
    }
}