package dz.nexatech.reporter.client.model

import android.annotation.SuppressLint
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.google.common.collect.ImmutableMap
import com.google.common.collect.ImmutableSet
import dagger.Lazy
import dz.nexatech.reporter.client.common.ioLaunch
import dz.nexatech.reporter.client.common.mapToSet
import dz.nexatech.reporter.client.common.withIO
import dz.nexatech.reporter.client.common.withMain
import dz.nexatech.reporter.client.core.ReporterExtension
import dz.nexatech.reporter.client.core.TemplateLoader
import dz.nexatech.reporter.util.model.Teller
import io.pebbletemplates.pebble.PebbleEngine
import io.pebbletemplates.pebble.template.PebbleTemplate
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import java.io.InputStream
import java.util.function.Function
import java.util.function.Predicate
import javax.inject.Inject

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

    @Volatile
    private var pebbleEngine: PebbleEngine? = null

    @SuppressLint("MutableCollectionMutableState")
    val templates: MutableState<ImmutableMap<String, Template>?> = mutableStateOf(null)

    @SuppressLint("MutableCollectionMutableState")
    val fontFamilies: MutableState<ImmutableSet<String>> =
        mutableStateOf(fontAssetsResources.keys.mapToSet(::fontFamilyName))
    private val firstRefreshJob: Job = ioLaunch { refresh() }

    private suspend fun refresh() {
        val builder: ImmutableMap.Builder<String, Template> = ImmutableMap.builder()
        for (template in templatesDao.get().loadTemplates()) {
            builder.put(template.name, template)
        }
        val loadedTemplates = builder.build()
        val availableFonts = resourcesRepository.loadFontFamilies()

        withMain {
            templates.value = loadedTemplates
            fontFamilies.value = availableFonts
            pebbleEngine = null
        }
    }

    suspend fun loadedTemplates(): ImmutableMap<String, Template> {
        firstRefreshJob.join()
        return templates.value ?: ImmutableMap.of()
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
        resourcesRepository.loadWithoutCache("$templateName.$fileFormat")
            ?.asInputStream()

    fun compileTemplateBlocking(templateName: String): PebbleTemplate? {
        try {
            val currentEngine = pebbleEngine
            if (currentEngine == null) {
                val newEngine = PebbleEngine.Builder()
                    .newLineTrimming(false)
                    .autoEscaping(true)
                    .extension(ReporterExtension)
                    .loader(templateLoader)
                    .build()
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
        firstRefreshJob.join()
        templatesDao.get().updateAll(templates)
        resourcesRepository.updateResources(resources)
        refresh()
    }

    suspend fun deleteAllTemplates() {
        withMain {
            templates.value = null
        }
        withIO {
            templatesDao.get().deleteAll()
            resourcesRepository.deleteAll()
            refresh()
        }
    }
}