package dz.nexatech.reporter.client.model

import com.google.common.collect.ImmutableMap
import dz.nexatech.reporter.common.ioLaunch
import dagger.Lazy
import io.pebbletemplates.pebble.PebbleEngine
import io.pebbletemplates.pebble.template.PebbleTemplate
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.InputStream
import javax.inject.Inject

class TemplatesRepository @Inject constructor(
    private val resourcesRepository: ResourcesRepository,
    private val templatesDao: Lazy<TemplatesDAO>,
) {
    private val mTemplates: MutableStateFlow<ImmutableMap<String, Template>?> = MutableStateFlow(null)
    val templates = mTemplates.asStateFlow()
    private val firstRefreshJob: Job = ioLaunch { refresh() }

    val pebbleEngine: PebbleEngine = PebbleEngine.Builder()
        .loader(TemplateLoader(this))
        .build()

    private suspend fun refresh() {
        val builder: ImmutableMap.Builder<String, Template> = ImmutableMap.builder()
        for (template in templatesDao.get().loadTemplates()) {
            builder.put(template.name, template)
        }
        val result = builder.build()
        mTemplates.value = result
    }

    suspend fun loadedTemplates(): ImmutableMap<String, Template> {
        firstRefreshJob.join()
        return templates.value!!
    }

    suspend fun templateExists(templateName: String): Boolean =
        loadedTemplates().containsKey(templateName)

    suspend fun loadTemplateContent(templateName: String): InputStream? =
        loadTemplateFile(templateName, "html")

    suspend fun loadTemplateMeta(templateName: String): InputStream? {
        resourcesRepository.clearCache()
        return loadTemplateFile(templateName, "json")
    }

    private suspend fun loadTemplateFile(templateName: String, fileFormat: String): InputStream? =
        resourcesRepository.loadWithoutCache("$TEMPLATE_RESOURCE_PREFIX$templateName.$fileFormat")?.asInputStream()

    fun compileTemplateBlocking(templateName: String): PebbleTemplate =
        pebbleEngine.getTemplate(templateName)

    suspend fun updateTemplates(templates: List<Template>, resources: List<Resource>) {
        templatesDao.get().updateAll(templates)
        resourcesRepository.updateResources(resources)
        firstRefreshJob.join()
        refresh()
    }
}