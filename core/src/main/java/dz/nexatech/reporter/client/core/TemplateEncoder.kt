package dz.nexatech.reporter.client.core

import dz.nexatech.reporter.client.common.AbstractLocalizer
import dz.nexatech.reporter.client.common.FilesExtension
import dz.nexatech.reporter.client.common.MimeType
import dz.nexatech.reporter.client.common.readAsBytes
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.LinkedList
import java.util.Properties
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

object TemplateEncoder {

    private const val TEMPLATE_INFO_EXTENSION = ".${FilesExtension.PROPERTIES}"

    fun readZipInput(input: InputStream, localizer: AbstractLocalizer): Pair<List<AbstractTemplate>, List<AbstractBinaryResource>> {
        ZipInputStream(input).use { zipStream ->
            val templates = LinkedList<AbstractTemplate>()
            val resources = LinkedList<AbstractBinaryResource>()
            var entry: ZipEntry? = zipStream.nextEntry

            while (entry != null) {
                val entryName = entry.name
                if (entry.isDirectory.not()) {
                    if (entryName.endsWith(TEMPLATE_INFO_EXTENSION)) {
                        val properties = Properties()
                        properties.load(
                            ByteArrayInputStream(
                                zipStream.readAsBytes(
                                    entry.size.toInt(),
                                    false
                                )
                            )
                        )
                        val templateName: String? =
                            properties.getProperty(TEMPLATE_COLUMN_NAME)
                        if (templateName != null) {
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
                                    lastUpdate = System.currentTimeMillis(),
                                    localizer = localizer,
                                )
                            )
                        }
                    } else {
                        resources.add(
                            SimpleBinaryResource(
                                path = entryName,
                                mimeType = MimeType.of(entryName),
                                data = zipStream.readAsBytes(entry.size.toInt(), false),
                                lastModified = System.currentTimeMillis(),
                            )
                        )
                    }
                }
                entry = zipStream.nextEntry
            }


            return Pair(templates, resources)
        }
    }
}