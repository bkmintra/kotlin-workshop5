package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class LendingRecord(
    val id: Int,
    val bookId: Int,
    val borrowerName: String,
    val checkoutDate: String,
    val returnDate: String? = null
)

@Serializable
data class CheckoutRequest(
    val bookId: Int,
    val borrowerName: String
)
