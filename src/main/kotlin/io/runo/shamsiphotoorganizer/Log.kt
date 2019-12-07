package io.runo.shamsiphotoorganizer

import org.tinylog.kotlin.Logger

fun info(log: String) {
    Logger.info(log)
}

fun error(log: Any) {
    Logger.error(log)
}