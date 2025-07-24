package com.pemrogamanmobile.hydrogrow.data.remote.service.firestore.firestoreservices

import com.google.firebase.firestore.FirebaseFirestore
import com.pemrogamanmobile.hydrogrow.data.remote.dto.OnboardingPreferencesDto
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class OnboardingFirestoreService @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    companion object {
        private const val USERS_COLLECTION = "users"
        private const val PREFERENCES_DOCUMENT = "onboarding_preferences"
    }

    suspend fun savePreferences(userId: String, preferencesDto: OnboardingPreferencesDto) {
        firestore.collection(USERS_COLLECTION)
            .document(userId)
            .collection(PREFERENCES_DOCUMENT)
            .document("user_prefs")
            .set(preferencesDto)
            .await()
    }

    suspend fun getPreferences(userId: String): OnboardingPreferencesDto? {
        return firestore.collection(USERS_COLLECTION)
            .document(userId)
            .collection(PREFERENCES_DOCUMENT)
            .document("user_prefs")
            .get()
            .await()
            .toObject(OnboardingPreferencesDto::class.java)
    }
}