package xyz.secozzi.aniyomilocalmanager.presentation.utils

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith

val transitionSpec = (slideInHorizontally(initialOffsetX = { it }))
    .togetherWith(slideOutHorizontally(targetOffsetX = { -it / 4 }) + fadeOut())

val popSpec = (slideInHorizontally(initialOffsetX = { -it / 4 }) + fadeIn())
    .togetherWith(slideOutHorizontally(targetOffsetX = { it }))
