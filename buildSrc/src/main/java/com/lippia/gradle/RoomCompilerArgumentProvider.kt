package com.lippia.gradle

import org.gradle.api.tasks.OutputDirectory
import org.gradle.process.CommandLineArgumentProvider
import java.io.File

class RoomCompilerArgumentProvider(schemaLocation: String) : CommandLineArgumentProvider {

    private val argument = "-Aroom.schemaLocation=${schemaLocation}"

    @get:OutputDirectory
    val schemaLocationDirectory = File(schemaLocation)

    override fun asArguments(): MutableIterable<String> = mutableListOf<String>(argument)
}