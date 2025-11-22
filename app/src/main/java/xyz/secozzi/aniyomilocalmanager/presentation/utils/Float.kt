package xyz.secozzi.aniyomilocalmanager.presentation.utils

import kotlin.math.ceil
import kotlin.math.floor

fun Float.toDisplayString(): String {
    return if (ceil(this) == floor(this)) {
        this.toInt().toString()
    } else {
        this.toString()
    }
}
