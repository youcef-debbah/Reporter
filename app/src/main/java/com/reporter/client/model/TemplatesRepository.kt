package com.reporter.client.model

import com.reporter.common.ioLaunch
import dagger.Lazy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class TemplatesRepository @Inject constructor(
    val templateDao: Lazy<TemplateDAO>,
    val valueDAO: Lazy<ValueDAO>,
) {
    val templates = MutableStateFlow<List<Template>?>(null).apply {
        ioLaunch {
            value = templateDao.get().loadTemplates()
        }
    }.asStateFlow()
}