package com.pemrogamanmobile.hydrogrow.domain.usecase.game

import com.pemrogamanmobile.hydrogrow.domain.model.Game
import com.pemrogamanmobile.hydrogrow.domain.repository.GameRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case untuk mendapatkan data game milik user.
 */
class GetGameUseCase @Inject constructor(
    private val gameRepository: GameRepository
) {
    operator fun invoke(): Flow<Game?> = gameRepository.getGame()
}