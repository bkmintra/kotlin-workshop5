package com.example

import io.ktor.server.engine.*
import io.ktor.server.application.*

import org.jetbrains.exposed.sql.Database
import com.example.repositories.LibraryRepository

fun main(args: Array<String>) {
    Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")
    LibraryRepository.initDb()

    io.ktor.server.netty.EngineMain.main(args)
}
