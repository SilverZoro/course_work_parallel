package com.motruk.invertedindex.data

interface DataSource {
    fun findData(search: String): String
    fun dispose()
}