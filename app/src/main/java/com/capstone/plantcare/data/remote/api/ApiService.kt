package com.capstone.plantcare.data.remote.api

import com.capstone.plantcare.data.remote.response.HistoryResponseItem
import com.capstone.plantcare.data.remote.response.NewsResponse
import com.capstone.plantcare.data.remote.response.UploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {
    @Multipart
    @POST("predict/")
    fun predictPlant(
        @Part file: MultipartBody.Part,
        @Part("plant_type") plantType: RequestBody,
        @Part("user_id") userId: RequestBody
    ): Call<UploadResponse>

    @GET("scanned_data/{user_id}/")
    fun getData(
        @Path("user_id") user_id : String
    ) :  Call<List<HistoryResponseItem>>

    @GET("news/")
    fun getNews(): Call<NewsResponse>
}