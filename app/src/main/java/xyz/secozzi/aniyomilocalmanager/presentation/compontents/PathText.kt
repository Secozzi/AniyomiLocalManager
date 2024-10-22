package xyz.secozzi.aniyomilocalmanager.presentation.compontents

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextOverflow
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun PathText(path: String) {
    val text = "/" + URLDecoder.decode(path, StandardCharsets.UTF_8.toString())
        .substringAfterLast("primary:")

    Text(
        text = text,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}
