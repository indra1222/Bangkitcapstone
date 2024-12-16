package com.example.sibantubaru.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Order(
    val id: String,
    val serviceProviderId: String,
    val serviceProviderName: String,
    val serviceType: String,
    val whatsappNumber: String,
    val rating: Float,
    val status: OrderStatus,
    val createdAt: Date = Date(),
    val completedAt: Date? = null
) : Parcelable

enum class OrderStatus {
    ONGOING,
    COMPLETED,
    CANCELLED
}