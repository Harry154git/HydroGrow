package com.pemrogamanmobile.hydrogrow.data.remote.service.plantnet

import com.pemrogamanmobile.hydrogrow.data.remote.dto.PlantIdentificationResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface PlantNetApiService {
    @Multipart
    @POST("v2/identify/all")
    suspend fun identifyPlant(
        @Part images: MultipartBody.Part,
        @Part("organs") organs: RequestBody,
        @Query("api-key") apiKey: String
    ): PlantIdentificationResponse
}