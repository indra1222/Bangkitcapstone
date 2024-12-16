package com.example.sibantubaru

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.FirebaseApp

class SplashActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Inisialisasi SharedPreferences
        sharedPreferences = getSharedPreferences("SibantuPrefs", MODE_PRIVATE)

        // Tunggu beberapa detik lalu periksa login
        Handler(Looper.getMainLooper()).postDelayed({
            if (isUserLoggedIn()) {
                // Jika sudah login, navigasi ke MainActivity
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                // Jika belum login, navigasi ke LoginActivity
                startActivity(Intent(this, OnboardingActivity::class.java))
            }
            finish() // Tutup SplashScreenActivity
        }, 2000) // Durasi splash screen (2 detik)
    }

    // Periksa apakah pengguna sudah login
    private fun isUserLoggedIn(): Boolean {
        val token = sharedPreferences.getString("USER_ID", null)
        return !token.isNullOrEmpty()
    }
}