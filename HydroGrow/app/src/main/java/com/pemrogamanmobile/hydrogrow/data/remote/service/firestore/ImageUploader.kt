package com.pemrogamanmobile.hydrogrow.data.remote.service.firestore

import android.net.Uri
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import android.util.Log

class ImageUploader @Inject constructor() {

    suspend fun uploadImageToStorage(uri: Uri): String {
        // 👇 Tambahkan log ini di sini
        Log.d("ImageUploader", "Uploading from uri: $uri")

        val storageRef = Firebase.storage.reference
        val fileName = "gardens/${UUID.randomUUID()}.jpg"
        val imageRef = storageRef.child(fileName)

        imageRef.putFile(uri).await()

        return imageRef.downloadUrl.await().toString()
    }
}
