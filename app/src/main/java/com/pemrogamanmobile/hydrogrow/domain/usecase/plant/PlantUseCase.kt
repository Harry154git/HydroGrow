package com.pemrogamanmobile.hydrogrow.domain.usecase.plant

import android.content.Context
import android.net.Uri
import com.pemrogamanmobile.hydrogrow.domain.model.Game
import com.pemrogamanmobile.hydrogrow.domain.model.Plant
import com.pemrogamanmobile.hydrogrow.domain.repository.GameRepository
import com.pemrogamanmobile.hydrogrow.domain.repository.PlantRepository
import com.pemrogamanmobile.hydrogrow.util.NotificationHelper // Import helper kita
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class PlantUseCase @Inject constructor(
    private val plantRepository: PlantRepository,
    private val gameRepository: GameRepository,
    @ApplicationContext private val context: Context // Suntikkan Context
) {
    // ... (fungsi upload, insert, update, delete tetap sama) ...
    suspend fun uploadPlantImage(uri: Uri): String {
        return plantRepository.uploadPlantImage(uri)
    }

    suspend fun insertPlant(plant: Plant) {
        plantRepository.insertPlant(plant)
    }

    suspend fun updatePlant(plant: Plant) {
        plantRepository.updatePlant(plant)
    }

    suspend fun deletePlant(plant: Plant, gardenId: String) {
        plantRepository.deletePlant(plant, gardenId)
    }

    /**
     * Fungsi ini hanya untuk memeriksa dan mengirim notifikasi.
     * Akan dipanggil oleh Worker di background.
     */
    suspend fun checkAndNotifyPlant(plant: Plant) {
        val currentTime = System.currentTimeMillis()
        val harvestDurationInMillis = TimeUnit.DAYS.toMillis(plant.harvestTime.toLong())
        val harvestTime = plant.plantingTime + harvestDurationInMillis
        val remainingTime = harvestTime - currentTime
        val remainingDays = TimeUnit.MILLISECONDS.toDays(remainingTime)

        var notificationMessage: String? = null
        when (remainingDays) {
            14L -> notificationMessage = "Panen ${plant.plantName} dalam 2 minggu!"
            7L -> notificationMessage = "Panen ${plant.plantName} dalam 1 minggu!"
            3L -> notificationMessage = "Panen ${plant.plantName} 3 hari lagi!"
            1L -> notificationMessage = "Besok panen ${plant.plantName}!"
            0L -> notificationMessage = "${plant.plantName} siap dipanen! Klik untuk konfirmasi."
        }

        // Kirim notifikasi jika ada pesan
        notificationMessage?.let { message ->
            sendNotification(
                plantId = plant.id,
                plantName = plant.plantName,
                imageUrl = plant.imageUrl,
                message = message
            )
        }

        // Logika penghapusan otomatis setelah 5 hari tanpa konfirmasi
        val fiveDaysInMillis = TimeUnit.DAYS.toMillis(5)
        if (currentTime - harvestTime >= fiveDaysInMillis) {
            handleUnconfirmedHarvest(plant, plant.gardenOwnerId)
        }
    }

    suspend fun confirmHarvest(plant: Plant, gardenId: String) {
        handleConfirmedHarvest(plant, gardenId)
    }

    private suspend fun handleConfirmedHarvest(plant: Plant, gardenId: String) {
        deletePlant(plant, gardenId)
        val currentGame = gameRepository.getGame().firstOrNull()
        val message = "Panen berhasil! Kamu mendapatkan ${plant.cupAmount} cup."

        if (currentGame != null) {
            val updatedGame = currentGame.copy(cup = currentGame.cup + plant.cupAmount)
            gameRepository.createOrUpdateGame(updatedGame)
        } else {
            val newGame = Game(id = plant.gardenOwnerId, userOwnerId = plant.gardenOwnerId, cup = plant.cupAmount)
            gameRepository.createOrUpdateGame(newGame)
        }
        // Kirim notifikasi keberhasilan panen
        sendNotification(
            plantId = plant.id,
            plantName = plant.plantName,
            imageUrl = null, // Tidak perlu gambar untuk notif ini
            message = message
        )
    }

    private suspend fun handleUnconfirmedHarvest(plant: Plant, gardenId: String) {
        deletePlant(plant, gardenId)
        val message = "Tanaman ${plant.plantName} telah dihapus karena panen tidak dikonfirmasi."
        // Kirim notifikasi penghapusan
        sendNotification(
            plantId = plant.id,
            plantName = plant.plantName,
            imageUrl = null, // Tidak perlu gambar
            message = message
        )
    }

    /**
     * Implementasi baru untuk mengirim notifikasi menggunakan NotificationHelper.
     */
    private suspend fun sendNotification(plantId: String, plantName: String, imageUrl: String?, message: String) {
        // Gunakan hashCode dari plantId sebagai ID notifikasi yang unik
        val notificationId = plantId.hashCode()
        NotificationHelper.sendHarvestNotification(context, plantId, plantName, imageUrl, message, notificationId)
    }

    // ... (fungsi getPlantsByGarden, getPlantById, getAllPlants tetap sama) ...
    fun getPlantsByGarden(gardenId: String): Flow<List<Plant>> {
        return plantRepository.getPlantsByGarden(gardenId)
    }

    fun getPlantById(plantId: String): Flow<Plant?> = plantRepository.getPlantById(plantId)

    suspend fun getAllPlants(): List<Plant> {
        return plantRepository.getAllPlants()
    }
}