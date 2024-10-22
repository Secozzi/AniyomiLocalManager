package xyz.secozzi.aniyomilocalmanager.presentation

import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey

abstract class Screen : Screen {
    final override val key = uniqueScreenKey
}
