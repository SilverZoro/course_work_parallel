package com.motruk.invertedindex.entity

data class WordLocation(val fileName: String, val wordNum: Int) {
    override fun toString() = "{$fileName, word number $wordNum}"
}
