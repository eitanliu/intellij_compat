package com.eitanliu.intellij.compat.extensions

import com.eitanliu.intellij.compat.application.EDTCompat
import com.intellij.openapi.application.Application
import com.intellij.openapi.application.EDT
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.util.*
import kotlin.concurrent.schedule
import kotlin.coroutines.CoroutineContext

private val edtContext: CoroutineContext = try {
    // com.intellij.openapi.application.CoroutinesKt.getEDT(Dispatchers)
    Dispatchers.EDT
} catch (e: Throwable) {
    Dispatchers.EDTCompat
}

// val ApplicationScope = MainScope()
val ApplicationScope = CoroutineScope(edtContext)

fun Application.invokeLater(delay: Long, runnable: Runnable) {
    Timer().schedule(delay) { invokeLater(runnable) }
}