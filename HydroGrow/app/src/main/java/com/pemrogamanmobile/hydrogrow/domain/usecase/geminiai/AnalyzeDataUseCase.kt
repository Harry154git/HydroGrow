package com.pemrogamanmobile.hydrogrow.domain.usecase.geminiai

import com.pemrogamanmobile.hydrogrow.domain.repository.GeminiRepository
import javax.inject.Inject

class AnalyzeDataUseCase @Inject constructor(private val repository: GeminiRepository) {
    suspend operator fun invoke(input: String): String {
        return repository.analyze(input)
    }
}