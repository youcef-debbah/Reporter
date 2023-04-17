package com.reporter.client.model

import com.google.common.collect.ImmutableMap
import com.reporter.common.ioLaunch
import dagger.Lazy
import io.pebbletemplates.pebble.PebbleEngine
import io.pebbletemplates.pebble.template.PebbleTemplate
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.InputStream
import javax.inject.Inject

class TemplatesRepository @Inject constructor(
    private val resourcesRepository: ResourcesRepository,
    private val templatesDao: Lazy<TemplatesDAO>,
) {
    private val templatesInitJob: Job
    val templates: StateFlow<ImmutableMap<String, Template>?>

    init {
        val mutableStateFlow: MutableStateFlow<ImmutableMap<String, Template>?> = MutableStateFlow(null)
        templatesInitJob = ioLaunch {
            val builder: ImmutableMap.Builder<String, Template> = ImmutableMap.builder()
            for (template in templatesDao.get().loadTemplates()) {
                builder.put(template.name, template)
            }
            val result = builder.build()
            mutableStateFlow.value = result
        }
        templates = mutableStateFlow.asStateFlow()
    }

    val pebbleEngine: PebbleEngine = PebbleEngine.Builder()
        .loader(TemplateLoader(this))
        .build()

    suspend fun loadedTemplates(): ImmutableMap<String, Template> {
        templatesInitJob.join()
        return templates.value!!
    }

    suspend fun templateExists(templateName: String): Boolean =
        loadedTemplates().containsKey(templateName)

    suspend fun loadTemplateContent(templateName: String): InputStream? =
        loadTemplateFile(templateName, "html")

    suspend fun loadTemplateMeta(templateName: String): InputStream? =
        loadTemplateFile(templateName, "json")

    private suspend fun loadTemplateFile(templateName: String, fileFormat: String): InputStream? =
        resourcesRepository.load("templates/$templateName.$fileFormat")?.asInputStream()

    fun compileTemplateBlocking(templateName: String): PebbleTemplate =
        pebbleEngine.getTemplate(templateName)
}