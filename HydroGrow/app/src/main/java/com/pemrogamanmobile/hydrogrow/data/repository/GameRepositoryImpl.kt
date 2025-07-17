package com.pemrogamanmobile.hydrogrow.data.repository

import com.pemrogamanmobile.hydrogrow.data.local.room.dao.GameDao
import com.pemrogamanmobile.hydrogrow.data.local.mapper.toDomain
import com.pemrogamanmobile.hydrogrow.data.local.mapper.toEntity
import com.pemrogamanmobile.hydrogrow.data.remote.mapper.toDomain
import com.pemrogamanmobile.hydrogrow.data.remote.mapper.toDto
import com.pemrogamanmobile.hydrogrow.data.remote.service.firestore.AuthService
import com.pemrogamanmobile.hydrogrow.data.remote.service.firestore.firestoreservices.GameService
import com.pemrogamanmobile.hydrogrow.domain.model.Game
import com.pemrogamanmobile.hydrogrow.domain.repository.GameRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlinx.coroutines.flow.emitAll

class GameRepositoryImpl @Inject constructor(
    private val dao: GameDao,
    private val firestore: GameService,
    private val authService: AuthService
) : GameRepository {

    private fun getUserId(): String {
        return authService.getCurrentUser()?.uid.orEmpty()
    }

    // Menggabungkan insert dan update menjadi satu fungsi untuk menyederhanakan
    override suspend fun createOrUpdateGame(game: Game) {
        val userId = getUserId()
        // Kirim ke Firestore
        firestore.createOrUpdateGame(userId, game.toDto())
        // Simpan/update ke database lokal
        dao.insertGame(game.toEntity())
    }

    override suspend fun deleteGame(game: Game) {
        // Hapus dari Firestore
        firestore.deleteGame(getUserId(), game.id)
        // Hapus dari database lokal
        dao.deleteGame(game.toEntity())
    }

    // Mengubah nama dan tipe data yang dikembalikan menjadi satu objek Game, bukan List
    override fun getGame(): Flow<Game?> {
        val userId = getUserId()
        return flow {
            if (userId.isNotEmpty()) {
                try {
                    // 1. Ambil data game (tunggal) dari Firestore
                    val remoteGameDto = firestore.getGame(userId)

                    // 2. Hapus semua data game lama untuk user ini di lokal
                    //    untuk memastikan konsistensi.
                    dao.deleteGameForUser(userId)

                    // 3. Jika ada data di remote, simpan ke lokal
                    if (remoteGameDto != null) {
                        dao.insertGame(remoteGameDto.toDomain().toEntity())
                    }
                } catch (e: Exception) {
                    // Jika gagal (misal: tidak ada internet), flow akan lanjut
                    // mengandalkan data yang sudah ada di cache lokal.
                    // Anda bisa menambahkan logging di sini.
                    // e.g., Log.e("GameRepository", "Failed to sync game data: ${e.message}")
                }
            }

            // 4. Emit data game (tunggal) dari Room.
            //    Ini adalah "Single Source of Truth" untuk UI.
            emitAll(dao.getGameForUser(userId).map { it?.toDomain() })
        }
    }
}