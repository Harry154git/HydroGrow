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

    /**
     * [DIUBAH] Fungsi ini sekarang menerima parameter OnboardingPreferences
     * untuk personalisasi prompt AI.
     */
    fun createNewGardenWithAi(
        params: GardenCreationParams,
        userPreferences: OnboardingPreferences // Tambahan parameter
    ): Flow<Resource<AiGardenPlan>> = flow {
        emit(Resource.Loading())
        try {
            // [DIUBAH] Meneruskan preferensi pengguna untuk membangun prompt
            val prompt = buildPrompt(params, userPreferences)
            val result = aiRepository.getAiAnalysis(prompt)

            result.fold(
                onSuccess = { aiResponse ->
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

    /**
     * [DIUBAH] Prompt sekarang menyertakan data pengguna dari onboarding
     * untuk memberikan konteks tambahan pada AI.
     */
    private fun buildPrompt(
        params: GardenCreationParams,
        userPreferences: OnboardingPreferences // Tambahan parameter
    ): String {
        return buildString {
            append("Buatkan aku rencana kebun hidroponik yang cocok berdasarkan data penting ini:\n\n")

            // [BARU] Menambahkan data pengguna dari onboarding untuk personalisasi
            append("Data Pengguna:\n")
            append("- Pengalaman Berkebun: ${userPreferences.experience}\n")
            append("- Waktu Luang Tersedia: ${userPreferences.timeAvailable}\n")
            append("- Waktu Perawatan Pilihan: ${userPreferences.preferredTime}\n\n")

            append("Detail Permintaan Kebun:\n")
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
            // [DIUBAH] Meminta AI mempertimbangkan pengalaman pengguna
            append("1. Tipe sistem hidroponik yang paling sesuai (misalnya NFT, DFT, Wick System, atau Dutch Bucket), pertimbangkan juga tingkat pengalaman pengguna.\n")
            append("2. Rekomendasi tanaman spesifik (jika sebelumnya saya meminta rekomendasi).\n")
            append("3. Estimasi ukuran instalasi (jika sebelumnya saya meminta rekomendasi lahan).\n")
            append("4. Estimasi rincian biaya agar sesuai dengan rentang yang diberikan.\n\n")

            append("Setelah penjelasan di atas, buat baris baru dan tambahkan teks tersembunyi dengan format ini: ")
            val hiddenTextFormat = if (params.mintaRekomendasiLahan) {
                "<<hidden text>>[tipe hidroponik]...[tipe hidroponik] [rekomendasi tanaman]...[rekomendasi tanaman] [estimasi biaya]...[estimasi biaya] [estimasi ukuran]PANJANGxLEBAR[estimasi ukuran]<<hidden text>>"
            } else {
                "<<hidden text>>[tipe hidroponik]...[tipe hidroponik] [rekomendasi tanaman]...[rekomendasi tanaman] [estimasi biaya]...[estimasi biaya]<<hidden text>>"
            }
            append(hiddenTextFormat.replace("...", "TIPE_REKOMENDASI"))
        }
    }

    private fun parseAiResponse(response: String, originalParams: GardenCreationParams): AiGardenPlan? {
        return try {
            val hiddenText = response.substringAfter("<<hidden text>>", missingDelimiterValue = "").substringBefore("<<hidden text>>")

            val hydroponicType = hiddenText.substringAfter("[tipe hidroponik]").substringBefore("[tipe hidroponik]")
            val plantsText = hiddenText.substringAfter("[rekomendasi tanaman]").substringBefore("[rekomendasi tanaman]")
            val costText = hiddenText.substringAfter("[estimasi biaya]").substringBefore("[estimasi biaya]")

            val displayText = response.substringBefore("<<hidden text>>").trim()

            val recommendedPlants = if (originalParams.mintaRekomendasiTanaman) {
                plantsText.split(',').map { it.trim() }.filter { it.isNotEmpty() }
            } else {
                emptyList()
            }

            val estimatedCost = costText.filter { it.isDigit() }.toDoubleOrNull() ?: 0.0

            val landSizeM2: Double
            if (originalParams.mintaRekomendasiLahan) {
                val sizeText = hiddenText.substringAfter("[estimasi ukuran]").substringBefore("[estimasi ukuran]")
                val dimensions = sizeText.split('x').mapNotNull { it.trim().toDoubleOrNull() }
                landSizeM2 = if (dimensions.size == 2) dimensions[0] * dimensions[1] else 0.0
            } else {
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