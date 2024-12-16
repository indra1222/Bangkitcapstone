package com.example.sibantubaru.api

import com.example.sibantubaru.model.LoginRequest
import com.example.sibantubaru.model.LoginResponse
import com.example.sibantubaru.model.ProfileResponse
import com.example.sibantubaru.model.SignupResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @Multipart
    @POST("signup")
    fun signup(
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody,
        @Part("name") name: RequestBody,
        @Part("birthdate") birthdate: RequestBody,
        @Part("phoneNumber") phoneNumber: RequestBody,
        @Part profileImage: MultipartBody.Part?
    ): Call<SignupResponse>

    @POST("login")
    fun login(
        @Body loginRequest: LoginRequest
    ): Call<LoginResponse>


    @GET("profile/{uid}")
    fun getProfile(@Path("uid") userId: String): Call<ProfileResponse>

    @Multipart
    @PUT("profile/{uid}")
    fun updateProfile(
        @Path("uid") userId: String,
        @Part("name") name: RequestBody,
        @Part("email") email: RequestBody,
        @Part("birthdate") birthdate: RequestBody,
        @Part("phonenumber") phonenumber: RequestBody,
        @Part profileImage: MultipartBody.Part?
    ): Call<Void>

    @DELETE("profile/{uid}")
    fun deleteProfile(@Path("uid") userId: String): Call<Void>
}
