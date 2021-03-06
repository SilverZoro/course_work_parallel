package com.motruk.invertedindex.utils

import com.motruk.invertedindex.entity.WordLocation
import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentMap
import kotlin.collections.ArrayList

object Utils {
    val splitter = Regex("""[^a-zA-Z\-']+""")

    fun indexFile(
        file: File,
        invIndex: ConcurrentMap<String, MutableList<WordLocation>>,
        fileNames: MutableCollection<String>,
        size: Int
    ): Boolean {
        val fileName = file.absolutePath
        if (fileName in fileNames) {
            println("'$fileName' already indexed")
            return fileNames.size == size
        }
        file.forEachLine { line ->
            for ((i, w) in line.toLowerCase().split(splitter).withIndex()) {
                if (w.isBlank()) continue
                var locations = invIndex[w]
                if (locations == null) {
                    locations = Collections.synchronizedList(ArrayList())
                    invIndex[w] = locations

                }
                locations!!.add(WordLocation(fileName, i + 1))
            }
        }
        fileNames.add(fileName)
        return fileNames.size == size
    }


    fun findWord(word: String, invIndex: Map<String, MutableList<WordLocation>>): String {
        val w = word.toLowerCase().replace("[^a-zA-Z-']".toRegex(), "")
        val locations = invIndex[w]
        return locations?.map { "$it - $w" }?.joinToString("\n") ?: "\n'$word' not found"
    }


    fun handleIndexingError(throwable: Throwable) {
        println("Error - ${throwable.message}")
        println("Throwable - $throwable")
    }
}
