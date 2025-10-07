package xyz.secozzi.aniyomilocalmanager.presentation.utils

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.ui.graphics.TransformOrigin

val slideHorizontal =
    slideInHorizontally(tween(220)) { it / 2 } togetherWith slideOutHorizontally(tween(220)) { -it / 2 }

val predictiveHorizonal =
    (
        fadeIn(animationSpec = tween(220)) +
            scaleIn(
                animationSpec = tween(220, delayMillis = 30),
                initialScale = .9f,
                TransformOrigin(-1f, .5f),
            )
        ).togetherWith(
        fadeOut(animationSpec = tween(220)) +
            scaleOut(
                animationSpec = tween(220, delayMillis = 30),
                targetScale = .9f,
                TransformOrigin(-1f, .5f),
            ),
    )
