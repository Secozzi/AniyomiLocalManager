package xyz.secozzi.aniyomilocalmanager.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TopLoadingIndicator(
    isLoading: Boolean,
    contentPadding: PaddingValues,
) {
    AnimatedVisibility(
        visible = isLoading,
        modifier = Modifier.zIndex(1f),
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = tween(300),
        ) + fadeIn(tween(300)),
        exit = slideOutVertically(
            targetOffsetY = { -it },
            animationSpec = tween(300),
        ) + fadeOut(tween(300)),
    ) {
        Box(modifier = Modifier.padding(contentPadding).fillMaxWidth()) {
            ContainedLoadingIndicator(
                modifier = Modifier.align(Alignment.TopCenter),
            )
        }
    }
}
