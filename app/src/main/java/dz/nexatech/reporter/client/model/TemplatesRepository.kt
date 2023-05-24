package dz.nexatech.reporter.client.model

import androidx.compose.runtime.Stable
import com.google.common.collect.ImmutableMap
import dagger.Lazy
import dz.nexatech.reporter.client.common.ioLaunch
import dz.nexatech.reporter.client.core.TemplateLoader
import dz.nexatech.reporter.util.model.Teller
import io.pebbletemplates.pebble.PebbleEngine
import io.pebbletemplates.pebble.template.PebbleTemplate
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.runBlocking
import java.io.InputStream
import java.util.function.Function
import java.util.function.Predicate
import javax.inject.Inject

@Stable
class TemplatesRepository @Inject constructor(
    private val resourcesRepository: ResourcesRepository,
    private val templatesDao: Lazy<TemplatesDAO>,
) {

    private val loader: Function<String, InputStream?> = Function { templateName ->
        runBlocking {
            loadTemplateContent(templateName)
        }
    }
    private val checker: Predicate<String> = Predicate { templateName ->
        runBlocking {
            templateExists(templateName)
        }
    }
    private val templateLoader = TemplateLoader(loader, checker)
    private var pebbleEngine: PebbleEngine? = null

    private val mTemplates: MutableStateFlow<ImmutableMap<String, Template>?> =
        MutableStateFlow(null)
    private val firstRefreshJob: Job = ioLaunch { refresh() }
    val templates = mTemplates.asStateFlow()

    private suspend fun refresh() {
        val builder: ImmutableMap.Builder<String, Template> = ImmutableMap.builder()
        for (template in templatesDao.get().loadTemplates()) {
            builder.put(template.name, template)
        }
        val result = builder.build()
        mTemplates.value = result
        pebbleEngine = null
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
        resourcesRepository.loadWithoutCache("$TEMPLATE_RESOURCE_PREFIX$templateName.$fileFormat")
            ?.asInputStream()

    fun compileTemplateBlocking(templateName: String): PebbleTemplate? {
        try {
            val currentEngine = pebbleEngine
            if (currentEngine == null) {
                val newEngine = PebbleEngine.Builder().loader(templateLoader).build()
                pebbleEngine = newEngine
                return newEngine.getTemplate(templateName)
            } else {
                return currentEngine.getTemplate(templateName)
            }
        } catch (e: Exception) {
            Teller.error("error while parsing template: $templateName", e)
            return null
        }
    }

    suspend fun updateTemplates(templates: List<Template>, resources: List<Resource>?) {
        templatesDao.get().updateAll(templates)
        resourcesRepository.updateResources(resources)
        firstRefreshJob.join()
        refresh()
    }
}