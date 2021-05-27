package com.motruk.invertedindex.data

import com.motruk.invertedindex.entity.WordLocation
import com.motruk.invertedindex.utils.PathUtils
import com.motruk.invertedindex.utils.Utils
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors

class DataSourceImpl(
    private val subscribeOn: Scheduler = Schedulers.computation(),
    private val observeOn: Scheduler = Schedulers.from(Executors.newFixedThreadPool(1))
    ) : DataSource {

    private val subscriptions: CompositeDisposable = CompositeDisposable()
    private val invIndex = ConcurrentHashMap<String, MutableList<WordLocation>>()
    private lateinit var fileNames: MutableCollection<String>
    private var wasIndexed = false

    init {
        startIndexing()
    }

    private fun startIndexing() {
        val resourcesPath = PathUtils.getResourcesPath()
        val fileList = resourcesPath.toFile().walkTopDown()
            .filter { it.isFile }
            .filter { it.name.endsWith(".txt") }

        val size = fileList.toMutableList().size
        fileNames = Collections.synchronizedCollection(ArrayList(size))
        fileList.forEach { file ->
            subscriptions.add(
                Single.fromCallable {
                    Utils.indexFile(file, invIndex, fileNames, size)
                }.subscribeOn(subscribeOn)
                    .filter { it }
                    .observeOn(observeOn)
                    .subscribe({
                        wasIndexed = it
                    }, Utils::handleIndexingError)
            )
        }
    }


    override fun findData(search: String): String {
        if (!wasIndexed) return "Try again later"
        val splitedUserInput = search.split(Utils.splitter)
        var result = ""
        splitedUserInput.forEach {
            result += Utils.findWord(it, invIndex)
            result += "\n"
            println(result)
        }
        return result
    }

    override fun dispose() {
        subscriptions.dispose()
    }


}