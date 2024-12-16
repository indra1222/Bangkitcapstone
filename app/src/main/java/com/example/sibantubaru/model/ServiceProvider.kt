package com.example.sibantubaru.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ServiceProvider(
    val id: String,
    val name: String,
    val serviceType: String,
    val address: String,
    val rating: Float,
    val latitude: Double,
    val longitude: Double,
    val profileImageUrl: String,
    val whatsappNumber: String,
    val deskripsi: String // New property
) : Parcelable