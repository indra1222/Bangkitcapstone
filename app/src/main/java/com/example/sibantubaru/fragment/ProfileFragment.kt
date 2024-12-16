package com.example.sibantubaru.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.sibantubaru.LoginActivity
import com.example.sibantubaru.R
import com.example.sibantubaru.api.ApiService
import com.example.sibantubaru.model.ProfileResponse
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ProfileFragment(private val onLogout: () -> Unit) : Fragment() {

    private lateinit var profileImage: ImageView
    private lateinit var nameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var birthdateTextView: TextView
    private lateinit var phoneNumberTextView: TextView
    private lateinit var editButton: Button
    private lateinit var deleteButton: Button
    private lateinit var logoutButton: Button
    private lateinit var apiService: ApiService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        profileImage = view.findViewById(R.id.profile_image)
        nameTextView = view.findViewById(R.id.name)
        emailTextView = view.findViewById(R.id.email)
        birthdateTextView = view.findViewById(R.id.birthdate)
        phoneNumberTextView = view.findViewById(R.id.phone_number)
        editButton = view.findViewById(R.id.edit_button)
        deleteButton = view.findViewById(R.id.delete_button)
        logoutButton = view.findViewById(R.id.logout_button) // Tombol logout

        // Setup API service
        setupApiService()

        // Fetch profile data for the user
        val userId = getUserId()
        if (userId != null) {
            fetchProfile(userId)
        } else {
            Toast.makeText(context, "User ID not found. Please log in again.", Toast.LENGTH_SHORT).show()
        }

        // Edit profile button action
        editButton.setOnClickListener { editProfile() }

        // Delete profile button action
        deleteButton.setOnClickListener { userId?.let { deleteProfile(it) } }

        // Logout button action
        logoutButton.setOnClickListener { onLogout() }

        return view
    }

    private fun setupApiService() {
        // Initialize Retrofit with base URL and Gson converter
        val retrofit = Retrofit.Builder()
            .baseUrl("https://si-bantu-capstone-bangkit.uc.r.appspot.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder().build())
            .build()

        apiService = retrofit.create(ApiService::class.java)
    }

    private fun getUserId(): String? {
        // Get user ID from SharedPreferences
        val sharedPreferences = requireContext().getSharedPreferences("SibantuPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("USER_ID", null)
    }

    private fun fetchProfile(userId: String) {
        apiService.getProfile(userId).enqueue(object : Callback<ProfileResponse> {
            override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                if (response.isSuccessful) {
                    val profile = response.body()
                    if (profile != null) {
                        nameTextView.text = profile.name
                        emailTextView.text = profile.email
                        birthdateTextView.text = profile.birthdate
                        phoneNumberTextView.text = profile.phoneNumber
                        Glide.with(this@ProfileFragment)
                            .load(profile.photoURL)
                            .placeholder(R.drawable.default_profile)
                            .into(profileImage)
                    }
                } else {
                    Toast.makeText(context, "Failed to load profile", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun editProfile() {
        Toast.makeText(context, "Edit profile clicked", Toast.LENGTH_SHORT).show()
    }

    private fun deleteProfile(userId: String) {
        apiService.deleteProfile(userId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Profile deleted successfully", Toast.LENGTH_SHORT).show()
                    clearUserDataAndRedirect()
                } else {
                    Toast.makeText(context, "Failed to delete profile", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun clearUserDataAndRedirect() {
        val sharedPreferences = requireContext().getSharedPreferences("SibantuPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        activity?.finish()
    }
}