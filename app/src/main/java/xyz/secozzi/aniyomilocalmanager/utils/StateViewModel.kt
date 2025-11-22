package xyz.secozzi.aniyomilocalmanager.utils

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class StateViewModel<T>(initial: T) : ViewModel() {
    protected val mutableState = MutableStateFlow(initial)
    val state = mutableState.asStateFlow()
}
