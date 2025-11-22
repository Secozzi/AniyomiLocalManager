package xyz.secozzi.aniyomilocalmanager.utils

fun <T> List<T>.copyAt(index: Int, func: (T) -> T): List<T> {
    return this.mapIndexed { i, v ->
        if (i == index) {
            func(v)
        } else {
            v
        }
    }
}
