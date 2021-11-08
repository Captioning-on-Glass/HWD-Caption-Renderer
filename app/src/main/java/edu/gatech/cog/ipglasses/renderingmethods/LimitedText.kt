package edu.gatech.cog.ipglasses.renderingmethods

import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import kotlin.math.roundToInt

/**
 * Renders only the `maxBottomLines` lines of a text sequence. For example, if a string occupies
 * 5 lines of space, and maxBottomLines is set to 3, this composable will show only the last 3 lines
 * of text.
 */
@Composable
fun LimitedText(
    text: String,
    maxBottomLines: Int,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    style: TextStyle = LocalTextStyle.current,
) {
    SubcomposeLayout(
        // prevent offset text from being drawn
        modifier.clipToBounds()
    ) { constraints ->
        var extraLinesHeight = 0
        val placeable = subcompose(null) {
            Text(
                text = text,
                color = color,
                fontSize = fontSize,
                fontStyle = fontStyle,
                fontWeight = fontWeight,
                fontFamily = fontFamily,
                letterSpacing = letterSpacing,
                textDecoration = textDecoration,
                textAlign = textAlign,
                lineHeight = lineHeight,
                overflow = overflow,
                softWrap = softWrap,
                onTextLayout = { textLayoutResult ->
                    val extraLines = textLayoutResult.lineCount - maxBottomLines
                    if (extraLines > 0) {
                        extraLinesHeight = textLayoutResult.getLineTop(extraLines).roundToInt()
                    }
                },
                style = style,
            )
        }[0].measure(
            // override maxWidth to get full text size
            constraints.copy(maxHeight = Int.MAX_VALUE)
        )
        layout(
            width = placeable.width,
            height = placeable.height - extraLinesHeight
        ) {
            placeable.place(0, -extraLinesHeight)
        }
    }
}