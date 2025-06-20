package com.pemrogamanmobile.hydrogrow.hydroponicpage
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class HydroponicResult(
    val hydroponictype: String,
    val costestimation: String,
    val gardensize: Double
) : Parcelable

