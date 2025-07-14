package com.pemrogamanmobile.hydrogrow.data.remote.mapper

import com.pemrogamanmobile.hydrogrow.data.remote.dto.GameDto
import com.pemrogamanmobile.hydrogrow.domain.model.Game

/**
 * Converts a GameDto (from Firestore) to a Game (domain model).
 */
fun GameDto.toDomain(): Game = Game(
    id = this.id,
    userOwnerId = this.userOwnerId,
    cup = this.cup
)

/**
 * Converts a Game (domain model) to a GameDto (for Firestore).
 */
fun Game.toDto(): GameDto = GameDto(
    id = this.id,
    userOwnerId = this.userOwnerId,
    cup = this.cup
)

/**
 * Converts a list of GameDto objects to a list of Game domain models.
 */
fun List<GameDto>.toDomainList(): List<Game> = map { it.toDomain() }

/**
 * Converts a list of Game domain models to a list of GameDto objects.
 */
fun List<Game>.toDtoList(): List<GameDto> = map { it.toDto() }