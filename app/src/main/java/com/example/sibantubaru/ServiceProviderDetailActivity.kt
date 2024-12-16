package com.example.sibantubaru

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.content.pm.PackageManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.sibantubaru.model.Order
import com.example.sibantubaru.model.OrderStatus
import com.example.sibantubaru.model.ServiceProvider
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import java.util.Date
import java.util.UUID

class ServiceProviderDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_provider_detail)

        // Get the ServiceProvider object passed from the previous activity
        val provider = intent.getParcelableExtra<ServiceProvider>("EXTRA_PROVIDER")
            ?: return // Handle null case if no provider is passed

        // Load image using Glide
        Glide.with(this)
            .load(provider.profileImageUrl)
            .placeholder(R.drawable.default_service_provider) // Placeholder image
            .into(findViewById<ImageView>(R.id.img_provider_detail))

        // Bind data to views
        findViewById<TextView>(R.id.tv_provider_name).text = provider.name
        findViewById<TextView>(R.id.tv_provider_type).text = provider.serviceType
        findViewById<TextView>(R.id.tv_provider_address).text = provider.address
        findViewById<TextView>(R.id.tv_provider_bio).text = provider.deskripsi

        // Rating bar and text for provider rating
        val ratingBar = findViewById<RatingBar>(R.id.rb_provider_rating)
        val ratingText = findViewById<TextView>(R.id.rb_provider_rating_text)

        ratingBar.rating = provider.rating
        ratingText.text = "(${provider.rating})"

        // Back button logic
        findViewById<MaterialButton>(R.id.btn_back).setOnClickListener {
            navigateToMainActivity()
        }

        // WhatsApp button logic
        findViewById<Button>(R.id.btn_contact_whatsapp).setOnClickListener {
            // Create a new order object
            val order = Order(
                id = UUID.randomUUID().toString(),
                serviceProviderId = provider.id,
                serviceProviderName = provider.name,
                serviceType = provider.serviceType,
                whatsappNumber = provider.whatsappNumber,
                rating = provider.rating,
                status = OrderStatus.ONGOING,
                createdAt = Date()
            )

            // Save the order to SharedPreferences
            val sharedPreferences = getSharedPreferences("OrderData", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            val orderJson = Gson().toJson(order) // Convert the order to JSON
            editor.putString("ongoing_order", orderJson)
            editor.apply()

            // Check if WhatsApp is installed and open it
            if (isWhatsappInstalled()) {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://wa.me/${provider.whatsappNumber}")
                }
                startActivity(intent)
            } else {
                // If WhatsApp is not installed, open WhatsApp Web
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://wa.me/${provider.whatsappNumber}")
                }
                startActivity(intent)
            }
        }
    }

    // Check if WhatsApp is installed on the device
    private fun isWhatsappInstalled(): Boolean {
        val pm = packageManager
        return try {
            pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    // Navigate to the MainActivity when back button is pressed
    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java).apply {
            // Clear the back stack and create a new task
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
        finish()
    }

    // Optional: Override the default back button behavior
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        navigateToMainActivity()
    }
}
