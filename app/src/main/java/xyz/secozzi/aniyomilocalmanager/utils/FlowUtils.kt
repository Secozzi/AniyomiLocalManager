package xyz.secozzi.aniyomilocalmanager.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
context(viewModel: ViewModel)
fun <R, T> Flow<T>.asResultFlow(
    idleResult: R,
    loadingResult: R,
    getErrorResult: (Throwable) -> R,
    fetchData: suspend (T) -> R,
): StateFlow<R> = this.flatMapLatest { data ->
    flow {
        emit(loadingResult)
        try {
            val result = fetchData(data)
            emit(result)
        } catch (e: Exception) {
            emit(getErrorResult(e))
        }
    }
}
    .stateIn(
        scope = viewModel.viewModelScope,
        started = SharingStarted.WhileSubscribed(5.seconds),
        initialValue = idleResult,
    )
