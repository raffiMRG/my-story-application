package com.example.mystoryapplication.data.api

import com.example.mystoryapplication.data.request.LoginRequest
import com.example.mystoryapplication.data.request.RegisterRequest
import com.example.mystoryapplication.data.response.LoginResponse
import com.example.mystoryapplication.data.response.BasicResponse
import com.example.mystoryapplication.data.response.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {


    @GET("/v1/stories")
    fun getAllStories(): Call<StoryResponse>

    @GET("/v1/stories")
    suspend fun getPagingStories(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): StoryResponse

    @GET("/v1/stories")
    fun getAllStoriesAndLocation(
        @Query("location") location: String
    ): Call<StoryResponse>

    @Multipart
    @POST("/v1/stories")
    suspend fun uploadImage(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Float?,
        @Part("lon") lon: Float?,
    ): BasicResponse

    @POST("/v1/register")
    fun createUser(
        @Body request: RegisterRequest
    ): Call<BasicResponse>

    @POST("/v1/login")
    fun userLogin(
        @Body request: LoginRequest
    ): Call<LoginResponse>
}