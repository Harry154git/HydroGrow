package com.pemrogamanmobile.hydrogrow.data.remote.service.firestore

import android.net.Uri
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class ImageUploader @Inject constructor() {

    /**
     * Mengunggah gambar ke Cloud Storage di dalam folder spesifik pengguna.
     * Path akan menjadi: "folderName/userId/namaFile.jpg"
     * @param uri Alamat file gambar di perangkat.
     * @param folderName Nama folder utama di Cloud Storage (contoh: "gardens", "plants").
     * @param userId ID pengguna yang sedang login untuk membuat sub-folder.
     * @return URL download dari gambar yang telah diunggah.
     */
    // DIUBAH: Menambahkan parameter userId
    suspend fun uploadImageToStorage(uri: Uri, folderName: String, userId: String): String {
        // Validasi agar userId tidak kosong untuk mencegah penyimpanan di tempat yang salah
        require(userId.isNotEmpty()) { "User ID tidak boleh kosong untuk mengunggah gambar." }

        val storageRef = Firebase.storage.reference

        // DIUBAH: Path file sekarang menyertakan userId
        val fileName = "$folderName/$userId/${UUID.randomUUID()}.jpg"
        val imageRef = storageRef.child(fileName)

        imageRef.putFile(uri).await()

        return imageRef.downloadUrl.await().toString()
    }
}