package com.example.sibantubaru

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.sibantubaru.fragment.HomeFragment
import com.example.sibantubaru.fragment.OrderFragment
import com.example.sibantubaru.fragment.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inisialisasi SharedPreferences
        sharedPreferences = getSharedPreferences("SibantuPrefs", MODE_PRIVATE)

        // Inisialisasi fragment pertama kali
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()
        }

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_nav)

        // Set listener untuk navigasi
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.nav_order -> {
                    replaceFragment(OrderFragment())
                    true
                }
                R.id.nav_profile -> {
                    replaceFragment(ProfileFragment(::handleLogout)) // Tambahkan fungsi logout di ProfileFragment
                    true
                }
                else -> false
            }
        }
    }

    // Fungsi untuk mengganti fragment
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    // Tangani logout dari ProfileFragment
    private fun handleLogout() {
        // Hapus semua data di SharedPreferences
        sharedPreferences.edit().clear().apply()

        // Navigasi kembali ke LoginActivity setelah logout
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
