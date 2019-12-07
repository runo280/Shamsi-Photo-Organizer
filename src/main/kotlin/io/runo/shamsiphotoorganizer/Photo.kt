package io.runo.shamsiphotoorganizer

import java.io.File

data class Photo(val file: File) {
    lateinit var metadata: String
    lateinit var hour: String
    lateinit var minute: String
    lateinit var second: String

    fun hasDate(): Boolean = metadata.contains("Date/Time", true)
}