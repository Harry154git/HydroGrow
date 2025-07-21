package com.pemrogamanmobile.hydrogrow.presentation.viewmodel.onboardingpage

import androidx.lifecycle.ViewModel
import com.pemrogamanmobile.hydrogrow.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class TampilanViewModel @Inject constructor() : ViewModel() {
    val tipsImages = listOf(
        R.drawable.tips1,
        R.drawable.tips2,
        R.drawable.tips3,
        R.drawable.tips4,
        R.drawable.tips5,
        R.drawable.tips6,
        R.drawable.tips7,
        R.drawable.tips8,
        R.drawable.tips9,
        R.drawable.tips10
    )
}
