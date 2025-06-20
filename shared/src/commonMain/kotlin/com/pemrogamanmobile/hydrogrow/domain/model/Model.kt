package com.pemrogamanmobile.hydrogrow.data

import android.annotation.SuppressLint
import android.content.Context
import com.pemrogamanmobile.hydrogrow.R
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList


data class User(
    val email: String,
    val password: String,
    val name: String,
    val phoneNumber: String = "Belum diisi",
    val address: String = "Belum diisi",
    val idgarden: Int
)

data class HydroponicGarden(
    val idgarden: Int,
    val gardenName: String,
    val gardenSize: Double,
    val plants: List<Plant>,
    val hydroponicType: String
)

data class Plant(
    val plantName: String,
    val idgarden: Int,
    val nutrientsUsed: String,
    val harvestTime: String
)

data class NewsItem(
    val id: Int,
    val imageResId: Int,
    val title: String,
    val link: String
)

class DataSource(private val context: Context) {

    private val users = mutableListOf<User>()
    private var currentUser: User? = null
    private var nextIdGarden = 3

    // Gantilah ini menjadi state list yang reaktif
    val hydroponicStateList: SnapshotStateList<HydroponicGarden> = mutableStateListOf()

    init {
        val user1 = User(
            email = context.getString(R.string.user_email),
            password = context.getString(R.string.user_password),
            name = context.getString(R.string.user_name),
            phoneNumber = context.getString(R.string.user_phone),
            address = context.getString(R.string.user_address),
            idgarden = context.getString(R.string.idgarden0).toInt()
        )
        val user2 = User(
            email = context.getString(R.string.user_email1),
            password = context.getString(R.string.user_password1),
            name = context.getString(R.string.user_name1),
            phoneNumber = context.getString(R.string.user_phone1),
            address = context.getString(R.string.user_address1),
            idgarden = context.getString(R.string.idgarden3).toInt()
        )

        users.add(user1)
        users.add(user2)

        hydroponicStateList.addAll(
            listOf(
                HydroponicGarden(
                    idgarden = 1,
                    gardenName = context.getString(R.string.garden1_name),
                    gardenSize = context.getString(R.string.garden1_size).toDouble(),
                    plants = listOf(
                        Plant(
                            plantName = context.getString(R.string.plant1_type),
                            idgarden = 1,
                            nutrientsUsed = context.getString(R.string.plant1_nutrients),
                            harvestTime = context.getString(R.string.plant1_harvest_time)
                        ),
                        Plant(
                            plantName = context.getString(R.string.plant2_type),
                            idgarden = 1,
                            nutrientsUsed = context.getString(R.string.plant2_nutrients),
                            harvestTime = context.getString(R.string.plant2_harvest_time)
                        )
                    ),
                    hydroponicType = context.getString(R.string.garden1_hydroponic_type)
                ),
                HydroponicGarden(
                    idgarden = 2,
                    gardenName = context.getString(R.string.garden_name),
                    gardenSize = context.getString(R.string.garden_size).toDouble(),
                    plants = listOf(
                        Plant(
                            plantName = context.getString(R.string.plant3_type),
                            idgarden = 2,
                            nutrientsUsed = context.getString(R.string.plant3_nutrients),
                            harvestTime = context.getString(R.string.plant3_harvest_time)
                        )
                    ),
                    hydroponicType = context.getString(R.string.garden_hydroponic_type)
                )
            )
        )
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: DataSource? = null

        fun getInstance(context: Context): DataSource {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: DataSource(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    fun registerUser(user: User) {
        users.add(user)
        currentUser = user
    }

    fun generateNewGardenId(): Int = nextIdGarden++

    fun getCurrentUser(): User? = currentUser

    fun checkUser(email: String, password: String): Boolean {
        val foundUser = users.find { it.email == email && it.password == password }
        return if (foundUser != null) {
            currentUser = foundUser
            true
        } else {
            false
        }
    }

    fun getAllUsers(): List<User> = users

    fun getKebunById(kebunId: Int): HydroponicGarden? {
        return hydroponicStateList.find { it.idgarden == kebunId }
    }

    fun getKebunByName(name: String): HydroponicGarden? {
        return hydroponicStateList.find { it.gardenName == name }
    }

    fun updateKebun(updatedGarden: HydroponicGarden) {
        val index = hydroponicStateList.indexOfFirst { it.idgarden == updatedGarden.idgarden }
        if (index != -1) {
            hydroponicStateList[index] = updatedGarden
        }
    }

    fun deleteKebun(id: Int) {
        hydroponicStateList.removeAll { it.idgarden == id }
    }

    fun addPlantToGarden(gardenId: Int, newPlant: Plant) {
        val index = hydroponicStateList.indexOfFirst { it.idgarden == gardenId }
        if (index != -1) {
            val garden = hydroponicStateList[index]
            val updatedGarden = garden.copy(plants = garden.plants + newPlant)
            hydroponicStateList[index] = updatedGarden
        }
    }

    fun deletePlant(plantName: String) {
        for ((index, garden) in hydroponicStateList.withIndex()) {
            val updatedPlants = garden.plants.filter { it.plantName != plantName }
            if (updatedPlants.size != garden.plants.size) {
                hydroponicStateList[index] = garden.copy(plants = updatedPlants)
            }
        }
    }

    fun loadPlantData(): List<Plant> {
        return hydroponicStateList.flatMap { it.plants }
    }

    fun loadPlants(): List<Plant> = loadPlantData()

    fun loadNewsData(): List<NewsItem> {
        return listOf(
            NewsItem(
                id = 1,
                imageResId = R.drawable.news_image1,
                title = context.getString(R.string.news1_title),
                link = context.getString(R.string.news1_link)
            ),
            NewsItem(
                id = 2,
                imageResId = R.drawable.news_image2,
                title = context.getString(R.string.news2_title),
                link = context.getString(R.string.news2_link)
            )
        )
    }
}
