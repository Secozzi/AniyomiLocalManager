package xyz.secozzi.aniyomilocalmanager.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
context(viewModel: ViewModel)
fun <R, T> Flow<T>.asResultFlow(
    idleResult: R,
    loadingResult: R,
    getErrorResult: (Throwable) -> R,
    context: CoroutineContext = Dispatchers.Unconfined,
    fetchData: suspend (T) -> R,
): StateFlow<R> = this.flatMapLatest { data ->
    flow {
        emit(loadingResult)
        try {
            val result = withContext(context) {
                fetchData(data)
            }
            emit(result)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            emit(getErrorResult(e))
        }
    }
}
    .stateIn(
        scope = viewModel.viewModelScope,
        started = SharingStarted.WhileSubscribed(5.seconds),
        initialValue = idleResult,
    )

@Composable
fun <T> CollectAsEffect(
    flow: SharedFlow<T>,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    block: suspend (T) -> Unit,
) {
    LaunchedEffect(Unit) {
        lifecycleOwner.repeatOnLifecycle(minActiveState) {
            withContext(Dispatchers.Main.immediate) {
                flow.collect { effect ->
                    block(effect)
                }
            }
        }
    }
}
