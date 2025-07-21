package com.pemrogamanmobile.hydrogrow.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.pemrogamanmobile.hydrogrow.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("settings")

class PreferenceManager(private val context: Context) {

    companion object {
        // Kunci untuk Onboarding
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        // Kunci baru untuk data user
        val USER_UID = stringPreferencesKey("user_uid")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_PHOTO_URL = stringPreferencesKey("user_photo_url")
        val USER_EMAIL = stringPreferencesKey("user_email")
    }

    // --- Onboarding Flow ---
    val onboardingCompletedFlow: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[ONBOARDING_COMPLETED] ?: false }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { prefs -> prefs[ONBOARDING_COMPLETED] = completed }
    }

    // --- User Data Flow ---
    // Menggabungkan beberapa flow menjadi satu flow User
    val cachedUserFlow: Flow<User?> = combine(
        context.dataStore.data.map { it[USER_UID] },
        context.dataStore.data.map { it[USER_NAME] },
        context.dataStore.data.map { it[USER_EMAIL] },
        context.dataStore.data.map { it[USER_PHOTO_URL] }
    ) { uid, name, email, photoUrl ->
        if (uid != null) {
            User(uid = uid, name = name, email = email, photoUrl = photoUrl)
        } else {
            null
        }
    }

    // Fungsi untuk menyimpan data user ke DataStore
    suspend fun saveUserData(user: User) {
        context.dataStore.edit { prefs ->
            prefs[USER_UID] = user.uid
            prefs[USER_NAME] = user.name ?: ""
            prefs[USER_EMAIL] = user.email ?: ""
            prefs[USER_PHOTO_URL] = user.photoUrl ?: ""
        }
    }

    // Fungsi untuk menghapus data user saat logout
    suspend fun clearUserData() {
        context.dataStore.edit { prefs ->
            prefs.remove(USER_UID)
            prefs.remove(USER_NAME)
            prefs.remove(USER_EMAIL)
            prefs.remove(USER_PHOTO_URL)
        }
    }
}