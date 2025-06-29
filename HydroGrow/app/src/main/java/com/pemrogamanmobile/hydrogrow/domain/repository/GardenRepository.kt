package com.pemrogamanmobile.hydrogrow.domain.repository

import android.net.Uri
import com.pemrogamanmobile.hydrogrow.domain.model.Garden
import kotlinx.coroutines.flow.Flow

interface GardenRepository {
    suspend fun insertGarden(garden: Garden)
    suspend fun uploadGardenImage(uri: Uri): String
    suspend fun updateGarden(garden: Garden)
    suspend fun deleteGarden(garden: Garden)
    fun getAllGardens(): Flow<List<Garden>>
    fun getGardensByUserId(userId: String): Flow<List<Garden>>
    suspend fun getGardenById(gardenId: String): Garden?
}