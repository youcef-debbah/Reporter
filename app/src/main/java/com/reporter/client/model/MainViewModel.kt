package com.reporter.client.model

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val templatesRepository: TemplatesRepository,
): ViewModel()