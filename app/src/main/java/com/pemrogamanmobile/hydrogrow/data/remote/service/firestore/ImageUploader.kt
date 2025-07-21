package com.pemrogamanmobile.hydrogrow.data.remote.service.firestore

import android.net.Uri
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import android.util.Log

class ImageUploader @Inject constructor() {

    /**
     * Mengunggah gambar ke Cloud Storage di dalam folder yang ditentukan.
     * @param uri Alamat file gambar di perangkat.
     * @param folderName Nama folder di Cloud Storage (contoh: "gardens", "plants", "posts").
     * @return URL download dari gambar yang telah diunggah.
     */
    suspend fun uploadImageToStorage(uri: Uri, folderName: String): String {
        val storageRef = Firebase.storage.reference
        // Nama file sekarang dinamis berdasarkan folderName
        val fileName = "$folderName/${UUID.randomUUID()}.jpg"
        val imageRef = storageRef.child(fileName)

        imageRef.putFile(uri).await()

        return imageRef.downloadUrl.await().toString()
    }
}
