package com.pemrogamanmobile.hydrogrow.data.repository

import android.net.Uri
import com.pemrogamanmobile.hydrogrow.data.local.room.dao.GardenDao
import com.pemrogamanmobile.hydrogrow.data.local.mapper.toDomain
import com.pemrogamanmobile.hydrogrow.data.local.mapper.toEntity
import com.pemrogamanmobile.hydrogrow.data.remote.mapper.toDomain
import com.pemrogamanmobile.hydrogrow.data.remote.mapper.toDto
import com.pemrogamanmobile.hydrogrow.data.remote.service.firestore.AuthService
import com.pemrogamanmobile.hydrogrow.data.remote.service.firestore.ImageUploader
import com.pemrogamanmobile.hydrogrow.data.remote.service.firestore.firestoreservices.GardenService
import com.pemrogamanmobile.hydrogrow.domain.model.Garden
import com.pemrogamanmobile.hydrogrow.domain.repository.GardenRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.emitAll


class GardenRepositoryImpl @Inject constructor(
    private val dao: GardenDao,
    private val firestore: GardenService,
    private val authService: AuthService,
    private val imageUploader: ImageUploader
) : GardenRepository {

    private fun getUserId(): String {
        return authService.getCurrentUser()?.uid.orEmpty()
    }

    override suspend fun insertGarden(garden: Garden) {
        val entity = garden.toEntity()
        dao.insertGarden(entity)
        firestore.uploadGarden(getUserId(), garden.toDto())
    }

    override suspend fun uploadGardenImage(uri: Uri): String {
        return imageUploader.uploadImageToStorage(uri)
    }

    override suspend fun updateGarden(garden: Garden) {
        val entity = garden.toEntity()
        dao.updateGarden(entity)
        firestore.uploadGarden(getUserId(), garden.toDto())
    }

    override suspend fun deleteGarden(garden: Garden) {
        dao.deleteGarden(garden.toEntity())
        firestore.deleteGarden(getUserId(), garden.id)
    }

    override fun getAllGardens(): Flow<List<Garden>> = flow {
        val localData = dao.getAllGardens().firstOrNull()?.map { it.toDomain() } ?: emptyList()

        if (localData.isNotEmpty()) {
            emit(localData)
        } else {
            // Fetch dari Firestore jika lokal kosong
            val remoteData = firestore.getGardens(getUserId()).map { it.toDomain() }
            // Simpan ke lokal
            remoteData.forEach { dao.insertGarden(it.toEntity()) }
            emit(remoteData)
        }

        // Listen update Room
        emitAll(dao.getAllGardens().map { it.map { e -> e.toDomain() } })
    }

    override fun getGardensByUserId(userId: String): Flow<List<Garden>> = flow {
        val localData = dao.getGardensByUserId(userId).firstOrNull()?.map { it.toDomain() } ?: emptyList()

        if (localData.isNotEmpty()) {
            emit(localData)
        } else {
            val remoteData = firestore.getGardens(userId).map { it.toDomain() }
            remoteData.forEach { dao.insertGarden(it.toEntity()) }
            emit(remoteData)
        }

        emitAll(dao.getGardensByUserId(userId).map { it.map { e -> e.toDomain() } })
    }

    override suspend fun getGardenById(gardenId: String): Garden? {
        var entity = dao.getGardenById(gardenId)
        if (entity != null) {
            return entity.toDomain()
        }

        val remoteGarden = firestore.getGardenById(getUserId(), gardenId)
        remoteGarden?.let {
            dao.insertGarden(it.toDomain().toEntity())
            return it.toDomain()
        }
        return null
    }
}

