package com.example.sibantubaru

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sibantubaru.api.ApiService
import com.example.sibantubaru.model.LoginRequest
import com.example.sibantubaru.model.LoginResponse
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var apiService: ApiService
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var signupTextView: TextView

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailEditText = findViewById(R.id.email_edit_text)
        passwordEditText = findViewById(R.id.password_edit_text)
        loginButton = findViewById(R.id.login_button)
        signupTextView = findViewById(R.id.signup_link)

        setupApiService()

        // Menangani klik untuk login
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                Log.d("LoginActivity", "Attempting to login with email: $email")
                performLogin(email, password)
            } else {
                Log.d("LoginActivity", "Email or password is empty")
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        // Navigasi ke SignupActivity
        signupTextView.setOnClickListener {
            Log.d("LoginActivity", "Navigating to SignupActivity")
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        // Menangani toggle visibilitas password
        passwordEditText.setOnTouchListener { v, event ->
            val drawableRight = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= passwordEditText.right - passwordEditText.compoundDrawables[drawableRight].bounds.width()) {
                    if (passwordEditText.transformationMethod == android.text.method.HideReturnsTransformationMethod.getInstance()) {
                        passwordEditText.transformationMethod = android.text.method.PasswordTransformationMethod.getInstance()
                        passwordEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_off, 0)
                    } else {
                        passwordEditText.transformationMethod = android.text.method.HideReturnsTransformationMethod.getInstance()
                        passwordEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_on_2, 0)
                    }
                    passwordEditText.setSelection(passwordEditText.text.length) // Menjaga kursor tetap di posisi terakhir
                    return@setOnTouchListener true
                }
            }
            false
        }
    }

    private fun setupApiService() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://si-bantu-capstone-bangkit.uc.r.appspot.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder().build())
            .build()

        apiService = retrofit.create(ApiService::class.java)
        Log.d("LoginActivity", "ApiService setup complete")
    }

    private fun performLogin(email: String, password: String) {
        val loginRequest = LoginRequest(email, password)

        Log.d("LoginActivity", "Sending login request for $email")

        apiService.login(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                Log.d("LoginActivity", "Received response: ${response.code()}")

                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        Log.d("LoginActivity", "Login successful with uid: ${loginResponse.uid}")
                        Toast.makeText(this@LoginActivity, "Login successful", Toast.LENGTH_SHORT).show()

                        // Save uid (token) dan navigasi ke home screen
                        saveToken(loginResponse.uid)
                        navigateToHome()
                    } else {
                        Log.d("LoginActivity", "Login failed: No response body")
                        Toast.makeText(this@LoginActivity, "Login failed: No response body", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.d("LoginActivity", "Login failed with error code: ${response.code()}")
                    Toast.makeText(this@LoginActivity, "Login failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.d("LoginActivity", "Login request failed: ${t.message}")
                Toast.makeText(this@LoginActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun saveToken(uid: String) {
        val sharedPreferences = getSharedPreferences("SibantuPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("USER_ID", uid) // Simpan uid sebagai token
        editor.apply()
        Log.d("LoginActivity", "Token saved successfully")
    }

    private fun navigateToHome() {
        Log.d("LoginActivity", "Navigating to MainActivity")
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
