package com.example

import java.util.Collections
import java.util.concurrent.atomic.AtomicInteger

object TaskRepository {
    private val tasks = Collections.synchronizedList(mutableListOf<Task>())
    private val currentId = AtomicInteger(1)

    fun getAll(): List<Task> {
        synchronized(tasks) {
            return tasks.toList()
        }
    }

    fun getById(id: Int): Task? {
        synchronized(tasks) {
            return tasks.find { it.id == id }
        }
    }

    fun add(request: TaskRequest): Task {
        val task = Task(
            id = currentId.getAndIncrement(),
            content = request.content,
            isDone = request.isDone
        )
        tasks.add(task)
        return task
    }

    fun add(task: Task): Task {
        val newTask = if (task.id <= 0) {
            task.copy(id = currentId.getAndIncrement())
        } else {
            task
        }
        tasks.add(newTask)
        return newTask
    }

    fun update(id: Int, request: TaskRequest): Task? {
        synchronized(tasks) {
            val index = tasks.indexOfFirst { it.id == id }
            if (index == -1) return null
            val updated = Task(id = id, content = request.content, isDone = request.isDone)
            tasks[index] = updated
            return updated
        }
    }

    fun update(id: Int, updatedTask: Task): Boolean {
        synchronized(tasks) {
            val index = tasks.indexOfFirst { it.id == id }
            if (index == -1) return false
            tasks[index] = updatedTask.copy(id = id)
            return true
        }
    }

    fun delete(id: Int): Boolean {
        synchronized(tasks) {
            return tasks.removeIf { it.id == id }
        }
    }

    fun clear() {
        synchronized(tasks) {
            tasks.clear()
            currentId.set(1)
        }
    }
}
