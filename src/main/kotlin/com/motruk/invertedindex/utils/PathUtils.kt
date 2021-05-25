package com.motruk.invertedindex.utils

import java.nio.file.Path
import java.nio.file.Paths

object PathUtils {

    fun getResourcesPath(): Path {
        val projectDirAbsolutePath = Paths.get("").toAbsolutePath().toString()
        return Paths.get(projectDirAbsolutePath, "src/main/resources")
    }
}
