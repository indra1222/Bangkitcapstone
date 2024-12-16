package com.example.sibantubaru.model

data class ProfileResponse(
    val uid: String,        // ID pengguna
    val email: String,      // Email pengguna
    val name: String,       // Nama pengguna
    val photoURL: String,   // URL gambar profil pengguna
    val phoneNumber: String, // Nomor telepon pengguna
    val birthdate: String,  // Tanggal lahir pengguna
    val createdAt: String   // Waktu pembuatan akun
)
