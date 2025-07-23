plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.devtools.ksp)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    id("com.google.gms.google-services")
    alias(libs.plugins.google.firebase.crashlytics)
}

android {
    namespace = "com.pemrogamanmobile.hydrogrow"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.pemrogamanmobile.hydrogrow"
        minSdk = 26
        targetSdk = 35
        versionCode = 23072504
        versionName = "1.1.3.3"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // UI & Compose
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.foundation.android) // Cukup satu kali
    implementation(libs.coil.compose)

    // Navigation
    implementation(libs.androidx.navigation.runtime.android)
    implementation(libs.androidx.navigation.compose.android)

    // Firebase (BoM di atas)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.storage.ktx)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)

    // Google Sign-In -> WAJIB DITAMBAHKAN
    implementation("com.google.android.gms:play-services-auth:21.2.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.1")

    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // Dependency Injection - Hilt (menggunakan KSP)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler) // <-- DIUBAH dari kapt
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.hilt.work)

    // Local Storage - Room (menggunakan KSP) & DataStore
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler) // <-- DIUBAH dari kapt
    implementation(libs.androidx.datastore.preferences)

    // Networking & JSON
    implementation(libs.retrofit)
    implementation(libs.okhttp)
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation(libs.kotlinx.serialization.json) // Cukup satu kali
    implementation(libs.retrofit2.kotlinx.serialization.converter)
    implementation(libs.converter.gson)

    // Lifecycle & ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // Background Work
    implementation ("androidx.work:work-runtime-ktx:2.10.2")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.firebase.messaging.ktx)

    implementation("androidx.compose.material:material-icons-extended-android:1.6.8")

}