package com.pemrogamanmobile.hydrogrow.data.remote.dto
// file: data/remote/dto/PlantIdentificationDto.kt
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlantIdentificationResponse(
    @SerialName("results")
    val results: List<ResultDto>
)

@Serializable
data class ResultDto(
    @SerialName("score")
    val score: Double,
    @SerialName("species")
    val species: SpeciesDto
)

@Serializable
data class SpeciesDto(
    @SerialName("scientificNameWithoutAuthor")
    val scientificName: String,
    @SerialName("commonNames")
    val commonNames: List<String>
)