คุณคือ Kotlin Backend Developer ช่วยเขียนโค้ด Kotlin ด้วย Ktor Framework สำหรับสร้าง REST API จัดการ Task ตามโครงสร้างและเงื่อนไขต่อไปนี้:

1. Data Modeling (ใช้ kotlinx.serialization):
   - data class `Task(val id: Int, val content: String, val isDone: Boolean = false)` กำหนด @Serializable
   - data class `TaskRequest(val content: String, val isDone: Boolean = false)` กำหนด @Serializable

2. Data Layer (In-Memory Repository):
   - สร้าง `object TaskRepository` ที่เก็บข้อมูลแบบ `mutableListOf<Task>()` พร้อม ID auto-increment
   - ฟังก์ชัน `getAll(): List<Task>`
   - ฟังก์ชัน `getById(id: Int): Task?`
   - ฟังก์ชัน `add(request: TaskRequest): Task` หรือ `add(task: Task): Task`
   - ฟังก์ชัน `update(id: Int, updatedTask: Task): Boolean` (หรือ return Task?)
   - ฟังก์ชัน `delete(id: Int): Boolean`

3. Routing Implementation:
   - GET `/tasks`: คืนค่า List ของ Task ทั้งหมด (Status 200 OK)
   - GET `/tasks/{id}`: รับ id จาก Path Parameter ถ้าเจอคืนค่า Task (200 OK) ถ้าไม่พบคืน 404 Not Found
   - POST `/tasks`: รับ TaskRequest/Task เข้ามา เพิ่มลง Repository แล้วตอบกลับด้วย Task ที่สร้างใหม่ (Status 201 Created)
   - PUT `/tasks/{id}`: รับ id และ Body สำหรับอัปเดต ถ้าสำเร็จตอบ 200 OK ถ้าไม่พบคืน 404 Not Found
   - DELETE `/tasks/{id}`: ลบ Task ตาม id ถ้าสำเร็จตอบกลับด้วย 204 No Content ถ้าไม่พบคืน 404 Not Found

กรุณาเขียนโค้ดภาษา Kotlin ที่สมบูรณ์ พร้อม import statements ที่จำเป็นทั้งหมด