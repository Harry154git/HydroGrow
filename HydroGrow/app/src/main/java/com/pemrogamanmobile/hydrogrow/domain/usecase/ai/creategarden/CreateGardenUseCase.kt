package com.pemrogamanmobile.hydrogrow.domain.usecase.ai.creategarden

import com.pemrogamanmobile.hydrogrow.domain.model.*
import com.pemrogamanmobile.hydrogrow.domain.repository.AiRepository
import com.pemrogamanmobile.hydrogrow.domain.repository.ChatBotRepository
import com.pemrogamanmobile.hydrogrow.domain.repository.GardenRepository
import com.pemrogamanmobile.hydrogrow.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID
import javax.inject.Inject

class CreateGardenUseCase @Inject constructor(
    private val chatRepository: ChatBotRepository,
    private val aiRepository: AiRepository,
    private val gardenRepository: GardenRepository
) {

    fun createNewGardenWithAi(params: GardenCreationParams): Flow<Resource<AiGardenPlan>> = flow {
        emit(Resource.Loading())
        try {
            val prompt = buildPrompt(params)
            val result = aiRepository.getAiAnalysis(prompt)

            result.fold(
                onSuccess = { aiResponse ->
                    // [DIUBAH] Memberikan parameter input awal untuk konteks parsing
                    val plan = parseAiResponse(aiResponse, params)
                    if (plan != null) {
                        emit(Resource.Success(plan))
                    } else {
                        emit(Resource.Error("Gagal memproses respons dari AI."))
                    }
                },
                onFailure = { exception ->
                    emit(Resource.Error(exception.message ?: "Terjadi kesalahan pada AI"))
                }
            )
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Terjadi kesalahan yang tidak diketahui"))
        }
    }

    fun saveGardenAndChat(
        userOwnerId: String,
        gardenName: String,
        plan: AiGardenPlan
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val newGarden = Garden(
                id = UUID.randomUUID().toString(),
                gardenName = gardenName,
                // [DIUBAH] Menggunakan hasil luas lahan dari AiGardenPlan
                gardenSize = plan.landSizeM2,
                hydroponicType = plan.hydroponicType,
                userOwnerId = userOwnerId,
                imageUrl = null
            )
            gardenRepository.insertGarden(newGarden)

            val aiMessage = ChatMessage(
                role = ChatMessage.ROLE_MODEL,
                content = plan.displayText,
                timestamp = System.currentTimeMillis()
            )

            val newChat = ChatBot(
                id = UUID.randomUUID().toString(),
                userOwnerId = userOwnerId,
                title = "Rencana untuk '$gardenName'",
                conversation = mutableListOf(aiMessage),
                relatedGardenId = newGarden.id,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            val chatSaveResult = chatRepository.addChat(newChat)

            emit(chatSaveResult)

        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Gagal menyimpan kebun dan percakapan"))
        }
    }

    private fun buildPrompt(params: GardenCreationParams): String {
        return buildString {
            append("Buatkan aku rencana kebun hidroponik yang cocok berdasarkan data penting ini:\n")
            append("- Kondisi cahaya: ${params.kondisiCahaya}\n")

            if (params.mintaRekomendasiLahan || params.panjangLahan == null || params.lebarLahan == null) {
                append("- Ukuran lahan: Tolong berikan rekomendasi ukuran lahan yang cocok.\n")
            } else {
                append("- Ukuran lahan: Panjang ${params.panjangLahan} meter dan lebar ${params.lebarLahan} meter.\n")
            }

            append("- Suhu cuaca: ${params.suhuCuaca}\n")

            if (params.mintaRekomendasiTanaman) {
                append("- Jenis tanaman: Tolong berikan rekomendasi jenis tanaman yang sesuai.\n")
            } else {
                append("- Jenis tanaman: Saya ingin menanam ${params.jenisTanaman}.\n")
            }

            append("- Tujuan/skala: ${params.tujuanSkala}\n")
            append("- Perkiraan biaya: ${params.rentangBiaya}\n\n")

            append("Tolong berikan penjelasan yang mencakup:\n")
            append("1. Tipe sistem hidroponik yang paling sesuai (misalnya NFT, DFT, Wick System, atau Dutch Bucket).\n")
            append("2. Rekomendasi tanaman spesifik (jika sebelumnya saya meminta rekomendasi).\n")
            append("3. Estimasi ukuran instalasi (jika sebelumnya saya meminta rekomendasi lahan).\n")
            append("4. Estimasi rincian biaya agar sesuai dengan rentang yang diberikan.\n\n")
            append("Setelah penjelasan di atas, buat baris baru dan tambahkan teks tersembunyi dengan format ini: ")
            // [PENTING] Menambahkan tag [estimasi ukuran] jika minta rekomendasi lahan
            val hiddenTextFormat = if (params.mintaRekomendasiLahan) {
                "<<hidden text>>[tipe hidroponik]...[tipe hidroponik] [rekomendasi tanaman]...[rekomendasi tanaman] [estimasi biaya]...[estimasi biaya] [estimasi ukuran]PANJANGxLEBAR[estimasi ukuran]<<hidden text>>"
            } else {
                "<<hidden text>>[tipe hidroponik]...[tipe hidroponik] [rekomendasi tanaman]...[rekomendasi tanaman] [estimasi biaya]...[estimasi biaya]<<hidden text>>"
            }
            append(hiddenTextFormat.replace("...", "TIPE_REKOMENDASI")) // Ganti placeholder agar lebih jelas
        }
    }

    // [DIUBAH] Parser sekarang menerima parameter input awal untuk perbandingan
    private fun parseAiResponse(response: String, originalParams: GardenCreationParams): AiGardenPlan? {
        return try {
            val hiddenText = response.substringAfter("<<hidden text>>", missingDelimiterValue = "").substringBefore("<<hidden text>>")

            val hydroponicType = hiddenText.substringAfter("[tipe hidroponik]").substringBefore("[tipe hidroponik]")
            val plantsText = hiddenText.substringAfter("[rekomendasi tanaman]").substringBefore("[rekomendasi tanaman]")
            val costText = hiddenText.substringAfter("[estimasi biaya]").substringBefore("[estimasi biaya]")

            val displayText = response.substringBefore("<<hidden text>>").trim()

            // [PENTING] Logika untuk tanaman: jika tidak minta rekomendasi, list akan kosong
            val recommendedPlants = if (originalParams.mintaRekomendasiTanaman) {
                plantsText.split(',').map { it.trim() }.filter { it.isNotEmpty() }
            } else {
                emptyList()
            }

            val estimatedCost = costText.filter { it.isDigit() }.toDoubleOrNull() ?: 0.0

            // [PENTING] Logika untuk ukuran lahan (dua skenario)
            val landSizeM2: Double
            if (originalParams.mintaRekomendasiLahan) {
                // Skenario 1: Ambil dari rekomendasi AI
                val sizeText = hiddenText.substringAfter("[estimasi ukuran]").substringBefore("[estimasi ukuran]")
                val dimensions = sizeText.split('x').mapNotNull { it.trim().toDoubleOrNull() }
                landSizeM2 = if (dimensions.size == 2) dimensions[0] * dimensions[1] else 0.0
            } else {
                // Skenario 2: Hitung dari input pengguna
                val p = originalParams.panjangLahan
                val l = originalParams.lebarLahan
                landSizeM2 = if (p != null && l != null) (p * l).toDouble() else 0.0
            }

            if (hydroponicType.isBlank()) return null

            AiGardenPlan(
                displayText = displayText,
                hydroponicType = hydroponicType,
                recommendedPlants = recommendedPlants,
                estimatedCost = estimatedCost,
                landSizeM2 = landSizeM2
            )
        } catch (e: Exception) {
            null
        }
    }
}