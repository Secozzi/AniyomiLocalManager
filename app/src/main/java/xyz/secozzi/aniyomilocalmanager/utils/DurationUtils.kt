package xyz.secozzi.aniyomilocalmanager.utils

import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.measureTime

suspend fun <T> withMinimumDuration(
    minDuration: Duration,
    onLess: Duration = Duration.INFINITE,
    block: suspend () -> T,
): T {
    val result: T
    val elapsed = measureTime {
        result = block()
    }

    if (elapsed < onLess) {
        return result
    }

    if (elapsed < minDuration) {
        delay(minDuration - elapsed)
    }

    return result
}
