package xyz.secozzi.aniyomilocalmanager.utils

import java.net.URLDecoder
import java.nio.charset.StandardCharsets

fun String.trimInfo(): String {
    var newString = this.trim().replaceFirst("""^\[\w+\] ?""".toRegex(), "")
    val regex = """( ?\[[\s\w-]+\]| ?\([\s\w-]+\))(\.mkv|\.mp4|\.avi)?${'$'}""".toRegex()

    while (regex.containsMatchIn(newString)) {
        newString = regex.replace(newString) { matchResult ->
            matchResult.groups[2]?.value ?: ""
        }
    }

    return newString.trim()
}

fun String.getDirectoryName(): String {
    return URLDecoder.decode(this, StandardCharsets.UTF_8.toString())
        .substringAfterLast("/")
        .trimInfo()
}
