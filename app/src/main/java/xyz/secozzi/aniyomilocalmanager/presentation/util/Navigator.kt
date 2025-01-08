package xyz.secozzi.aniyomilocalmanager.presentation.util

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import cafe.adriel.voyager.navigator.Navigator


interface Tab : cafe.adriel.voyager.navigator.tab.Tab {
    suspend fun onReselect(navigator: Navigator) {}
}

// For popping back with result

interface NavigatorResult

private val savedResult = mutableStateOf<NavigatorResult?>(
    null
)

fun Navigator.popWithResult(result: NavigatorResult) {
    savedResult.value = result
    pop()
}

@Composable
fun getResult(): State<NavigatorResult?> {
    val result = savedResult
    val resultState = remember(result) {
        derivedStateOf {
            result.value
        }
    }
    return resultState
}

fun Navigator.clearResults() {
    savedResult.value = null
}

// Workaround to pass pass large data between screens

private val screenResults = hashMapOf<String, Any?>()

fun <T: Any> Navigator.getScreenResult(key: String): T? {
    val result = screenResults[key]
    screenResults[key] = null

    return result as? T
}

fun Navigator.setScreenResult(key: String, result: Any) {
    screenResults[key] = result
}
