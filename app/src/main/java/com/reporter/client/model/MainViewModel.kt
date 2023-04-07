package com.reporter.client.model

import androidx.lifecycle.ViewModel
import dagger.Lazy
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val templatesRepository: TemplatesRepository,
    private val valueDAO: Lazy<ValueDAO>,
): ViewModel() {
    suspend fun newTemplateCache(templateName: String, meta: TemplateMeta) =
        TemplateCache.from(templateName, meta, valueDAO)
}