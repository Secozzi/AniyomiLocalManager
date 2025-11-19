package xyz.secozzi.aniyomilocalmanager.presentation.anime.episodes.edit.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.PreviewLightDark
import xyz.secozzi.aniyomilocalmanager.presentation.PreviewContent

@Composable
fun DateTextField(
    label: String,
    text: String,
    isError: Boolean,
    errorMessage: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val rawInput = remember(text) { text.filter { it.isDigit() }.take(14) }

    OutlinedTextField(
        value = rawInput,
        onValueChange = { newValue ->
            if (newValue.all { it.isDigit() }) {
                onTextChange(formatToIso(newValue.take(14)))
            }
        },
        label = { Text(text = label) },
        isError = isError,
        supportingText = { if (isError) Text(errorMessage) },
        visualTransformation = DateTimeVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        modifier = modifier,
    )
}

private fun formatToIso(raw: String): String {
    val padded = raw.take(14).padEnd(14, '0')

    return buildString {
        padded.indices.forEach { i ->
            append(padded[i])
            when (i) {
                3 -> append("-")
                5 -> append("-")
                7 -> append("T")
                9 -> append(":")
                11 -> append(":")
            }
        }
    }
}

private class DateTimeVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val input = text.text

        val formattedText = buildString {
            for (i in 0 until 14) {
                val char = input.getOrNull(i) ?: '0'
                append(char)

                when (i) {
                    3 -> append("-")
                    5 -> append("-")
                    7 -> append("T")
                    9 -> append(":")
                    11 -> append(":")
                }
            }
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 3) return offset
                if (offset <= 5) return offset + 1
                if (offset <= 7) return offset + 2
                if (offset <= 9) return offset + 3
                if (offset <= 11) return offset + 4
                if (offset <= 14) return offset + 5
                return formattedText.length
            }

            override fun transformedToOriginal(offset: Int): Int {
                val calculatedOffset = when {
                    offset <= 4 -> offset
                    offset <= 7 -> offset - 1
                    offset <= 10 -> offset - 2
                    offset <= 13 -> offset - 3
                    offset <= 16 -> offset - 4
                    offset <= 19 -> offset - 5
                    else -> 14
                }
                return calculatedOffset.coerceAtMost(input.length)
            }
        }

        return TransformedText(AnnotatedString(formattedText), offsetMapping)
    }
}

@Composable
@PreviewLightDark
private fun DateTextFieldPreview() {
    PreviewContent {
        DateTextField(
            label = "Date",
            text = "2023-00-00T00:00:00",
            isError = false,
            errorMessage = "",
            onTextChange = { },
        )
    }
}
