package xyz.secozzi.aniyomilocalmanager.utils

import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.measureTime

suspend fun <T> withMinimumDuration(
    minDuration: Duration,
    block: suspend () -> T,
): T {
    val result: T
    val elapsed = measureTime {
        result = block()
    }

    if (elapsed < minDuration) {
        delay(minDuration - elapsed)
    }

    return result
}
