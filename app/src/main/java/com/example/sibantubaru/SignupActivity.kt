package com.example.sibantubaru

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.app.Activity
import android.graphics.Color
import android.net.Uri
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import com.example.sibantubaru.api.ApiClient
import com.example.sibantubaru.model.SignupResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class SignupActivity : AppCompatActivity() {

    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var name: EditText
    private lateinit var birthdate: EditText
    private lateinit var phoneNumber: EditText
    private var profileImageUri: Uri? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        confirmPassword = findViewById(R.id.confirm_password)
        name = findViewById(R.id.name)
        phoneNumber = findViewById(R.id.phone_number)


        val signup = findViewById<Button>(R.id.signup)
        signup.setOnClickListener {
                uploadData()
        }

        val loginRedirect = findViewById<TextView>(R.id.login_redirect)
        loginRedirect.setOnClickListener {
            val intent = Intent(this@SignupActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        val backButton = findViewById<ImageView>(R.id.back_button)
        backButton.setOnClickListener {
            val intent = Intent(this@SignupActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        val termsConditionTextView: TextView = findViewById(R.id.terms_condition)
        val text = "By signing up, you agree to our Terms of Service and Privacy Policy"

// Create a SpannableString
        val spannableString = SpannableString(text)

// Make "Terms of Service" clickable
        val termsStart = text.indexOf("Terms of Service")
        val termsEnd = termsStart + "Terms of Service".length
        spannableString.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                // Navigate to Terms of Service
                Toast.makeText(widget.context, "Terms of Service clicked", Toast.LENGTH_SHORT).show()
            }
        }, termsStart, termsEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

// Make "Privacy Policy" clickable
        val privacyStart = text.indexOf("Privacy Policy")
        val privacyEnd = privacyStart + "Privacy Policy".length
        spannableString.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                // Navigate to Privacy Policy
                Toast.makeText(widget.context, "Privacy Policy clicked", Toast.LENGTH_SHORT).show()
            }
        }, privacyStart, privacyEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

// Set color for links
        spannableString.setSpan(
            ForegroundColorSpan(Color.BLUE), termsStart, termsEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            ForegroundColorSpan(Color.BLUE), privacyStart, privacyEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

// Enable links and set text
        termsConditionTextView.text = spannableString
        termsConditionTextView.movementMethod = LinkMovementMethod.getInstance()


        password.setOnTouchListener { v, event ->
            val drawableRight = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= password.right - password.compoundDrawables[drawableRight].bounds.width()) {
                    if (password.transformationMethod == android.text.method.HideReturnsTransformationMethod.getInstance()) {
                        password.transformationMethod = android.text.method.PasswordTransformationMethod.getInstance()
                        password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_off, 0)
                    } else {
                        password.transformationMethod = android.text.method.HideReturnsTransformationMethod.getInstance()
                        password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_on_2, 0)
                    }
                    password.setSelection(password.text.length) // Menjaga kursor tetap di posisi terakhir
                    return@setOnTouchListener true
                }
            }
            false
        }
        confirmPassword.setOnTouchListener { v, event ->
            val drawableRight = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= confirmPassword.right - confirmPassword.compoundDrawables[drawableRight].bounds.width()) {
                    if (confirmPassword.transformationMethod == android.text.method.HideReturnsTransformationMethod.getInstance()) {
                        confirmPassword.transformationMethod = android.text.method.PasswordTransformationMethod.getInstance()
                        confirmPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_off, 0)
                    } else {
                        confirmPassword.transformationMethod = android.text.method.HideReturnsTransformationMethod.getInstance()
                        confirmPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_on_2, 0)
                    }
                    confirmPassword.setSelection(confirmPassword.text.length) // Menjaga kursor tetap di posisi terakhir
                    return@setOnTouchListener true
                }
            }
            false
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            profileImageUri = data?.data
            if (profileImageUri == null) {
                Log.e("SignupActivity", "Failed to retrieve image URI")
                Toast.makeText(this, "Error retrieving image", Toast.LENGTH_SHORT).show()
            }
        } else {
            Log.e("SignupActivity", "Image selection failed or cancelled")
        }
    }

    private fun uploadData() {
        try {
            if (password.text.toString() != confirmPassword.text.toString()) {
                Toast.makeText(
                    this,
                    "Password and Confirm Password do not match",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

//            val file = createTempFileFromUri(profileImageUri ?: throw IllegalArgumentException("Invalid URI"))
//            val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
//            val profileImage = MultipartBody.Part.createFormData("profileImage", file.name, requestFile)

            val emailBody = RequestBody.create("text/plain".toMediaTypeOrNull(), email.text.toString())
            val passwordBody = RequestBody.create("text/plain".toMediaTypeOrNull(), password.text.toString())
            val nameBody = RequestBody.create("text/plain".toMediaTypeOrNull(), name.text.toString())
            val birthdateBody = RequestBody.create("text/plain".toMediaTypeOrNull(), "2004-02-09")
            val phoneNumberBody = RequestBody.create("text/plain".toMediaTypeOrNull(), phoneNumber.text.toString())

            ApiClient.instance.signup(
                emailBody, passwordBody, nameBody, birthdateBody, phoneNumberBody, null
            ).enqueue(object : Callback<SignupResponse> {
                override fun onResponse(call: Call<SignupResponse>, response: Response<SignupResponse>) {
                    if (response.isSuccessful) {
                        val signupResponse = response.body()
                        if (signupResponse != null) {
                            Toast.makeText(this@SignupActivity, signupResponse.message, Toast.LENGTH_SHORT).show()
                            // Navigasi ke LoginActivity setelah sukses signup
                            val intent = Intent(this@SignupActivity, LoginActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish() // Optional, untuk menutup SignupActivity agar tidak kembali ke halaman signup
                        } else {
                            Toast.makeText(this@SignupActivity, "Signup failed: No response body", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Log.e("SignupActivity", "Signup failed: ${response.errorBody()?.string()}")
                        Toast.makeText(this@SignupActivity, "Signup failed", Toast.LENGTH_SHORT).show()
                    }
                }


                override fun onFailure(call: Call<SignupResponse>, t: Throwable) {
                    Log.e("SignupActivity", "Signup error: ${t.message}", t)
                    Toast.makeText(this@SignupActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } catch (e: Exception) {
            Log.e("SignupActivity", "Error preparing image upload: ${e.message}", e)
            Toast.makeText(this, "Error preparing image upload", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createTempFileFromUri(uri: Uri): File {
        val inputStream = contentResolver.openInputStream(uri)
            ?: throw IllegalArgumentException("Unable to open InputStream from URI")
        val tempFile = File.createTempFile("profile_image", ".jpg", cacheDir)
        tempFile.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }
        return tempFile
    }
}
