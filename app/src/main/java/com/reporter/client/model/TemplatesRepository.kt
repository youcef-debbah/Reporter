package com.reporter.client.model

import android.content.Context
import com.google.common.collect.ImmutableMap
import com.reporter.common.ioLaunch
import com.reporter.util.model.Teller
import com.reporter.util.ui.AbstractApplication
import dagger.Lazy
import io.pebbletemplates.pebble.PebbleEngine
import io.pebbletemplates.pebble.template.PebbleTemplate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.CountDownLatch
import javax.inject.Inject

class TemplatesRepository @Inject constructor(private val templatesDao: Lazy<TemplatesDAO>) {

    val context: Context = AbstractApplication.INSTANCE

    @Volatile
    private var loadedTemplates: ImmutableMap<String, Template>? = null

    private val templatesLoadingLatch = CountDownLatch(1)

    val templates = MutableStateFlow<Map<String, Template>?>(null).apply {
        ioLaunch {
            val builder: ImmutableMap.Builder<String, Template> = ImmutableMap.builder()
            for (template in templatesDao.get().loadTemplates()) {
                builder.put(template.name, template)
            }
            val result = builder.build()
            value = result
            loadedTemplates = result
            templatesLoadingLatch.countDown()
        }
    }.asStateFlow()

    val pebbleEngine: PebbleEngine = PebbleEngine.Builder()
        .loader(TemplateLoader(this))
        .build()

    fun loadedTemplates(): ImmutableMap<String, Template> =
        loadedTemplates ?: run {
            templatesLoadingLatch.await()
            loadedTemplates!!
        }

    fun templateExists(templateName: String): Boolean =
        loadedTemplates().containsKey(templateName)

    fun loadTemplateContent(templateName: String): InputStream? =
        loadTemplateFile(templateName, "html")

    fun loadTemplateMeta(templateName: String): InputStream? =
        loadTemplateFile(templateName, "json")

    private fun loadTemplateFile(templateName: String, fileFormat: String): InputStream? {
        val file = "templates/$templateName.$fileFormat"
        try {
            return context.assets.open(file)
        } catch (e: IOException) {
            Teller.error("could not find template file: $file", e)
            return null
        }
    }

    fun compileTemplate(templateName: String): PebbleTemplate =
        pebbleEngine.getTemplate(templateName)
}