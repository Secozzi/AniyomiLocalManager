package xyz.secozzi.aniyomilocalmanager.presentation.compontents.cover

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.size.Size
import xyz.secozzi.aniyomilocalmanager.R
import xyz.secozzi.aniyomilocalmanager.data.cover.CoverData
import xyz.secozzi.aniyomilocalmanager.ui.theme.spacing

@Composable
fun CoverResultItem(
    coverData: CoverData,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.extraSmall)
            .selectedOutline(
                isSelected = selected,
                color = MaterialTheme.colorScheme.secondary,
            )
            .padding(4.dp)
            .combinedClickable(
                onClick = onClick,
            )
    ) {
        val contentColor = if (selected) {
            MaterialTheme.colorScheme.onSecondary
        } else {
            LocalContentColor.current
        }
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            Column {
                val loadingText = stringResource(R.string.cover_loading_image_placeholder)

                var titleText by remember { mutableStateOf(loadingText) }
                var sizeText by remember { mutableStateOf("") }

                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(coverData.coverUrl)
                        .size(Size.ORIGINAL)
                        .listener(
                            onSuccess = { _, result ->
                                titleText = coverData.origin
                                sizeText = "${result.image.width}x${result.image.height}"
                            }
                        )
                        .build(),
                    placeholder = ColorPainter(Color(0x1F888888)),
                    contentDescription = "",
                    modifier = Modifier
                        .fillMaxSize()
                        .aspectRatio(2f / 3f)
                        .clip(MaterialTheme.shapes.extraSmall),
                    contentScale = ContentScale.Crop,
                )

                Text(
                    text = titleText,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    minLines = 1,
                    maxLines = 1,
                    style = MaterialTheme.typography.titleSmall,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(),
                )

                Text(
                    text = sizeText,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    minLines = 1,
                    maxLines = 1,
                    style = MaterialTheme.typography.titleSmall,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

private fun Modifier.selectedOutline(
    isSelected: Boolean,
    color: Color,
) = drawBehind { if (isSelected) drawRect(color = color) }
