package dz.nexatech.reporter.client.core

import dz.nexatech.reporter.client.common.AbstractLocalizer
import dz.nexatech.reporter.client.common.FilesExtension
import dz.nexatech.reporter.client.common.MimeType
import dz.nexatech.reporter.client.common.readAsBytes
import dz.nexatech.reporter.client.common.loadProperties
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.LinkedList
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

object TemplateEncoder {

    private const val TEMPLATE_INFO_EXTENSION = ".${FilesExtension.PROPERTIES}"

    fun readZipInput(
        input: InputStream,
        newLocalizer: (String) -> AbstractLocalizer,
    ): Pair<List<AbstractTemplate>, List<AbstractBinaryResource>> {
        val templates = LinkedList<AbstractTemplate>()
        val resources = LinkedList<AbstractBinaryResource>()

        ZipInputStream(input).use { zipStream ->
            var entry: ZipEntry? = zipStream.nextEntry
            while (entry != null) {
                val entryName = entry.name
                if (entry.isDirectory.not()) {
                    val entryBytes = zipStream.readAsBytes(entry.size.toInt(), false)
                    if (entryName.endsWith(TEMPLATE_INFO_EXTENSION)) {
                        val properties = loadProperties(ByteArrayInputStream(entryBytes))
                        val templateName: String? = properties.getProperty(TEMPLATE_COLUMN_NAME)
                        if (templateName != null) {
                            val lang = properties.getProperty(TEMPLATE_COLUMN_LANG)
                            templates.add(
                                SimpleTemplate(
                                    templateName,
                                    label_en = properties.getProperty(
                                        TEMPLATE_COLUMN_LABEL_EN
                                    ),
                                    label_ar = properties.getProperty(
                                        TEMPLATE_COLUMN_LABEL_AR
                                    ),
                                    label_fr = properties.getProperty(
                                        TEMPLATE_COLUMN_LABEL_FR
                                    ),
                                    desc_en = properties.getProperty(
                                        TEMPLATE_COLUMN_DESC_EN
                                    ),
                                    desc_ar = properties.getProperty(
                                        TEMPLATE_COLUMN_DESC_AR
                                    ),
                                    desc_fr = properties.getProperty(
                                        TEMPLATE_COLUMN_DESC_FR
                                    ),
                                    lang = lang,
                                    lastUpdate = System.currentTimeMillis(),
                                    localizer = newLocalizer(lang),
                                )
                            )
                        }
                    } else {
                        resources.add(
                            SimpleBinaryResource(
                                path = entryName,
                                mimeType = MimeType.of(entryName),
                                data = entryBytes,
                                lastModified = System.currentTimeMillis(),
                            )
                        )
                    }
                }
                entry = zipStream.nextEntry
            }
        }

        return Pair(templates, resources)
    }
}