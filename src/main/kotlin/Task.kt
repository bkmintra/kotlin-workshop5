package com.example

import kotlinx.serialization.Serializable

@Serializable
data class Task(
    val id: Int,
    val content: String,
    val isDone: Boolean = false
)

@Serializable
data class TaskRequest(
    val content: String,
    val isDone: Boolean = false
)
