package com.pemrogamanmobile.hydrogrow.data.local.mapper

import com.pemrogamanmobile.hydrogrow.data.local.room.entity.GameEntity
import com.pemrogamanmobile.hydrogrow.domain.model.Game

/**
 * Mengubah GameEntity (dari database) menjadi Game (model domain).
 */
fun GameEntity.toDomain(): Game = Game(
    id = this.id,
    userOwnerId = this.userOwnerId,
    cup = this.cup
)

/**
 * Mengubah Game (model domain) menjadi GameEntity (untuk database).
 */
fun Game.toEntity(): GameEntity = GameEntity(
    id = this.id,
    userOwnerId = this.userOwnerId,
    cup = this.cup
)

/**
 * Mengubah daftar objek GameEntity menjadi daftar model domain Game.
 */
fun List<GameEntity>.toDomainList(): List<Game> = map { it.toDomain() }

/**
 * Mengubah daftar model domain Game menjadi daftar objek GameEntity.
 */
fun List<Game>.toEntityList(): List<GameEntity> = map { it.toEntity() }