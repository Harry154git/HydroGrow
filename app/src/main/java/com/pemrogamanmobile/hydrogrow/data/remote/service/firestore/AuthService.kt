package com.pemrogamanmobile.hydrogrow.data.remote.service.firestore

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthService @Inject constructor(
    private val auth: FirebaseAuth,
    private val crashlytics: FirebaseCrashlytics
) {
    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    fun getAuthStateFlow(): Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser)
        }
        auth.addAuthStateListener(listener)
        awaitClose {
            auth.removeAuthStateListener(listener)
        }
    }


    suspend fun signInWithCredential(credential: AuthCredential): FirebaseUser? {
        return try {
            auth.signInWithCredential(credential).await().user
        } catch (e: Exception) {
            crashlytics.log("Error : ${e.message}")
            crashlytics.recordException(e)
            throw e
        }
    }

    suspend fun signOut() {
        auth.signOut()
    }
}